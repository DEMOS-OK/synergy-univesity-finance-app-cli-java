package main.java.financesapp.ui;

import main.java.financesapp.exception.TransactionNotFoundException;
import main.java.financesapp.model.Transaction;
import main.java.financesapp.service.TransactionService;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

/**
 * Console user interface for FinancesApp.
 */
public class ConsoleUI {
    
    private final TransactionService service;
    private final Scanner scanner;
    
    private static final DateTimeFormatter DATE_TIME_FORMATTER = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    public ConsoleUI(TransactionService service) {
        if (service == null) {
            throw new IllegalArgumentException("Service cannot be null");
        }
        this.service = service;
        this.scanner = new Scanner(System.in);
    }
    
    /**
     * Main application loop.
     */
    public void run() {
        boolean running = true;
        
        System.out.println("╔═══════════════════════════════════════╗");
        System.out.println("║     Welcome to FinancesApp!           ║");
        System.out.println("╚═══════════════════════════════════════╝");
        System.out.println();
        
        while (running) {
            displayMenu();
            
            try {
                int choice = readInt("Select action: ");
                
                switch (choice) {
                    case 1:
                        handleAddTransaction();
                        break;
                    case 2:
                        handleViewAllTransactions();
                        break;
                    case 3:
                        handleCalculateTotal();
                        break;
                    case 4:
                        handleFilterByCategory();
                        break;
                    case 5:
                        handleDeleteTransaction();
                        break;
                    case 0:
                        running = false;
                        System.out.println("Thank you for using FinancesApp! Goodbye!");
                        break;
                    default:
                        System.out.println("Error: invalid choice. Please select an action from the menu.");
                }
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                System.out.println("Please try again.");
            }
            
            if (running) {
                System.out.println();
                System.out.println("Press Enter to continue...");
                scanner.nextLine();
            }
        }
        
        scanner.close();
    }
    
    private void displayMenu() {
        System.out.println("╔═══════════════════════════════════════╗");
        System.out.println("║     FinancesApp - Main Menu           ║");
        System.out.println("╠═══════════════════════════════════════╣");
        System.out.println("║ 1. Add transaction                    ║");
        System.out.println("║ 2. View all transactions              ║");
        System.out.println("║ 3. Total sum of transactions          ║");
        System.out.println("║ 4. Find transactions by category      ║");
        System.out.println("║ 5. Delete transaction                 ║");
        System.out.println("║ 0. Exit                               ║");
        System.out.println("╚═══════════════════════════════════════╝");
    }
    
    private void handleAddTransaction() {
        System.out.println("\n=== Add Transaction ===");
        
        try {
            BigDecimal amount = readAmount();
            String category = readCategory();
            Transaction transaction = service.addTransaction(amount, category);
            
            System.out.println("\n✓ Transaction added successfully!");
            System.out.println("ID: " + transaction.getId());
            System.out.println("Amount: " + formatAmount(transaction.getAmount()));
            System.out.println("Category: " + transaction.getCategory());
            System.out.println("Date: " + transaction.getDateTime().format(DATE_TIME_FORMATTER));
            
        } catch (NumberFormatException e) {
            System.out.println("Error: invalid amount format. Please enter a number.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error saving transaction: " + e.getMessage());
            System.out.println("Please check file access permissions.");
        }
    }
    
    private void handleViewAllTransactions() {
        System.out.println("\n=== All Transactions ===");
        
        try {
            List<Transaction> transactions = service.getAllTransactions();
            
            if (transactions.isEmpty()) {
                System.out.println("No transactions yet. Add your first transaction through the menu.");
            } else {
                displayTransactions(transactions);
            }
        } catch (IOException e) {
            System.out.println("Error loading transactions: " + e.getMessage());
            System.out.println("Please check file access permissions.");
        }
    }
    
    private void handleCalculateTotal() {
        System.out.println("\n=== Total Sum ===");
        
        try {
            BigDecimal total = service.getTotalSum();
            System.out.println("Total sum of all transactions: " + formatAmount(total));
        } catch (IOException e) {
            System.out.println("Error loading transactions: " + e.getMessage());
            System.out.println("Please check file access permissions.");
        }
    }
    
