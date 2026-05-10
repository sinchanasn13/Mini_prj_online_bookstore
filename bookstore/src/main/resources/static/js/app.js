// === STATE ===
let currentPage = 'home';
let allBooks = [];
let allCategories = [];
let currentBookId = null;
let cartCount = 0;
let selectedRating = 0;
let currentDetailQty = 1;

// === INITIALIZATION ===
document.addEventListener('DOMContentLoaded', () => {
    loadBooks();
    loadCartCount();
    checkAuthStatus();

    // Enter key on nav search
    document.getElementById('navSearch').addEventListener('keydown', e => {
        if (e.key === 'Enter') performSearch();
    });
    document.getElementById('heroSearch').addEventListener('keydown', e => {
        if (e.key === 'Enter') performHeroSearch();
    });
});

// === NAVIGATION ===
function showPage(page) {
    document.querySelectorAll('.page').forEach(p => p.classList.remove('active'));
    document.getElementById(page + 'Page').classList.add('active');
    currentPage = page;
    window.scrollTo({ top: 0, behavior: 'smooth' });

    if (page === 'cart') loadCart();
    if (page === 'checkout') loadCheckoutSummary();
}

// === BOOKS ===
async function loadBooks(search = '', category = '') {
    showLoading(true);
    try {
        let url = '/books';
        const params = new URLSearchParams();
        if (search) params.append('search', search);
        if (category) params.append('category', category);
        if (params.toString()) url += '?' + params.toString();

        const res = await fetch(url);
        const data = await res.json();
        allBooks = data.books;
        allCategories = data.categories;

        renderBooks(allBooks);
        renderCategories(allCategories, category);

        const title = search ? `Results for "${search}"` : category ? category : 'Our Collection';
        document.getElementById('sectionTitle').textContent = title;
        document.getElementById('bookCount').textContent = `${allBooks.length} book${allBooks.length !== 1 ? 's' : ''}`;
    } catch (err) {
        console.error('Failed to load books:', err);
        showToast('Failed to load books. Is the server running?', 'error');
    } finally {
        showLoading(false);
    }
}

function renderBooks(books) {
    const grid = document.getElementById('booksGrid');
    const empty = document.getElementById('emptyState');

    if (books.length === 0) {
        grid.innerHTML = '';
        empty.style.display = 'block';
        return;
    }

    empty.style.display = 'none';
    grid.innerHTML = books.map(book => `
        <div class="book-card" onclick="showBookDetail(${book.id})">
            ${book.imageUrl
                ? `<img class="book-cover" src="${book.imageUrl}" alt="${escHtml(book.title)}" onerror="this.style.display='none';this.nextElementSibling.style.display='flex'">`
                : ''}
            <div class="book-cover-placeholder" ${book.imageUrl ? 'style="display:none"' : ''}>📖</div>
            <div class="book-info">
                <div class="book-category">${escHtml(book.category || 'Book')}</div>
                <div class="book-title">${escHtml(book.title)}</div>
                <div class="book-author">by ${escHtml(book.author)}</div>
                <div class="book-footer">
                    <div class="book-price">$${book.price.toFixed(2)}</div>
                    <div class="book-rating">
                        <span class="stars">★</span>
                        <span>${(book.rating || 4).toFixed(1)}</span>
                    </div>
                </div>
            </div>
            <button class="add-to-cart-quick" onclick="event.stopPropagation(); quickAddToCart(${book.id})" title="Add to Cart">+</button>
        </div>
    `).join('');
}

function renderCategories(categories, active = '') {
    const pills = document.getElementById('categoryPills');
    pills.innerHTML = `<button class="pill ${!active ? 'active' : ''}" onclick="filterCategory('')">All Books</button>`;
    categories.forEach(cat => {
        pills.innerHTML += `<button class="pill ${active === cat ? 'active' : ''}" onclick="filterCategory('${escHtml(cat)}')">${escHtml(cat)}</button>`;
    });
}

function filterCategory(category) {
    loadBooks('', category);
    showPage('home');
}

function performSearch() {
    const q = document.getElementById('navSearch').value.trim();
    if (q) loadBooks(q);
    else loadBooks();
    showPage('home');
}

function performHeroSearch() {
    const q = document.getElementById('heroSearch').value.trim();
    if (q) {
        document.getElementById('navSearch').value = q;
        loadBooks(q);
        showPage('home');
        setTimeout(() => {
            document.querySelector('.books-section').scrollIntoView({ behavior: 'smooth' });
        }, 100);
    }
}

