/**
 * CONCEPT 1 - ENCAPSULATION
 * The balance is private. The only way to change it is through deposit()
 * and withdraw(), which enforce the rules. No other class can set it directly.
 */
public class Account {
    private static final double MAX_DEPOSIT = 100_000;
    private static final double MAX_WITHDRAWAL = 20_000;

    private final String holderName;
    private double balance;

    public Account(String holderName, double openingBalance) {
        // CONCEPT 2 - CONDITIONAL STATEMENTS
        if (openingBalance < 0) {
            throw new IllegalArgumentException("Opening balance cannot be negative");
        }
        this.holderName = holderName;
        this.balance = openingBalance;
    }

    public String getHolderName() {
        return holderName;
    }

    public synchronized double getBalance() {
        return balance;
    }

    public synchronized void deposit(double amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException("Deposit must be greater than zero.");
        } else if (amount > MAX_DEPOSIT) {
            throw new InvalidAmountException("Maximum deposit per transaction is " + String.format("%.2f", MAX_DEPOSIT) + ".");
        }
        balance += amount;
    }

    public synchronized void withdraw(double amount)
            throws InvalidAmountException, InsufficientFundsException {
        if (amount <= 0) {
            throw new InvalidAmountException("Withdrawal must be greater than zero.");
        } else if (amount > MAX_WITHDRAWAL) {
            throw new InvalidAmountException("Maximum withdrawal per transaction is " + String.format("%.2f", MAX_WITHDRAWAL) + ".");
        } else if (amount > balance) {
            throw new InsufficientFundsException(
                "Insufficient funds. Your balance is " + String.format("%.2f", balance) + ".");
        }
        balance -= amount;
    }
}
