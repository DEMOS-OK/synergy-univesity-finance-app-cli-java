package main.java.financesapp.exception;

/**
 * Exception thrown when a transaction with specified ID is not found.
 */
public class TransactionNotFoundException extends Exception {
    
    public TransactionNotFoundException(long id) {
        super("Transaction with ID " + id + " not found");
    }
    
    public TransactionNotFoundException(String message) {
        super(message);
    }
}
