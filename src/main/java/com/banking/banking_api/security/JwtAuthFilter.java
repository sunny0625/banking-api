package com.banking.banking_api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // ── Step 1: Read the Authorization header ──────────────────────────
        // Every secured request must carry:  Authorization: Bearer <token>
        // If the header is missing or doesn't start with "Bearer", skip this
        // filter entirely and let Spring Security reject it downstream.
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // ── Step 2: Extract the JWT from the header ─────────────────────────
        // "Bearer " is 7 characters — substring(7) gives us the raw token.
        final String jwt = authHeader.substring(7);

        // ── Step 3: Extract the username from the token ─────────────────────
        // JwtUtil parses the token and reads the "sub" (subject) claim,
        // which is the username we stored when we generated the token.
        final String username;
        try {
            username = jwtUtil.extractUsername(jwt);
        } catch (Exception e) {
            // Token is malformed or has an invalid signature — reject silently.
            // Do NOT expose the error reason in the response (security best practice).
            filterChain.doFilter(request, response);
            return;
        }

        // ── Step 4: Authenticate — but only if not already authenticated ────
        // SecurityContextHolder holds the current user for this request.
        // If it's already set (e.g. by a previous filter), don't overwrite it.
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Load full user details (roles, enabled status, etc.) from DB
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // ── Step 5: Validate the token against the loaded user ──────────
            // This checks two things:
            //   a) The username in the token matches the one in the DB
            //   b) The token has not expired
            if (jwtUtil.validateToken(jwt, userDetails)) {

                // ── Step 6: Build a Spring Security authentication object ───
                // UsernamePasswordAuthenticationToken is the standard way to
                // tell Spring Security "this user is authenticated".
                // Constructor: (principal, credentials, authorities)
                //   principal   = the UserDetails object
                //   credentials = null (we don't need the password anymore)
                //   authorities = the user's roles (e.g. ROLE_USER, ROLE_ADMIN)
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                // Attach request metadata (IP address, session ID) to the token.
                // Spring uses this for audit logging and session management.
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // ── Step 7: Store in SecurityContext ────────────────────────
                // This is the key step. Once set here, Spring Security treats
                // this request as authenticated for its entire lifecycle.
                // Any @PreAuthorize or .anyRequest().authenticated() check
                // will pass from this point forward.
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // ── Step 8: Always continue the filter chain ────────────────────────
        // Whether authentication succeeded or failed, we must call this.
        // If authentication failed, SecurityContext remains empty and Spring
        // Security will return 401 Unauthorized automatically.
        filterChain.doFilter(request, response);
    }
}
