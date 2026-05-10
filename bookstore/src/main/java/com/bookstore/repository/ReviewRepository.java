package com.bookstore.repository;

import com.bookstore.model.Review;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ReviewRepository {

    private final JdbcTemplate jdbcTemplate;

    public ReviewRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Review> findByBookId(Long bookId) {
        String sql = "SELECT * FROM reviews WHERE book_id = ? ORDER BY created_at DESC";
        return jdbcTemplate.query(sql, (rs, rn) -> {
            Review r = new Review();
            r.setId(rs.getLong("id"));
            r.setBookId(rs.getLong("book_id"));
            r.setReviewerName(rs.getString("reviewer_name"));
            r.setRating(rs.getInt("rating"));
            r.setComment(rs.getString("comment"));
            return r;
        }, bookId);
    }

    public void save(Review review) {
        String sql = "INSERT INTO reviews (book_id, reviewer_name, rating, comment) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, review.getBookId(), review.getReviewerName(),
                review.getRating(), review.getComment());

        // Update book average rating
        String updateRating = """
            UPDATE books SET rating = (
                SELECT AVG(CAST(rating AS DECIMAL(3,2))) FROM reviews WHERE book_id = ?
            ) WHERE id = ?
            """;
        jdbcTemplate.update(updateRating, review.getBookId(), review.getBookId());
    }
}
