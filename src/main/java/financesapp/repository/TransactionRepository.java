package main.java.financesapp.repository;

import main.java.financesapp.model.Transaction;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Repository for persistent storage of transactions.
 * Uses CSV format: id,amount,category,date (ISO 8601 date format).
 * Loads transactions lazily on first access.
 */
public class TransactionRepository {
    
    private final String filePath;
    private final List<Transaction> transactions;
    private boolean loaded;
    private final AtomicLong idGenerator;
    
    private static final DateTimeFormatter DATE_TIME_FORMATTER = 
            DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    private static final String CSV_HEADER = "id,amount,category,date";
    
    public TransactionRepository(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }
        this.filePath = filePath.trim();
        this.transactions = new ArrayList<>();
        this.loaded = false;
        this.idGenerator = new AtomicLong(1);
    }
    
    /**
     * Ensures transactions are loaded from file (lazy loading).
     */
    private void ensureLoaded() throws IOException {
        if (!loaded) {
            loadFromFile();
            loaded = true;
        }
    }
    
    /**
     * Loads all transactions from CSV file.
     * Returns empty list if file doesn't exist.
     * Skips invalid lines and continues loading.
     */
    private void loadFromFile() throws IOException {
        transactions.clear();
        
        Path path = Paths.get(filePath);
        
        if (!Files.exists(path)) {
            return;
        }
        
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            boolean isFirstLine = true;
            
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                
                try {
                    Transaction transaction = parseTransaction(line);
                    if (transaction != null) {
                        transactions.add(transaction);
                    }
                } catch (Exception e) {
                    System.err.println("Warning: Skipped invalid line: " + line);
                    System.err.println("Error: " + e.getMessage());
                }
            }
        }
        
        // Update ID generator to avoid conflicts
        if (!transactions.isEmpty()) {
            long maxId = transactions.stream()
                    .mapToLong(Transaction::getId)
                    .max()
                    .orElse(0);
            idGenerator.set(maxId + 1);
        } else {
            idGenerator.set(1);
        }
    }
    
    /**
     * Saves all transactions to CSV file.
     * Creates directory and file if they don't exist.
     */
    private void saveToFile() throws IOException {
        Path path = Paths.get(filePath);
        Path parentDir = path.getParent();
        if (parentDir != null && !Files.exists(parentDir)) {
            Files.createDirectories(parentDir);
        }
        
        try (BufferedWriter writer = Files.newBufferedWriter(
                path, 
                StandardCharsets.UTF_8,
                java.nio.file.StandardOpenOption.CREATE,
                java.nio.file.StandardOpenOption.TRUNCATE_EXISTING,
                java.nio.file.StandardOpenOption.WRITE)) {
            
            writer.write(CSV_HEADER);
            writer.newLine();
            
            for (Transaction transaction : transactions) {
                writer.write(formatTransaction(transaction));
                writer.newLine();
            }
        }
    }
    
    /**
     * Returns all transactions.
     */
    public List<Transaction> getAll() throws IOException {
        ensureLoaded();
        return new ArrayList<>(transactions);
    }
    
    /**
     * Adds a new transaction and saves to file.
     */
    public Transaction add(Transaction transaction) throws IOException {
        ensureLoaded();
        transactions.add(transaction);
        saveToFile();
        return transaction;
    }
    
    /**
     * Removes a transaction and saves to file.
     */
    public void remove(Transaction transaction) throws IOException {
        ensureLoaded();
        transactions.remove(transaction);
        saveToFile();
    }
    
    /**
     * Finds transaction by ID.
     */
    public Transaction findById(long id) throws IOException {
        ensureLoaded();
        return transactions.stream()
                .filter(t -> t.getId() == id)
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Finds transactions by category (case-insensitive).
     */
    public List<Transaction> findByCategory(String category) throws IOException {
        ensureLoaded();
        String categoryLower = category.trim().toLowerCase();
        return transactions.stream()
                .filter(t -> t.getCategory().toLowerCase().equals(categoryLower))
                .collect(Collectors.toList());
    }
    
    /**
     * Returns next available ID.
     */
    public long getNextId() throws IOException {
        ensureLoaded();
        long newId = idGenerator.getAndIncrement();
        
        // Avoid ID conflicts
        long finalNewId = newId;
        while (transactions.stream().anyMatch(t -> t.getId() == finalNewId)) {
            newId = idGenerator.getAndIncrement();
        }
        
        return newId;
    }
    
    /**
     * Parses CSV line into Transaction object.
     * Format: id,amount,category,date
     */
    private Transaction parseTransaction(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }
        
        String[] parts = line.split(",");
        
        if (parts.length < 4) {
            throw new IllegalArgumentException("Invalid CSV format: expected 4 fields, got " + parts.length);
        }
        
        try {
            long id = Long.parseLong(parts[0].trim());
            BigDecimal amount = new BigDecimal(parts[1].trim());
            String category = parts[2].trim();
            LocalDateTime dateTime = LocalDateTime.parse(parts[3].trim(), DATE_TIME_FORMATTER);
            
            return new Transaction(id, amount, category, dateTime);
            
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Invalid number format in line: " + line);
        } catch (DateTimeParseException e) {
            throw new DateTimeParseException("Invalid date format in line: " + line, e.getParsedString(), e.getErrorIndex());
        }
    }
    
    /**
     * Formats Transaction to CSV string.
     * Format: id,amount,category,date
     */
    private String formatTransaction(Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("Transaction cannot be null");
        }
        
        return String.format("%d,%s,%s,%s",
                transaction.getId(),
                transaction.getAmount().toString(),
                transaction.getCategory(),
                transaction.getDateTime().format(DATE_TIME_FORMATTER));
    }
}
