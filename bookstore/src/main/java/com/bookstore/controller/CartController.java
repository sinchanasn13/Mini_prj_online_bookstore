package com.bookstore.controller;

import com.bookstore.model.CartItem;
import com.bookstore.repository.CartRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cart")
@CrossOrigin(origins = "*")
public class CartController {

    private final CartRepository cartRepository;

    public CartController(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    // GET /cart - Get user's cart
    @GetMapping
    public ResponseEntity<Map<String, Object>> getCart(HttpSession session) {
        String sessionId = session.getId();
        List<CartItem> items = cartRepository.findBySessionId(sessionId);

        BigDecimal total = items.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int itemCount = cartRepository.countItems(sessionId);

        Map<String, Object> response = new HashMap<>();
        response.put("items", items);
        response.put("total", total);
        response.put("itemCount", itemCount);
        response.put("sessionId", sessionId);

        return ResponseEntity.ok(response);
    }

    // POST /cart - Add book to cart
    @PostMapping
    public ResponseEntity<Map<String, Object>> addToCart(
            @RequestBody Map<String, Object> request,
            HttpSession session) {

        String sessionId = session.getId();
        Long bookId;
        Integer quantity;

        try {
            bookId = Long.parseLong(request.get("bookId").toString());
            quantity = request.containsKey("quantity")
                    ? Integer.parseInt(request.get("quantity").toString())
                    : 1;
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Invalid bookId or quantity");
            return ResponseEntity.badRequest().body(error);
        }

        cartRepository.addToCart(sessionId, bookId, quantity);

        int itemCount = cartRepository.countItems(sessionId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Book added to cart successfully");
        response.put("itemCount", itemCount);

        return ResponseEntity.ok(response);
    }

    // DELETE /cart/:id - Remove book from cart
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> removeFromCart(
            @PathVariable Long id,
            HttpSession session) {

        String sessionId = session.getId();
        boolean removed = cartRepository.removeFromCart(id, sessionId);

        Map<String, String> response = new HashMap<>();
        if (removed) {
            response.put("message", "Item removed from cart");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Item not found in cart");
            return ResponseEntity.notFound().build();
        }
    }

    // PUT /cart/:id - Update item quantity
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateQuantity(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request,
            HttpSession session) {

        String sessionId = session.getId();
        Integer quantity;

        try {
            quantity = Integer.parseInt(request.get("quantity").toString());
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid quantity");
            return ResponseEntity.badRequest().body(error);
        }

        cartRepository.updateQuantity(id, sessionId, quantity);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Quantity updated");
        return ResponseEntity.ok(response);
    }
}
