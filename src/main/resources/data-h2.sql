-- Sample Demo Data for H2 Profile
INSERT INTO users (id, name, email, password, phone) VALUES
(1, 'Rahul Sharma (Lead)', 'rahul@example.com', 'password123', '9876543210'),
(2, 'Priya Patel', 'priya@example.com', 'password123', '9876543211'),
(3, 'Amit Verma', 'amit@example.com', 'password123', '9876543212'),
(4, 'Sneha Kulkarni', 'sneha@example.com', 'password123', '9876543213');

INSERT INTO trips (id, title, destination, start_date, end_date, created_by) VALUES
(1, 'Goa Beach & Adventure Vacation', 'Goa, India', '2026-11-10', '2026-11-15', 1);

INSERT INTO trip_members (id, trip_id, user_id, role) VALUES
(1, 1, 1, 'ORGANIZER'),
(2, 1, 2, 'MEMBER'),
(3, 1, 3, 'MEMBER'),
(4, 1, 4, 'MEMBER');

INSERT INTO activities (id, trip_id, name, category, estimated_cost) VALUES
(1, 1, 'Seaside Beach Resort Stay', 'HOTEL', 24000.00),
(2, 1, 'Fisherman Wharf Dinner', 'FOOD', 4000.00),
(3, 1, 'Scuba Diving & Water Sports', 'SIGHTSEEING', 8000.00),
(4, 1, 'Cab Rentals & Fuel', 'TRAVEL', 5000.00);

INSERT INTO expenses (id, trip_id, activity_id, title, amount, payer_id, expense_date, category) VALUES
(1, 1, 1, 'Resort Booking (3 nights)', 24000.00, 1, '2026-11-10', 'HOTEL'),
(2, 1, 2, 'Group Dinner at Wharf', 1000.00, 2, '2026-11-11', 'FOOD');

INSERT INTO expense_shares (id, expense_id, user_id, share_amount) VALUES
(1, 1, 1, 6000.00),
(2, 1, 2, 6000.00),
(3, 1, 3, 6000.00),
(4, 1, 4, 6000.00),
(5, 2, 1, 333.34),
(6, 2, 2, 333.33),
(7, 2, 3, 333.33);

INSERT INTO settlements (id, trip_id, payer_id, payee_id, amount, settlement_date, notes) VALUES
(1, 1, 3, 1, 2000.00, '2026-11-12', 'UPI payment for resort advance');

