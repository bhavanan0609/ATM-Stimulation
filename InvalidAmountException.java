/** Thrown when an amount is zero, negative, or breaks a transaction rule. */
public class InvalidAmountException extends Exception {
    public InvalidAmountException(String message) {
        super(message);
    }
}