// === BOOK DETAIL ===
async function showBookDetail(bookId) {
    currentBookId = bookId;
    currentDetailQty = 1;
    showPage('bookDetail');

    const content = document.getElementById('bookDetailContent');
    content.innerHTML = '<div class="loading"><div class="spinner"></div><p>Loading book...</p></div>';

    try {
        const res = await fetch(`/books/${bookId}`);
        const data = await res.json();
        const book = data.book;
        const reviews = data.reviews || [];

        const starsHtml = generateStars(book.rating || 4);

        content.innerHTML = `
            <div style="display:grid;grid-template-columns:300px 1fr;gap:48px;align-items:start">
                <div>
                    ${book.imageUrl
                        ? `<img class="detail-cover" src="${book.imageUrl}" alt="${escHtml(book.title)}" onerror="this.style.display='none';this.nextElementSibling.style.display='flex'">`
                        : ''}
                    <div class="detail-cover-placeholder" ${book.imageUrl ? 'style="display:none"' : ''}>📖</div>
                </div>
                <div class="detail-info">
                    <div class="detail-category">${escHtml(book.category || 'Book')}</div>
                    <h1 class="detail-title">${escHtml(book.title)}</h1>
                    <div class="detail-author">by ${escHtml(book.author)}</div>
                    <div class="detail-rating">
                        <span class="stars" style="font-size:22px">${starsHtml}</span>
                        <span style="font-size:16px;color:var(--ink-light)">${(book.rating || 4).toFixed(1)} out of 5</span>
                    </div>
                    <div class="detail-price">$${book.price.toFixed(2)}</div>
                    <p class="detail-description">${escHtml(book.description || 'No description available.')}</p>
                    
                    <div class="qty-add">
                        <div class="qty-control">
                            <button class="qty-btn" onclick="changeDetailQty(-1)">−</button>
                            <span class="qty-display" id="detailQty">1</span>
                            <button class="qty-btn" onclick="changeDetailQty(1)">+</button>
                        </div>
                        <button class="add-cart-btn" onclick="addToCartFromDetail(${book.id})">
                            Add to Cart
                        </button>
                    </div>
                    <p style="font-size:13px;color:var(--sage)">✓ ${book.stock || 'Many'} in stock — Ready to ship</p>
                </div>
            </div>

            <!-- Reviews Section -->
            <div class="reviews-section">
                <h2>Customer Reviews (${reviews.length})</h2>
                
                <div class="review-form">
                    <h3>Write a Review</h3>
                    <div class="star-rating" id="starRating">
                        ${[1,2,3,4,5].map(n => `<button class="star-btn" onclick="setRating(${n})" data-star="${n}">★</button>`).join('')}
                    </div>
                    <div class="form-group" style="margin-bottom:16px">
                        <input type="text" id="reviewName" placeholder="Your name">
                    </div>
                    <div class="form-group" style="margin-bottom:16px">
                        <textarea id="reviewComment" rows="3" placeholder="Share your thoughts about this book..."></textarea>
                    </div>
                    <button class="primary-btn" onclick="submitReview(${book.id})">Post Review</button>
                </div>

                <div class="review-list" id="reviewList">
                    ${reviews.length === 0
                        ? '<p style="color:var(--ink-light);text-align:center;padding:20px">No reviews yet. Be the first!</p>'
                        : reviews.map(r => renderReview(r)).join('')}
                </div>
            </div>
        `;

        // Fix mobile responsiveness inline
        if (window.innerWidth <= 768) {
            const grid = content.querySelector('[style*="grid-template-columns:300px"]');
            if (grid) grid.style.gridTemplateColumns = '1fr';
        }

    } catch (err) {
        content.innerHTML = '<p style="color:var(--rust);padding:40px">Failed to load book details.</p>';
    }
}

function renderReview(r) {
    const initial = r.reviewerName ? r.reviewerName[0].toUpperCase() : '?';
    return `
        <div class="review-item">
            <div class="review-header">
                <div class="reviewer-avatar">${initial}</div>
                <div class="reviewer-info">
                    <strong>${escHtml(r.reviewerName)}</strong>
                    <div class="review-stars">${'★'.repeat(r.rating)}${'☆'.repeat(5 - r.rating)}</div>
                </div>
            </div>
            <p class="review-comment">${escHtml(r.comment || '')}</p>
        </div>
    `;
}

function changeDetailQty(delta) {
    currentDetailQty = Math.max(1, currentDetailQty + delta);
    document.getElementById('detailQty').textContent = currentDetailQty;
}

