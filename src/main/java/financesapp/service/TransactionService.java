package main.java.financesapp.service;

import main.java.financesapp.exception.TransactionNotFoundException;
import main.java.financesapp.model.Transaction;
import main.java.financesapp.repository.TransactionRepository;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for managing financial transactions.
 * Contains business logic and coordinates between UI and Repository layers.
 */
public class TransactionService {
    
    private final TransactionRepository repository;
    
    public TransactionService(TransactionRepository repository) {
        if (repository == null) {
            throw new IllegalArgumentException("Repository cannot be null");
        }
        this.repository = repository;
    }
    
    /**
     * Adds a new transaction and automatically saves to file.
     */
    public Transaction addTransaction(BigDecimal amount, String category) throws IOException {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be null or empty");
        }
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        
        long newId = repository.getNextId();
        
        Transaction transaction = new Transaction(
                newId,
                amount,
                category.trim(),
                LocalDateTime.now()
        );
        
        return repository.add(transaction);
    }
    
    /**
     * Returns a copy of all transactions.
     */
    public List<Transaction> getAllTransactions() throws IOException {
        return repository.getAll();
    }
    
    /**
     * Calculates total sum of all transactions.
     */
    public BigDecimal getTotalSum() throws IOException {
        return repository.getAll().stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * Finds all transactions by category (case-insensitive).
     */
    public List<Transaction> getTransactionsByCategory(String category) throws IOException {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be null or empty");
        }
        
        return repository.findByCategory(category);
    }
    
    /**
     * Deletes transaction by ID and automatically saves to file.
     */
    public void deleteTransaction(long id) throws TransactionNotFoundException, IOException {
        Transaction transactionToDelete = repository.findById(id);
        
        if (transactionToDelete == null) {
            throw new TransactionNotFoundException(id);
        }
        
        repository.remove(transactionToDelete);
    }
}
