package main.java.financesapp;

import main.java.financesapp.repository.TransactionRepository;
import main.java.financesapp.service.TransactionService;
import main.java.financesapp.ui.ConsoleUI;

/**
 * Main entry point for FinancesApp.
 */
public class Main {
    
    private static final String TRANSACTIONS_FILE_PATH = "data/transactions.csv";
    
    public static void main(String[] args) {
        try {
            TransactionRepository repository = new TransactionRepository(TRANSACTIONS_FILE_PATH);
            TransactionService service = new TransactionService(repository);
            ConsoleUI ui = new ConsoleUI(service);
            
            ui.run();
            
        } catch (Exception e) {
            System.err.println("Critical error starting application: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