function setRating(n) {
    selectedRating = n;
    document.querySelectorAll('.star-btn').forEach((btn, i) => {
        btn.classList.toggle('active', i < n);
    });
}

async function submitReview(bookId) {
    const name = document.getElementById('reviewName').value.trim();
    const comment = document.getElementById('reviewComment').value.trim();

    if (!name) { showToast('Please enter your name', 'error'); return; }
    if (!selectedRating) { showToast('Please select a rating', 'error'); return; }

    try {
        const res = await fetch(`/books/${bookId}/reviews`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ reviewerName: name, rating: selectedRating, comment })
        });
        const data = await res.json();

        if (res.ok) {
            showToast('Review posted!', 'success');
            document.getElementById('reviewName').value = '';
            document.getElementById('reviewComment').value = '';
            selectedRating = 0;
            document.querySelectorAll('.star-btn').forEach(b => b.classList.remove('active'));

            // Add review to list
            const list = document.getElementById('reviewList');
            const newReview = renderReview({ reviewerName: name, rating: selectedRating || 5, comment });
            if (list.children.length === 1 && list.children[0].tagName === 'P') {
                list.innerHTML = '';
            }
            list.insertAdjacentHTML('afterbegin', renderReview({ reviewerName: name, rating: selectedRating, comment }));
        } else {
            showToast(data.error || 'Failed to post review', 'error');
        }
    } catch {
        showToast('Failed to post review', 'error');
    }
}

// === CART ===
async function loadCart() {
    const layout = document.getElementById('cartLayout');
    layout.innerHTML = '<div class="loading"><div class="spinner"></div><p>Loading cart...</p></div>';

    try {
        const res = await fetch('/cart');
        const data = await res.json();

        cartCount = data.itemCount || 0;
        updateCartBadge();

        if (!data.items || data.items.length === 0) {
            layout.innerHTML = `
                <div class="empty-cart">
                    <div style="font-size:64px">🛒</div>
                    <h2>Your cart is empty</h2>
                    <p>Add some books to get started!</p>
                    <button class="primary-btn" onclick="showPage('home')">Browse Books</button>
                </div>
            `;
            return;
        }

        const itemsHtml = data.items.map(item => `
            <div class="cart-item" id="cartItem${item.id}">
                ${item.bookImageUrl
                    ? `<img class="cart-item-img" src="${item.bookImageUrl}" alt="${escHtml(item.bookTitle)}" onerror="this.style.display='none';this.nextElementSibling.style.display='flex'">`
                    : ''}
                <div class="cart-item-placeholder" ${item.bookImageUrl ? 'style="display:none"' : ''}>📖</div>
                <div class="cart-item-info">
                    <div class="cart-item-title" onclick="showBookDetail(${item.bookId})">${escHtml(item.bookTitle)}</div>
                    <div class="cart-item-author">by ${escHtml(item.bookAuthor)}</div>
                    <div class="cart-item-controls">
                        <div class="qty-control">
                            <button class="qty-btn" onclick="updateCartQty(${item.id}, ${item.quantity - 1})">−</button>
                            <span class="qty-display">${item.quantity}</span>
                            <button class="qty-btn" onclick="updateCartQty(${item.id}, ${item.quantity + 1})">+</button>
                        </div>
                        <button class="remove-btn" onclick="removeFromCart(${item.id})">Remove</button>
                    </div>
                </div>
                <div class="cart-item-price">$${item.subtotal.toFixed(2)}</div>
            </div>
        `).join('');

        const subtotal = data.total;
        const shipping = subtotal > 0 ? 4.99 : 0;
        const grandTotal = subtotal + shipping;

        layout.innerHTML = `
            <div class="cart-items-section">${itemsHtml}</div>
            <div class="cart-summary">
                <h2>Order Summary</h2>
                <div class="summary-row"><span>Subtotal (${data.items.length} items)</span><span>$${subtotal.toFixed(2)}</span></div>
                <div class="summary-row"><span>Shipping</span><span>$${shipping.toFixed(2)}</span></div>
                <div class="summary-row total"><span>Total</span><span>$${grandTotal.toFixed(2)}</span></div>
                <button class="checkout-btn" onclick="showPage('checkout')">Proceed to Checkout →</button>
                <button class="back-btn" onclick="showPage('home')" style="width:100%;justify-content:center;margin-top:12px">Continue Shopping</button>
            </div>
        `;
    } catch (err) {
        layout.innerHTML = '<p style="color:var(--rust);padding:40px">Failed to load cart.</p>';
    }
}

