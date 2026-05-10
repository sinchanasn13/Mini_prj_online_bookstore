# 📚 PageTurner — Online Bookstore

A full-stack online bookstore built with **Java Spring Boot MVC**, **JDBC**, **H2 Database**, and **HTML/CSS/JavaScript**.

---

## 🚀 Quick Start (3 Steps)

### Prerequisites
- **Java 17+** — [Download](https://adoptium.net/)
- **Maven 3.8+** — [Download](https://maven.apache.org/download.cgi)
- **VS Code** — with [Extension Pack for Java](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack)

### Step 1 — Open in VS Code
```
File → Open Folder → Select the `bookstore` folder
```

### Step 2 — Run the Application
**Option A: VS Code**
- Press `F5` or go to Run → Start Debugging
- Select "Run BookstoreApplication"

**Option B: Terminal**
```bash
cd bookstore
mvn spring-boot:run
```

**Option C: Maven Wrapper**
```bash
cd bookstore
./mvnw spring-boot:run    # Mac/Linux
mvnw.cmd spring-boot:run  # Windows
```

### Step 3 — Open the App
```
http://localhost:8080
```

---

## 🎯 Features

| Feature | Status |
|---------|--------|
| Browse all books | ✅ |
| Search by title/author | ✅ |
| Filter by category | ✅ |
| Book detail page | ✅ |
| Add to cart | ✅ |
| Remove from cart | ✅ |
| Update quantities | ✅ |
| Checkout with form | ✅ |
| Order confirmation | ✅ |
| User login/signup | ✅ |
| Book reviews | ✅ (Bonus) |
| Book images | ✅ (Bonus) |
| H2 Console | ✅ |

---

## 🌐 REST API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/books` | Get all books (supports `?search=` and `?category=`) |
| `GET` | `/books/:id` | Get book details + reviews |
| `POST` | `/books/:id/reviews` | Add a review |
| `GET` | `/cart` | Get cart items |
| `POST` | `/cart` | Add book to cart |
| `PUT` | `/cart/:id` | Update cart item quantity |
| `DELETE` | `/cart/:id` | Remove item from cart |
| `POST` | `/checkout` | Place an order |
| `GET` | `/checkout/order/:id` | Get order details |
| `POST` | `/auth/login` | Login |
| `POST` | `/auth/signup` | Register |
| `POST` | `/auth/logout` | Logout |
| `GET` | `/auth/me` | Get current user |

### Example API Calls (curl)
```bash
# Get all books
curl http://localhost:8080/books

# Search books
curl "http://localhost:8080/books?search=gatsby"

# Get book by ID
curl http://localhost:8080/books/1

# Add to cart (requires session cookie)
curl -c cookies.txt -b cookies.txt -X POST http://localhost:8080/cart \
  -H "Content-Type: application/json" \
  -d '{"bookId": 1, "quantity": 1}'

# Get cart
curl -c cookies.txt -b cookies.txt http://localhost:8080/cart

# Checkout
curl -c cookies.txt -b cookies.txt -X POST http://localhost:8080/checkout \
  -H "Content-Type: application/json" \
  -d '{"customerName":"John","customerEmail":"john@test.com","customerAddress":"123 Main St"}'
```

---

## 🗄️ Database

Uses **H2 In-Memory Database** — no setup required! The database is created automatically on startup.

**H2 Console** (view/query the database):
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:bookstoredb`
- Username: `sa`
- Password: *(leave blank)*

### Schema
```sql
books          -- Book catalog
users          -- Registered users
cart_items     -- Shopping cart (session-based)
orders         -- Placed orders
order_items    -- Items in each order
reviews        -- Book reviews
```

---

## 📁 Project Structure

```
bookstore/
├── src/main/
│   ├── java/com/bookstore/
│   │   ├── BookstoreApplication.java     # Entry point
│   │   ├── controller/
│   │   │   ├── BookController.java       # GET /books, GET /books/:id
│   │   │   ├── CartController.java       # POST/GET/DELETE /cart
│   │   │   ├── CheckoutController.java   # POST /checkout
│   │   │   ├── UserController.java       # POST /auth/login, /signup
│   │   │   └── PageController.java       # Serves index.html
│   │   ├── model/
│   │   │   ├── Book.java
│   │   │   ├── CartItem.java
│   │   │   ├── Order.java
│   │   │   ├── OrderItem.java
│   │   │   ├── Review.java
│   │   │   └── User.java
│   │   └── repository/                   # JDBC data access
│   │       ├── BookRepository.java
│   │       ├── CartRepository.java
│   │       ├── OrderRepository.java
│   │       ├── ReviewRepository.java
│   │       └── UserRepository.java
│   └── resources/
│       ├── static/
│       │   ├── index.html                # Frontend SPA
│       │   ├── css/styles.css            # All styles
│       │   └── js/app.js                 # All JavaScript
│       ├── application.properties        # Config
│       ├── schema.sql                    # DB schema
│       └── data.sql                      # Sample data
└── pom.xml                               # Maven dependencies
```

---

## 🧑‍💻 Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 17, Spring Boot 3.2 |
| Web MVC | Spring Web MVC (REST Controllers) |
| Database | H2 (in-memory), Spring JDBC |
| Frontend | HTML5, CSS3, Vanilla JavaScript |
| Build | Maven |

---

## 🔑 Demo Credentials

| Username | Password |
|----------|----------|
| `john_doe` | `password123` |
| `jane_smith` | `password123` |

---

## 📖 Learning Concepts

This project demonstrates:

1. **Spring Web MVC** — `@RestController`, `@GetMapping`, `@PostMapping`, `@DeleteMapping`
2. **JDBC** — `JdbcTemplate`, `RowMapper`, `KeyHolder`
3. **REST APIs** — HTTP verbs, JSON responses, status codes
4. **Sessions** — Cart management using `HttpSession`
5. **Frontend** — DOM manipulation, `fetch()` API, event handling
6. **Database Design** — Foreign keys, joins, aggregate functions
7. **HTML/CSS** — Responsive design, CSS variables, animations

---

## ⚡ Troubleshooting

**Port 8080 already in use:**
```bash
# Kill the process using port 8080
lsof -ti:8080 | xargs kill -9   # Mac/Linux
netstat -ano | findstr :8080     # Windows (find PID, then kill it)
```

**Java version error:**
```bash
java -version  # Should show 17 or higher
```

**Maven not found:**
```bash
mvn -version  # Should work; if not, install Maven or use ./mvnw
```
