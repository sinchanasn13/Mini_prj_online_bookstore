package com.bookstore.repository;

import com.bookstore.model.Book;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class BookRepository {

    private final JdbcTemplate jdbcTemplate;

    public BookRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Book> bookRowMapper = (rs, rowNum) -> {
        Book book = new Book();
        book.setId(rs.getLong("id"));
        book.setTitle(rs.getString("title"));
        book.setAuthor(rs.getString("author"));
        book.setDescription(rs.getString("description"));
        book.setPrice(rs.getBigDecimal("price"));
        book.setCategory(rs.getString("category"));
        book.setImageUrl(rs.getString("image_url"));
        book.setStock(rs.getInt("stock"));
        book.setRating(rs.getDouble("rating"));
        return book;
    };

    // GET /books - Fetch all books
    public List<Book> findAll() {
        String sql = "SELECT * FROM books ORDER BY title";
        return jdbcTemplate.query(sql, bookRowMapper);
    }

    // GET /books/:id - Fetch book details
    public Optional<Book> findById(Long id) {
        String sql = "SELECT * FROM books WHERE id = ?";
        List<Book> books = jdbcTemplate.query(sql, bookRowMapper, id);
        return books.isEmpty() ? Optional.empty() : Optional.of(books.get(0));
    }

    // Search books by title or author
    public List<Book> searchBooks(String query) {
        String sql = "SELECT * FROM books WHERE LOWER(title) LIKE ? OR LOWER(author) LIKE ? ORDER BY title";
        String searchParam = "%" + query.toLowerCase() + "%";
        return jdbcTemplate.query(sql, bookRowMapper, searchParam, searchParam);
    }

    // Get books by category
    public List<Book> findByCategory(String category) {
        String sql = "SELECT * FROM books WHERE LOWER(category) = ? ORDER BY title";
        return jdbcTemplate.query(sql, bookRowMapper, category.toLowerCase());
    }

    // Get all distinct categories
    public List<String> findAllCategories() {
        String sql = "SELECT DISTINCT category FROM books ORDER BY category";
        return jdbcTemplate.queryForList(sql, String.class);
    }
}
