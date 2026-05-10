package com.bookstore.controller;

import com.bookstore.model.CartItem;
import com.bookstore.model.Order;
import com.bookstore.repository.CartRepository;
import com.bookstore.repository.OrderRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/checkout")
@CrossOrigin(origins = "*")
public class CheckoutController {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;

    public CheckoutController(CartRepository cartRepository, OrderRepository orderRepository) {
        this.cartRepository = cartRepository;
        this.orderRepository = orderRepository;
    }

    // POST /checkout - Place an order
    @PostMapping
    public ResponseEntity<Map<String, Object>> checkout(
            @RequestBody Map<String, String> request,
            HttpSession session) {

        String sessionId = session.getId();
        List<CartItem> cartItems = cartRepository.findBySessionId(sessionId);

        if (cartItems.isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Cart is empty. Please add items before checkout.");
            return ResponseEntity.badRequest().body(error);
        }

        // Validate required fields
        String customerName = request.get("customerName");
        String customerEmail = request.get("customerEmail");
        String customerAddress = request.get("customerAddress");

        if (customerName == null || customerName.trim().isEmpty()) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Customer name is required");
            return ResponseEntity.badRequest().body(error);
        }

        if (customerEmail == null || customerEmail.trim().isEmpty() || !customerEmail.contains("@")) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Valid email is required");
            return ResponseEntity.badRequest().body(error);
        }

        // Calculate total
        BigDecimal total = cartItems.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Create order
        Order order = new Order();
        order.setSessionId(sessionId);
        order.setCustomerName(customerName.trim());
        order.setCustomerEmail(customerEmail.trim());
        order.setCustomerAddress(customerAddress != null ? customerAddress.trim() : "");
        order.setTotalAmount(total);
        order.setStatus("CONFIRMED");

        Long orderId = orderRepository.createOrder(order);

        // Save order items
        for (CartItem item : cartItems) {
            orderRepository.createOrderItem(orderId, item.getBookId(), item.getQuantity(), item.getBookPrice());
        }

        // Clear cart after successful order
        cartRepository.clearCart(sessionId);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Order placed successfully! Thank you for your purchase.");
        response.put("orderId", orderId);
        response.put("customerName", customerName);
        response.put("customerEmail", customerEmail);
        response.put("totalAmount", total);
        response.put("itemCount", cartItems.size());
        response.put("status", "CONFIRMED");

        return ResponseEntity.ok(response);
    }

    // GET /checkout/order/:id - Get order confirmation
    @GetMapping("/order/{id}")
    public ResponseEntity<?> getOrder(@PathVariable Long id, HttpSession session) {
        Optional<Order> order = orderRepository.findById(id);

        if (order.isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Order not found");
            return ResponseEntity.notFound().build();
        }

        var items = orderRepository.findOrderItems(id);
        order.get().setItems(items);

        return ResponseEntity.ok(order.get());
    }
}
