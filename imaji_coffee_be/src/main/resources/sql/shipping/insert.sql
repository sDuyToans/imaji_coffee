INSERT INTO ship (methodId, methodName, code, expectedArrival, price, is_active, created_by)
VALUES
    (1, 'Free Shipping', 'F', '7 - 10 Business Days', 0.00, TRUE, 'system'),
    (2, 'Standard Shipping', 'S', '4 - 6 Business Days', 3.99, TRUE, 'system'),
    (3, 'Express Shipping', 'E', '2 - 3 Business Days', 6.99, TRUE, 'system'),
    (4, 'Instant Delivery', 'I', 'Same Day Delivery', 9.99, TRUE, 'system');
