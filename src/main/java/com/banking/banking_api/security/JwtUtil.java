package com.banking.banking_api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    // FIX: @Value works only when the field is NOT final.
    // The error "Cannot find @interface method 'value()'" happens when you
    // accidentally import the wrong @Value (e.g. lombok's). Make sure the
    // import is: org.springframework.beans.factory.annotation.Value
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    // ── Generate a signed JWT token for a username ────────────────────────
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // ── Extract username (subject) from token ─────────────────────────────
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // ── Extract expiration date from token ────────────────────────────────
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // ── Generic claim extractor ───────────────────────────────────────────
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // ── Validate token against a UserDetails object ───────────────────────
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    // ── Private helpers ───────────────────────────────────────────────────
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
