package com.bookstore.controller;

import com.bookstore.model.Book;
import com.bookstore.model.Review;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.ReviewRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/books")
@CrossOrigin(origins = "*")
public class BookController {

    private final BookRepository bookRepository;
    private final ReviewRepository reviewRepository;

    public BookController(BookRepository bookRepository, ReviewRepository reviewRepository) {
        this.bookRepository = bookRepository;
        this.reviewRepository = reviewRepository;
    }

    // GET /books - Fetch all books (with optional search and category filter)
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllBooks(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category) {

        List<Book> books;

        if (search != null && !search.trim().isEmpty()) {
            books = bookRepository.searchBooks(search.trim());
        } else if (category != null && !category.trim().isEmpty()) {
            books = bookRepository.findByCategory(category.trim());
        } else {
            books = bookRepository.findAll();
        }

        List<String> categories = bookRepository.findAllCategories();

        Map<String, Object> response = new HashMap<>();
        response.put("books", books);
        response.put("categories", categories);
        response.put("total", books.size());

        return ResponseEntity.ok(response);
    }

    // GET /books/:id - Fetch book details
    @GetMapping("/{id}")
    public ResponseEntity<?> getBookById(@PathVariable Long id) {
        Optional<Book> book = bookRepository.findById(id);

        if (book.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Book not found with id: " + id);
            return ResponseEntity.notFound().build();
        }

        List<Review> reviews = reviewRepository.findByBookId(id);

        Map<String, Object> response = new HashMap<>();
        response.put("book", book.get());
        response.put("reviews", reviews);

        return ResponseEntity.ok(response);
    }

    // POST /books/:id/reviews - Add a review (Bonus feature)
    @PostMapping("/{id}/reviews")
    public ResponseEntity<?> addReview(@PathVariable Long id, @RequestBody Review review) {
        Optional<Book> book = bookRepository.findById(id);

        if (book.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (review.getReviewerName() == null || review.getReviewerName().trim().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Reviewer name is required");
            return ResponseEntity.badRequest().body(error);
        }

        if (review.getRating() == null || review.getRating() < 1 || review.getRating() > 5) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Rating must be between 1 and 5");
            return ResponseEntity.badRequest().body(error);
        }

        review.setBookId(id);
        reviewRepository.save(review);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Review added successfully");
        return ResponseEntity.ok(response);
    }
}