async function quickAddToCart(bookId) {
    await addToCart(bookId, 1);
}

async function addToCartFromDetail(bookId) {
    await addToCart(bookId, currentDetailQty);
}

async function addToCart(bookId, quantity = 1) {
    try {
        const res = await fetch('/cart', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ bookId, quantity })
        });
        const data = await res.json();

        if (res.ok) {
            cartCount = data.itemCount;
            updateCartBadge();
            showToast('Added to cart! 🛒', 'success');
        } else {
            showToast(data.error || 'Failed to add to cart', 'error');
        }
    } catch {
        showToast('Failed to add to cart', 'error');
    }
}

async function removeFromCart(itemId) {
    try {
        const res = await fetch(`/cart/${itemId}`, { method: 'DELETE' });
        if (res.ok) {
            showToast('Item removed', 'success');
            loadCart();
            loadCartCount();
        }
    } catch {
        showToast('Failed to remove item', 'error');
    }
}

async function updateCartQty(itemId, newQty) {
    if (newQty <= 0) {
        removeFromCart(itemId);
        return;
    }
    try {
        const res = await fetch(`/cart/${itemId}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ quantity: newQty })
        });
        if (res.ok) {
            loadCart();
            loadCartCount();
        }
    } catch {
        showToast('Failed to update quantity', 'error');
    }
}

async function loadCartCount() {
    try {
        const res = await fetch('/cart');
        const data = await res.json();
        cartCount = data.itemCount || 0;
        updateCartBadge();
    } catch {}
}

function updateCartBadge() {
    const badge = document.getElementById('cartBadge');
    badge.textContent = cartCount;
    badge.style.display = cartCount > 0 ? 'flex' : 'none';
}

// === CHECKOUT ===
async function loadCheckoutSummary() {
    try {
        const res = await fetch('/cart');
        const data = await res.json();
        const summary = document.getElementById('checkoutSummary');

        if (!data.items || data.items.length === 0) {
            summary.innerHTML = '<p>Your cart is empty.</p>';
            return;
        }

        const subtotal = data.total;
        const shipping = 4.99;
        const total = subtotal + shipping;

        summary.innerHTML = `
            <h2>Order Summary</h2>
            ${data.items.map(item => `
                <div class="summary-row">
                    <span>${escHtml(item.bookTitle)} × ${item.quantity}</span>
                    <span>$${item.subtotal.toFixed(2)}</span>
                </div>
            `).join('')}
            <div class="summary-row"><span>Shipping</span><span>$${shipping.toFixed(2)}</span></div>
            <div class="summary-row total"><span>Total</span><span>$${total.toFixed(2)}</span></div>
        `;
    } catch {}
}

async function placeOrder() {
    const name = document.getElementById('checkoutName').value.trim();
    const email = document.getElementById('checkoutEmail').value.trim();
    const address = document.getElementById('checkoutAddress').value.trim();

    if (!name) { showToast('Please enter your name', 'error'); return; }
    if (!email || !email.includes('@')) { showToast('Please enter a valid email', 'error'); return; }

    const btn = document.querySelector('.checkout-btn');
    btn.textContent = 'Placing Order...';
    btn.disabled = true;

    try {
        const res = await fetch('/checkout', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ customerName: name, customerEmail: email, customerAddress: address })
        });
        const data = await res.json();

        if (res.ok) {
            cartCount = 0;
            updateCartBadge();

            document.getElementById('confirmationMsg').textContent =
                `Thank you, ${data.customerName}! Your order #${data.orderId} has been confirmed. A confirmation will be sent to ${data.customerEmail}.`;

            document.getElementById('orderDetails').innerHTML = `
                <div class="order-detail-row"><span>Order Number</span><span>#${data.orderId}</span></div>
                <div class="order-detail-row"><span>Items</span><span>${data.itemCount}</span></div>
                <div class="order-detail-row"><span>Status</span><span>✅ ${data.status}</span></div>
                <div class="order-detail-row"><span>Total</span><span>$${parseFloat(data.totalAmount).toFixed(2)}</span></div>
            `;

            showPage('confirmation');
        } else {
            showToast(data.error || 'Checkout failed', 'error');
            btn.textContent = 'Place Order';
            btn.disabled = false;
        }
    } catch {
        showToast('Failed to place order. Please try again.', 'error');
        btn.textContent = 'Place Order';
        btn.disabled = false;
    }
}

