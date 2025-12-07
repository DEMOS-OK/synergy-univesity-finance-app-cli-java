package main.java.financesapp.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a financial transaction.
 * Uses BigDecimal for amount to ensure precision with monetary values.
 */
public class Transaction {
    
    private long id;
    private BigDecimal amount;
    
    private String category;
    
    private LocalDateTime dateTime;
    
    /**
     * Creates a new transaction without ID (ID will be assigned automatically).
     */
    public Transaction(BigDecimal amount, String category, LocalDateTime dateTime) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be null or empty");
        }
        if (dateTime == null) {
            throw new IllegalArgumentException("DateTime cannot be null");
        }
        
        this.amount = amount;
        this.category = category.trim();
        this.dateTime = dateTime;
    }
    
    /**
     * Creates a transaction with specified ID (used when loading from file).
     */
    public Transaction(long id, BigDecimal amount, String category, LocalDateTime dateTime) {
        this(amount, category, dateTime);
        if (id <= 0) {
            throw new IllegalArgumentException("ID must be positive");
        }
        this.id = id;
    }
    
    public long getId() {
        return id;
    }
    
    public void setId(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID must be positive");
        }
        this.id = id;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public void setAmount(BigDecimal amount) {
        if (amount == null) {
            throw new IllegalArgumentException("Amount cannot be null");
        }
        this.amount = amount;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Category cannot be null or empty");
        }
        this.category = category.trim();
    }
    
    public LocalDateTime getDateTime() {
        return dateTime;
    }
    
    public void setDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            throw new IllegalArgumentException("DateTime cannot be null");
        }
        this.dateTime = dateTime;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Transaction that = (Transaction) obj;
        return id == that.id;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", amount=" + amount +
                ", category='" + category + '\'' +
                ", dateTime=" + dateTime +
                '}';
    }
}
