package com.bookstore.repository;

import com.bookstore.model.Order;
import com.bookstore.model.OrderItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class OrderRepository {

    private final JdbcTemplate jdbcTemplate;

    public OrderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Long createOrder(Order order) {
        String sql = """
            INSERT INTO orders (session_id, customer_name, customer_email, customer_address, total_amount, status)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, order.getSessionId());
            ps.setString(2, order.getCustomerName());
            ps.setString(3, order.getCustomerEmail());
            ps.setString(4, order.getCustomerAddress());
            ps.setBigDecimal(5, order.getTotalAmount());
            ps.setString(6, "CONFIRMED");
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public void createOrderItem(Long orderId, Long bookId, Integer quantity, java.math.BigDecimal price) {
        String sql = "INSERT INTO order_items (order_id, book_id, quantity, price) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, orderId, bookId, quantity, price);
    }

    public Optional<Order> findById(Long id) {
        String sql = "SELECT * FROM orders WHERE id = ?";
        List<Order> orders = jdbcTemplate.query(sql, (rs, rn) -> {
            Order o = new Order();
            o.setId(rs.getLong("id"));
            o.setSessionId(rs.getString("session_id"));
            o.setCustomerName(rs.getString("customer_name"));
            o.setCustomerEmail(rs.getString("customer_email"));
            o.setCustomerAddress(rs.getString("customer_address"));
            o.setTotalAmount(rs.getBigDecimal("total_amount"));
            o.setStatus(rs.getString("status"));
            return o;
        }, id);
        return orders.isEmpty() ? Optional.empty() : Optional.of(orders.get(0));
    }

    public List<OrderItem> findOrderItems(Long orderId) {
        String sql = """
            SELECT oi.*, b.title, b.author
            FROM order_items oi
            JOIN books b ON oi.book_id = b.id
            WHERE oi.order_id = ?
            """;
        return jdbcTemplate.query(sql, (rs, rn) -> {
            OrderItem item = new OrderItem();
            item.setId(rs.getLong("id"));
            item.setOrderId(rs.getLong("order_id"));
            item.setBookId(rs.getLong("book_id"));
            item.setQuantity(rs.getInt("quantity"));
            item.setPrice(rs.getBigDecimal("price"));
            item.setBookTitle(rs.getString("title"));
            item.setBookAuthor(rs.getString("author"));
            return item;
        }, orderId);
    }
}