// === AUTH ===
async function checkAuthStatus() {
    try {
        const res = await fetch('/auth/me');
        const data = await res.json();

        if (data.loggedIn) {
            showUserMenu(data.fullName || data.username);
        }
    } catch {}
}

function showUserMenu(name) {
    document.getElementById('authBtn').style.display = 'none';
    document.getElementById('userMenu').style.display = 'flex';
    document.getElementById('userInitial').textContent = name[0].toUpperCase();
    document.getElementById('dropdownName').textContent = name;
}

function hideUserMenu() {
    document.getElementById('authBtn').style.display = 'flex';
    document.getElementById('userMenu').style.display = 'none';
}

function toggleAuthModal() {
    document.getElementById('authModal').classList.toggle('active');
}

function closeAuthModal() {
    document.getElementById('authModal').classList.remove('active');
}

function closeModalOnOverlay(e) {
    if (e.target === document.getElementById('authModal')) closeAuthModal();
}

function switchTab(tab) {
    document.getElementById('loginTab').classList.toggle('active', tab === 'login');
    document.getElementById('signupTab').classList.toggle('active', tab === 'signup');
    document.getElementById('loginForm').style.display = tab === 'login' ? 'block' : 'none';
    document.getElementById('signupForm').style.display = tab === 'signup' ? 'block' : 'none';
    document.getElementById('loginError').textContent = '';
    document.getElementById('signupError').textContent = '';
}

function toggleUserDropdown() {
    const dd = document.getElementById('userDropdown');
    dd.style.display = dd.style.display === 'none' ? 'block' : 'none';
}

async function login() {
    const username = document.getElementById('loginUsername').value.trim();
    const password = document.getElementById('loginPassword').value;
    const errorEl = document.getElementById('loginError');

    if (!username || !password) {
        errorEl.textContent = 'Username and password are required';
        return;
    }

    try {
        const res = await fetch('/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });
        const data = await res.json();

        if (res.ok) {
            closeAuthModal();
            showUserMenu(data.fullName || data.username);
            showToast(`Welcome back, ${data.fullName || data.username}!`, 'success');
        } else {
            errorEl.textContent = data.error || 'Login failed';
        }
    } catch {
        errorEl.textContent = 'Network error. Please try again.';
    }
}

async function signup() {
    const name = document.getElementById('signupName').value.trim();
    const username = document.getElementById('signupUsername').value.trim();
    const email = document.getElementById('signupEmail').value.trim();
    const password = document.getElementById('signupPassword').value;
    const errorEl = document.getElementById('signupError');

    if (!username || !email || !password) {
        errorEl.textContent = 'All fields except name are required';
        return;
    }

    try {
        const res = await fetch('/auth/signup', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, email, password, fullName: name || username })
        });
        const data = await res.json();

        if (res.ok) {
            closeAuthModal();
            showUserMenu(data.fullName || data.username);
            showToast(`Welcome, ${data.fullName || data.username}!`, 'success');
        } else {
            errorEl.textContent = data.error || 'Signup failed';
        }
    } catch {
        errorEl.textContent = 'Network error. Please try again.';
    }
}

async function logout() {
    try {
        await fetch('/auth/logout', { method: 'POST' });
    } catch {}
    hideUserMenu();
    document.getElementById('userDropdown').style.display = 'none';
    showToast('Signed out successfully');
}

// === UTILITIES ===
function showLoading(show) {
    const loading = document.getElementById('loading');
    const grid = document.getElementById('booksGrid');
    if (show) {
        loading.style.display = 'flex';
        grid.style.display = 'none';
    } else {
        loading.style.display = 'none';
        grid.style.display = 'grid';
    }
}

function showToast(message, type = '') {
    const toast = document.getElementById('toast');
    toast.textContent = message;
    toast.className = 'toast' + (type ? ` ${type}` : '') + ' show';
    setTimeout(() => {
        toast.classList.remove('show');
    }, 3000);
}

function generateStars(rating) {
    const full = Math.floor(rating);
    const half = rating % 1 >= 0.5 ? 1 : 0;
    const empty = 5 - full - half;
    return '★'.repeat(full) + (half ? '½' : '') + '☆'.repeat(empty);
}

function escHtml(str) {
    if (!str) return '';
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;');
}

// Close user dropdown when clicking outside
document.addEventListener('click', e => {
    const userMenu = document.getElementById('userMenu');
    const dropdown = document.getElementById('userDropdown');
    if (userMenu && !userMenu.contains(e.target)) {
        dropdown.style.display = 'none';
    }
});
