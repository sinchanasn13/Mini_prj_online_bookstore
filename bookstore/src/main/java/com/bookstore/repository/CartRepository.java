package com.bookstore.repository;

import com.bookstore.model.CartItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CartRepository {

    private final JdbcTemplate jdbcTemplate;

    public CartRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<CartItem> cartItemRowMapper = (rs, rowNum) -> {
        CartItem item = new CartItem();
        item.setId(rs.getLong("id"));
        item.setSessionId(rs.getString("session_id"));
        item.setBookId(rs.getLong("book_id"));
        item.setQuantity(rs.getInt("quantity"));
        // Joined book fields
        try {
            item.setBookTitle(rs.getString("title"));
            item.setBookAuthor(rs.getString("author"));
            item.setBookPrice(rs.getBigDecimal("price"));
            item.setBookImageUrl(rs.getString("image_url"));
        } catch (Exception ignored) {}
        return item;
    };

    // GET /cart - Get user's cart
    public List<CartItem> findBySessionId(String sessionId) {
        String sql = """
            SELECT c.*, b.title, b.author, b.price, b.image_url
            FROM cart_items c
            JOIN books b ON c.book_id = b.id
            WHERE c.session_id = ?
            ORDER BY c.added_at DESC
            """;
        return jdbcTemplate.query(sql, cartItemRowMapper, sessionId);
    }

    // POST /cart - Add book to cart
    public void addToCart(String sessionId, Long bookId, Integer quantity) {
        // Check if item already exists in cart
        String checkSql = "SELECT id, quantity FROM cart_items WHERE session_id = ? AND book_id = ?";
        List<CartItem> existing = jdbcTemplate.query(checkSql,
            (rs, rn) -> {
                CartItem c = new CartItem();
                c.setId(rs.getLong("id"));
                c.setQuantity(rs.getInt("quantity"));
                return c;
            }, sessionId, bookId);

        if (!existing.isEmpty()) {
            // Update quantity
            String updateSql = "UPDATE cart_items SET quantity = quantity + ? WHERE session_id = ? AND book_id = ?";
            jdbcTemplate.update(updateSql, quantity, sessionId, bookId);
        } else {
            // Insert new item
            String insertSql = "INSERT INTO cart_items (session_id, book_id, quantity) VALUES (?, ?, ?)";
            jdbcTemplate.update(insertSql, sessionId, bookId, quantity);
        }
    }

    // DELETE /cart/:id - Remove book from cart
    public boolean removeFromCart(Long cartItemId, String sessionId) {
        String sql = "DELETE FROM cart_items WHERE id = ? AND session_id = ?";
        int rows = jdbcTemplate.update(sql, cartItemId, sessionId);
        return rows > 0;
    }

    // Update item quantity
    public void updateQuantity(Long cartItemId, String sessionId, Integer quantity) {
        if (quantity <= 0) {
            removeFromCart(cartItemId, sessionId);
        } else {
            String sql = "UPDATE cart_items SET quantity = ? WHERE id = ? AND session_id = ?";
            jdbcTemplate.update(sql, quantity, cartItemId, sessionId);
        }
    }

    // Clear cart after checkout
    public void clearCart(String sessionId) {
        String sql = "DELETE FROM cart_items WHERE session_id = ?";
        jdbcTemplate.update(sql, sessionId);
    }

    // Count items in cart
    public int countItems(String sessionId) {
        String sql = "SELECT COALESCE(SUM(quantity), 0) FROM cart_items WHERE session_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, sessionId);
        return count != null ? count : 0;
    }
}
