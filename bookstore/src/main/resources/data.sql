-- Sample Books Data
INSERT INTO books (title, author, description, price, category, image_url, stock, rating) VALUES
('The Great Gatsby', 'F. Scott Fitzgerald', 'A story of the fabulously wealthy Jay Gatsby and his love for the beautiful Daisy Buchanan, set in the Jazz Age on Long Island. A vivid portrait of the American dream, its excess, its corruption, and its ultimate failure.', 12.99, 'Classic Fiction', 'https://covers.openlibrary.org/b/id/8432522-L.jpg', 50, 4.1),

('To Kill a Mockingbird', 'Harper Lee', 'The unforgettable novel of a childhood in a sleepy Southern town and the crisis of conscience that rocked it. Winner of the Pulitzer Prize, it became both an instant bestseller and a critical success.', 14.99, 'Classic Fiction', 'https://covers.openlibrary.org/b/id/8228691-L.jpg', 75, 4.8),

('1984', 'George Orwell', 'A dystopian social science fiction novel that follows Winston Smith, who works for the Party rewriting history. A chilling depiction of a totalitarian society that feels more relevant than ever.', 13.99, 'Science Fiction', 'https://covers.openlibrary.org/b/id/8575708-L.jpg', 60, 4.7),

('The Alchemist', 'Paulo Coelho', 'A fable about following your dream. It tells the story of Santiago, an Andalusian shepherd boy who yearns to travel in search of a worldly treasure. A global phenomenon, translated into 80 languages.', 11.99, 'Fiction', 'https://covers.openlibrary.org/b/id/8294349-L.jpg', 90, 4.6),

('Sapiens: A Brief History of Humankind', 'Yuval Noah Harari', 'From a renowned historian comes a groundbreaking narrative of humanity''s creation and evolution—a #1 international bestseller—that explores the ways in which biology and history have defined us.', 18.99, 'Non-Fiction', 'https://covers.openlibrary.org/b/id/8739161-L.jpg', 45, 4.5),

('The Psychology of Money', 'Morgan Housel', 'Timeless lessons on wealth, greed, and happiness. Doing well with money isn''t necessarily about what you know. It''s about how you behave. And behavior is hard to teach, even to really smart people.', 16.99, 'Finance', 'https://covers.openlibrary.org/b/id/10527692-L.jpg', 55, 4.7),

('Atomic Habits', 'James Clear', 'An Easy & Proven Way to Build Good Habits & Break Bad Ones. No matter your goals, Atomic Habits offers a proven framework for improving every day.', 17.99, 'Self-Help', 'https://covers.openlibrary.org/b/id/10452011-L.jpg', 80, 4.8),

('Harry Potter and the Sorcerer''s Stone', 'J.K. Rowling', 'Harry Potter has never even heard of Hogwarts when the letters start dropping on the doormat at number four, Privet Drive. The magical world awaits.', 15.99, 'Fantasy', 'https://covers.openlibrary.org/b/id/10110415-L.jpg', 100, 4.9),

('The Lean Startup', 'Eric Ries', 'How Today''s Entrepreneurs Use Continuous Innovation to Create Radically Successful Businesses. A new approach to business being adopted around the world.', 19.99, 'Business', 'https://covers.openlibrary.org/b/id/8399608-L.jpg', 40, 4.4),

('Dune', 'Frank Herbert', 'Set on the desert planet Arrakis, Dune is the story of the boy Paul Atreides, heir to a noble family tasked with ruling the planet. The greatest science fiction novel of all time.', 16.99, 'Science Fiction', 'https://covers.openlibrary.org/b/id/9255566-L.jpg', 65, 4.8),

('The Midnight Library', 'Matt Haig', 'Between life and death there is a library, and within that library, the shelves go on forever. Every book provides a chance to try another life you could have lived.', 14.99, 'Fiction', 'https://covers.openlibrary.org/b/id/10521270-L.jpg', 70, 4.3),

('Clean Code', 'Robert C. Martin', 'A Handbook of Agile Software Craftsmanship. Even bad code can function. But if code isn''t clean, it can bring a development organization to its knees. A must-read for every programmer.', 39.99, 'Technology', 'https://covers.openlibrary.org/b/id/8681264-L.jpg', 30, 4.6);

-- Sample Users
INSERT INTO users (username, email, password, full_name) VALUES
('john_doe', 'john@example.com', 'password123', 'John Doe'),
('jane_smith', 'jane@example.com', 'password123', 'Jane Smith');

-- Sample Reviews
INSERT INTO reviews (book_id, reviewer_name, rating, comment) VALUES
(1, 'Alice Reader', 5, 'A timeless classic that everyone should read. Fitzgerald''s prose is simply beautiful.'),
(1, 'Bob Bookworm', 4, 'Great story, though a bit slow in places. The symbolism is remarkable.'),
(2, 'Carol Page', 5, 'One of the most powerful books I have ever read. A masterpiece of American literature.'),
(3, 'David Novel', 5, 'Terrifyingly relevant to today''s world. A must-read for everyone.'),
(7, 'Eve Chapter', 5, 'Changed how I think about habits completely. Practical and insightful.'),
(8, 'Frank Story', 5, 'Magical from start to finish! Perfect for all ages.');
