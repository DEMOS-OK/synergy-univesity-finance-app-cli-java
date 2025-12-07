package main.java.financesapp.exception;

/**
 * Exception thrown when transaction data is invalid.
 */
public class InvalidTransactionException extends Exception {
    
    public InvalidTransactionException(String message) {
        super(message);
    }
    
    public InvalidTransactionException(String message, Throwable cause) {
        super(message, cause);
    }
}