    private void handleFilterByCategory() {
        System.out.println("\n=== Find Transactions by Category ===");
        
        try {
            String category = readCategory();
            List<Transaction> transactions = service.getTransactionsByCategory(category);
            
            if (transactions.isEmpty()) {
                System.out.println("No transactions found for category \"" + category + "\".");
            } else {
                System.out.println("\nTransactions in category \"" + category + "\":");
                displayTransactions(transactions);
                System.out.println("Found transactions: " + transactions.size());
            }
            
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error loading transactions: " + e.getMessage());
            System.out.println("Please check file access permissions.");
        }
    }
    
    private void handleDeleteTransaction() {
        System.out.println("\n=== Delete Transaction ===");
        
        try {
            List<Transaction> transactions = service.getAllTransactions();
            
            if (transactions.isEmpty()) {
                System.out.println("No transactions to delete.");
                return;
            }
            
            System.out.println("Transaction list:");
            displayTransactions(transactions);
            
            long id = readTransactionId();
            service.deleteTransaction(id);
            System.out.println("\n✓ Transaction with ID " + id + " deleted successfully!");
            
        } catch (TransactionNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error: invalid ID format. Please enter a number.");
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            System.out.println("Please check file access permissions.");
        }
    }
    
    private void displayTransactions(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return;
        }
        
        System.out.println("─────────────────────────────────────────");
        System.out.printf("%-4s | %-12s | %-15s | %s%n", "ID", "Amount", "Category", "Date");
        System.out.println("─────────────────────────────────────────");
        
        for (Transaction transaction : transactions) {
            System.out.printf("%-4d | %-12s | %-15s | %s%n",
                    transaction.getId(),
                    formatAmount(transaction.getAmount()),
                    transaction.getCategory(),
                    transaction.getDateTime().format(DATE_TIME_FORMATTER));
        }
        
        System.out.println("─────────────────────────────────────────");
    }
    
    private BigDecimal readAmount() {
        while (true) {
            System.out.print("Enter transaction amount (can be positive or negative): ");
            String input = scanner.nextLine().trim();
            
            if (input.isEmpty()) {
                System.out.println("Error: amount cannot be empty. Please enter a number.");
                continue;
            }
            
            try {
                return new BigDecimal(input);
            } catch (NumberFormatException e) {
                System.out.println("Error: invalid number format. Please enter a number (e.g., 1500.50 or -500.00).");
            }
        }
    }
    
    private String readCategory() {
        while (true) {
            System.out.print("Enter transaction category: ");
            String input = scanner.nextLine().trim();
            
            if (input.isEmpty()) {
                System.out.println("Error: category cannot be empty. Please enter a category.");
                continue;
            }
            
            return input;
        }
    }
    
    private long readTransactionId() {
        while (true) {
            System.out.print("Enter transaction ID to delete: ");
            String input = scanner.nextLine().trim();
            
            if (input.isEmpty()) {
                System.out.println("Error: ID cannot be empty. Please enter a number.");
                continue;
            }
            
            try {
                return Long.parseLong(input);
            } catch (NumberFormatException e) {
                System.out.println("Error: invalid ID format. Please enter a number.");
            }
        }
    }
    
    private int readInt(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Invalid number format: " + input);
        }
    }
    
    /**
     * Formats amount as currency value with +/- sign and currency symbol.
     */
    private String formatAmount(BigDecimal amount) {
        if (amount == null) {
            return "0.00 ₽";
        }
        
        int comparison = amount.compareTo(BigDecimal.ZERO);
        
        if (comparison > 0) {
            return "+" + amount.setScale(2, RoundingMode.HALF_UP).toString() + " ₽";
        } else if (comparison < 0) {
            return amount.setScale(2, RoundingMode.HALF_UP).toString() + " ₽";
        } else {
            return "0.00 ₽";
        }
    }
}
