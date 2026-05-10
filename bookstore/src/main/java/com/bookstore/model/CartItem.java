package com.bookstore.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CartItem {
    private Long id;
    private String sessionId;
    private Long bookId;
    private Integer quantity;
    private LocalDateTime addedAt;

    // Joined fields from books table
    private String bookTitle;
    private String bookAuthor;
    private BigDecimal bookPrice;
    private String bookImageUrl;

    public CartItem() {}

    public CartItem(String sessionId, Long bookId, Integer quantity) {
        this.sessionId = sessionId;
        this.bookId = bookId;
        this.quantity = quantity;
    }

    public BigDecimal getSubtotal() {
        if (bookPrice != null && quantity != null) {
            return bookPrice.multiply(BigDecimal.valueOf(quantity));
        }
        return BigDecimal.ZERO;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public LocalDateTime getAddedAt() { return addedAt; }
    public void setAddedAt(LocalDateTime addedAt) { this.addedAt = addedAt; }

    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    public String getBookAuthor() { return bookAuthor; }
    public void setBookAuthor(String bookAuthor) { this.bookAuthor = bookAuthor; }

    public BigDecimal getBookPrice() { return bookPrice; }
    public void setBookPrice(BigDecimal bookPrice) { this.bookPrice = bookPrice; }

    public String getBookImageUrl() { return bookImageUrl; }
    public void setBookImageUrl(String bookImageUrl) { this.bookImageUrl = bookImageUrl; }
}
