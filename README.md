# ATM Simulation (Java)

A small full-stack ATM simulation: a Java backend with a REST API and a plain HTML/CSS/JavaScript front end. Built to practise three core Java concepts.

## Features

- Check balance
- Deposit money
- Withdraw money
- Clear error messages for invalid amounts and insufficient funds

## Concepts demonstrated

| Concept | Where |
|---|---|
| Encapsulation | `Account.java`: the balance is `private` and can only change through `deposit()` and `withdraw()` |
| Conditional statements | `Account.java`: `if / else if` checks for amount, limits and available balance |
| Exception handling | `InvalidAmountException`, `InsufficientFundsException`, and the `try / catch` in `AtmServer.java` that maps them to HTTP status codes |

## Project structure

```
atm-simulation-java/
├── src/
│   ├── Account.java
│   ├── AtmServer.java
│   ├── InsufficientFundsException.java
│   └── InvalidAmountException.java
├── test/
│   └── AccountTest.java
├── web/
│   └── index.html
├── .gitignore
├── LICENSE
└── README.md
```

## Requirements

- JDK 17 or newer (no external libraries)

## Run the app

```bash
javac -d out src/*.java
java -cp out AtmServer
```

Open http://localhost:8080 in your browser. The demo account starts with a balance of 1000.00.

## Run the tests

```bash
javac -d out src/*.java test/AccountTest.java
java -cp out AccountTest
```

## API

| Method | Endpoint | Body | Success | Errors |
|---|---|---|---|---|
| GET | `/api/balance` | none | 200 | none |
| POST | `/api/deposit` | `{"amount": 500}` | 200 | 400 invalid amount |
| POST | `/api/withdraw` | `{"amount": 200}` | 200 | 400 invalid amount, 409 insufficient funds |

Example response:

```json
{"holder":"Demo User","balance":1300.00,"message":"Withdrawal successful. Please take your cash."}
```

Example error:

```json
{"error":"Insufficient funds. Your balance is 1300.00."}
```

## Rules

- Amounts must be greater than zero
- Maximum deposit per transaction: 100,000.00
- Maximum withdrawal per transaction: 20,000.00
- A withdrawal cannot exceed the balance

## Ideas for next steps

- PIN login with a lockout after 3 wrong attempts
- Multiple accounts
- Server-side transaction history
- Migrate the backend to Spring Boot

## License

MIT. See [LICENSE](LICENSE).
