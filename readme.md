# First Project
## Banking API
Task and difficulty faced in between to create this project:

- Pom.xml configuration for springdoc AI something getting version issue solved by cursor.
- Need to create UserDetails class for doing implementation in further classes.

- Model -- done
- dto -- done
- repository -- done
- service -- done
- Controller -- error
	AccountController:
		1. `public ResponseEntity<Account> getAccount(@PathVariable String accountNumber) {}`
		Error: Missing return statement.
		2. `public ResponseEntity<Account> createAccount(@RequestBody AccountRequest req) {}`
		Error: Missing return statement.
		3. `public ResponseEntity<List<Account>> listAccounts() {}`
		Error: Missing return statement.

	AuthController:
		1. `public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest req) {
        // authenticate, generate token, return it
        }`
		Error: Missing return statement.
		2. `public ResponseEntity<String> register(@RequestBody RegisterRequest req) {
        // save user with encoded password
        }`
		Error: Cannot resolve symbol 'RegisterRequest'
		3. 
		```java
		public ResponseEntity<String> register(@RequestBody RegisterRequest req) {
        // save user with encoded password
        }
        ```
		Error: Missing return statement.

	TranscationController:
		```java
		@PostMapping("/transfer")
    @Operation(summary = "Transfer funds between accounts")
    public ResponseEntity<TransactionResponse> transfer(
            @Valid @RequestBody TransferRequest req) {}

    @GetMapping("/history/{accountNumber}")
    @Operation(summary = "Get transaction history")
    public ResponseEntity<List<Transaction>> history(
            @PathVariable String accountNumber) {}
```
    Error:
    1. Cannot resolve symbol 'TransactionResponse'
    2. Missing return statement
    3. Missing return statement

- security -- error
	- JwtUtil
		@Value("${jwt.secret}") private String secret;
    	@Value("${jwt.expiration}") private long 	zexpiration;
    	Error: Cannot find @interface method 'value()'
	- SecurityConfig
		'csrf()' is deprecated since version 6.1 and marked for removal 

## Project is testing stage

1. User create with credentials:
	URL: http://localhost:8080/swagger-ui/index.html#/Authentication/register
	- username: 'admin'
	- password: 'admin123'
	- role: 'ROLE_USER'

2. Login existing user:
	- username: 'admin'
	- password: 'admin123'

	```json
	{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTc3NDE2MjUyMywiZXhwIjoxNzc0MjQ4OTIzfQ.MjUyWlLgN7b_JvPh2Wo52wnsKgzxWu2ztppStJAZqFU",
  "message": "Login successful",
  "username": "admin"
}
	```

3. Create new bank account:
	Request:
		```json
		{
  "ownerName": "Rahul Sharma",
  "accountType": "SAVINGS"
}
		```
	```json
	{
  "id": 1,
  "accountNumber": "ACC-96EEF74D",
  "ownerName": "Rahul Sharma",
  "balance": 0,
  "accountType": "SAVINGS",
  "createdAt": "2026-03-22T12:27:40.464231"
}
	```

4. Created 2nd bank account number:
	Request:
	```json
	Body:
{
  "ownerName": "Priya Patel",
  "accountType": "CURRENT"
}
	```

	Response:
	```json
	{
  "id": 2,
  "accountNumber": "ACC-1899337E",
  "ownerName": "Priya Patel",
  "balance": 0,
  "accountType": "CURRENT",
  "createdAt": "2026-03-22T12:31:58.654685"
}
	```

5. Deposit to Account A:
	Request:
	```json
	accountNumber: "ACC-96EEF74D"
	amount: "10000"
	```

	Response:
	```json
	{
  "id": 1,
  "accountNumber": "ACC-96EEF74D",
  "ownerName": "Rahul Sharma",
  "balance": 10000,
  "accountType": "SAVINGS",
  "createdAt": "2026-03-22T12:27:40.464231"
}
	```

6. Get account details:
	Request:
		accountNumber: "ACC-96EEF74D"
	Response:
	```json
	{
  "id": 1,
  "accountNumber": "ACC-96EEF74D",
  "ownerName": "Rahul Sharma",
  "balance": 10000,
  "accountType": "SAVINGS",
  "createdAt": "2026-03-22T12:27:40.464231"
}
	```

7. Transfer from Account A to Account B:
	Request:
	```json
	{
  "fromAccount": "ACC-96EEF74D",
  "toAccount": "ACC-1899337E",
  "amount": 3000
}
	```

	Response:
	```json
	{
  "id": 1,
  "fromAccount": "ACC-96EEF74D",
  "toAccount": "ACC-1899337E",
  "amount": 3000,
  "type": "TRANSFER",
  "status": "SUCCESS",
  "description": "Transfer from ACC-96EEF74D to ACC-1899337E",
  "timestamp": "2026-03-22T12:59:18.987703"
}
	```
