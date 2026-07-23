INSERT INTO products
(product_id, name, description, price, old_price, is_available_at_web, quantity, category, created_by)
VALUES (1, 'ristretto bianco', 'desc', 5.00, 6.00, FALSE, 30, 'coffee_baverage', 'admin'),
       (2, 'iced creamy latte', 'desc', 5.00, 6.00, FALSE, 30, 'coffee_baverage', 'admin'),
       (3, 'cappucino', 'desc', 5.00, 6.00, FALSE, 30, 'coffee_baverage', 'admin'),
       (4, 'iced long black', 'desc', 5.00, 6.00, FALSE, 30, 'coffee_baverage', 'admin'),
       (5, 'milk coffee regal', 'desc', 5.00, 6.00, FALSE, 30, 'coffee_baverage', 'admin'),
       (6, 'orange juice', 'desc', 5.00, 6.00, FALSE, 30, 'coffee_baverage', 'admin'),
       (7, 'soda beverage', 'desc', 5.00, 6.00, FALSE, 30, 'coffee_baverage', 'admin'),
       (8, 'iced coffee with milk', 'desc', 5.00, 6.00, FALSE, 30, 'coffee_baverage', 'admin'),
       (9, 'iced americano', 'desc', 5.00, 5.00, FALSE, 30, 'coffee_baverage', 'admin'),
       (10, 'vegan iced latte', 'desc', 5.00, 5.00, FALSE, 30, 'coffee_baverage', 'admin'),
       (11, 'iced chocolate', 'desc', 5.00, 5.00, FALSE, 30, 'coffee_baverage', 'admin'),
       (12, 'autumnal coffee', 'desc', 5.00, 5.00, FALSE, 30, 'coffee_baverage', 'admin'),

       (13, 'seafood lunch', 'desc', 5.00, 5.00, FALSE, 30, 'food_snack', 'admin'),
       (14, 'french toast with sugar', 'desc', 5.00, 5.00, FALSE, 30, 'food_snack', 'admin'),
       (15, 'chocolate croissant', 'desc', 5.00, 6.00, FALSE, 30, 'food_snack', 'admin'),
       (16, 'potato wedges', 'desc', 5.00, 6.00, FALSE, 30, 'food_snack', 'admin'),
       (17, 'brownies', 'desc', 5.00, 5.00, FALSE, 30, 'food_snack', 'admin'),
       (18, 'banana cake', 'desc', 5.00, 5.00, FALSE, 0, 'food_snack', 'admin'),
       (19, 'sandwiches and pickles', 'desc', 5.00, 5.00, FALSE, 30, 'food_snack', 'admin'),
       (20, 'spaghetti bolognese', 'desc', 5.00, 5.00, FALSE, 30, 'food_snack', 'admin'),
       (21, 'sandwich vegan', 'desc', 5.00, 5.00, FALSE, 30, 'food_snack', 'admin'),
       (22, 'eggs benedict burger', 'desc', 5.00, 5.00, FALSE, 0, 'food_snack', 'admin'),
       (23, 'corn cheese sandwich', 'desc', 5.00, 6.00, FALSE, 30, 'food_snack', 'admin'),
       (24, 'buttermilk waffle', 'desc', 5.00, 5.00, FALSE, 30, 'food_snack', 'admin'),

       (25, 'at home house blend', 'desc', 5.00, 5.00, FALSE, 30, 'at_home', 'admin'),
       (26, 'at home arabica', 'desc', 5.00, 5.00, FALSE, 0, 'at_home', 'admin'),
       (27, 'at home classic', 'desc', 5.00, 5.00, FALSE, 30, 'at_home', 'admin'),
       (28, 'white mug', 'desc', 5.00, 5.00, FALSE, 30, 'at_home', 'admin'),
       (29, 'at home kalosi', 'desc', 5.00, 5.00, FALSE, 0, 'at_home', 'admin'),
       (30, 'at home luwak', 'desc', 5.00, 5.00, FALSE, 0, 'at_home', 'admin'),
       (31, 'at home robusta', 'desc', 5.00, 5.00, FALSE, 30, 'at_home', 'admin'),
       (32, 'coffee temper 58 MM', 'desc', 5.00, 5.00, FALSE, 30, 'at_home', 'admin'),
       (33, 'french press 9 cups', 'desc', 25.00, 25.00, FALSE, 10, 'at_home', 'admin'),
       (34, 'glass tea pot teiera (6 cups)', 'desc', 13.00, 13.00, FALSE, 10, 'at_home', 'admin'),
       (35, 'french press 3 cup', 'desc', 20.00, 20.00, FALSE, 30, 'at_home', 'admin'),
       (36, 'moka pot', 'desc', 20.00, 20.00, FALSE, 30, 'at_home', 'admin');


INSERT INTO product_images (product_id, image_url, created_by)
VALUES (1, '/menu/coffee_baverage/Sections/Image.png', 'admin'),
       (2, '/menu/coffee_baverage/Sections/Image-1.png', 'admin'),
       (3, '/menu/coffee_baverage/Sections/Image-2.png', 'admin'),
       (4, '/menu/coffee_baverage/Sections/Image-3.png', 'admin'),
       (5, '/menu/coffee_baverage/Sections/Image-4.png', 'admin'),
       (6, '/menu/coffee_baverage/Sections/Image-5.png', 'admin'),
       (7, '/menu/coffee_baverage/Sections/Image-6.png', 'admin'),
       (8, '/menu/coffee_baverage/Sections/Image-7.png', 'admin'),
       (9, '/menu/coffee_baverage/Sections/Image-8.png', 'admin'),
       (10, '/menu/coffee_baverage/Sections/Image-9.png', 'admin'),
       (11, '/menu/coffee_baverage/Sections/Image-10.png', 'admin'),
       (12, '/menu/coffee_baverage/Sections/Image-11.png', 'admin'),

       (13, '/menu/food_snack/Sections/Image.png', 'admin'),
       (14, '/menu/food_snack/Sections/Image-1.png', 'admin'),
       (15, '/menu/food_snack/Sections/Image-2.png', 'admin'),
       (16, '/menu/food_snack/Sections/Image-3.png', 'admin'),
       (17, '/menu/food_snack/Sections/Image-4.png', 'admin'),
       (18, '/menu/food_snack/Sections/Image-5.png', 'admin'),
       (19, '/menu/food_snack/Sections/Image-6.png', 'admin'),
       (20, '/menu/food_snack/Sections/Image-7.png', 'admin'),
       (21, '/menu/food_snack/Sections/Image-8.png', 'admin'),
       (22, '/menu/food_snack/Sections/Image-9.png', 'admin'),
       (23, '/menu/food_snack/Sections/Image-10.png', 'admin'),
       (24, '/menu/food_snack/Sections/Image-11.png', 'admin'),

       (25, '/menu/at_home/Sections/Image.png', 'admin'),
       (26, '/menu/at_home/Sections/Image-1.png', 'admin'),
       (27, '/menu/at_home/Sections/Image-2.png', 'admin'),
       (28, '/menu/at_home/Sections/Image-3.png', 'admin'),
       (29, '/menu/at_home/Sections/Image-4.png', 'admin'),
       (30, '/menu/at_home/Sections/Image-5.png', 'admin'),
       (31, '/menu/at_home/Sections/Image-6.png', 'admin'),
       (32, '/menu/at_home/Sections/Image-7.png', 'admin'),
       (33, '/menu/at_home/Sections/Image-8.png', 'admin'),
       (34, '/menu/at_home/Sections/Image-9.png', 'admin'),
       (35, '/menu/at_home/Sections/Image-10.png', 'admin'),
       (36, '/menu/at_home/Sections/Image-11.png', 'admin');



INSERT INTO news (new_id, title, description, image, created_at, created_by, updated_at, updated_by)
VALUES (1, '', 'desc', '/news/Sections/Image1.png', '2022-08-19 00:00:00', 'admin', NULL, NULL),

       (2, 'Cold Brew Coffee, How to Drink Cold Coffee is More Enjoyable', 'desc', '/news/Sections/Image2.png',
        '2022-08-19 00:00:00', 'admin', NULL, NULL),

       (3, 'Meet Coffee Tonic, the Sensation of Drinking Coffee-Flavored Soda', 'desc', '/news/Sections/Image3.png',
        '2022-08-19 00:00:00', 'admin', NULL, NULL),

       (4, 'Workshop Coffee Sharing Session', 'desc', '/news/Sections/Image4.png', '2022-08-19 00:00:00', 'admin',
        NULL, NULL),

       (5, 'Workshop Coffee Brewing', 'desc', '/news/Sections/Image5.png', '2022-08-19 00:00:00', 'admin', NULL, NULL);


INSERT INTO event (event_id, name, image, start_date, duration, created_at, created_by, updated_at, updated_by)
VALUES (1, 'latter art workshop', '/home/event/card/Sections/Image1.png', '2023-02-20 00:00:00', '02:00:00',
        '2023-02-20 00:00:00', 'admin', NULL, NULL),

       (2, 'EXHIBITION COFFEE HARDWARE', '/home/event/card/Sections/Image2.png', '2023-03-20 00:00:00', '02:00:00',
        '2023-03-20 00:00:00', 'admin', NULL,
        NULL),

       (3, 'Factory visit', '/home/event/card/Sections/Image3.png', '2023-04-20 00:00:00', '02:00:00',
        '2023-04-20 00:00:00', 'admin', NULL, NULL);

INSERT INTO event (event_id, name, image, start_date, duration, created_by, updated_at, updated_by)
VALUES (4, 'Bezzera Latte Art Competition', '/event/Sections/Image1.png', '2023-02-20 00:00:00', '02:00:00', 'system',
        NULL, NULL),

       (5, 'SENSORY AND CUPPING CLASS', '/event/Sections/Image2.png', '2023-03-20 00:00:00', '02:00:00', 'system', NULL,
        NULL),

       (6, 'Public Cupping', '/event/Sections/Image3.png', '2023-02-20 00:00:00', '02:00:00', 'system', NULL, NULL),

       (7, 'Competitions and Showcases', '/event/Sections/Image4.png', '2023-03-20 00:00:00', '02:00:00', 'system',
        NULL, NULL),

       (8, 'Art and Coffee Festival', '/event/Sections/Image5.png', '2023-03-20 00:00:00', '02:00:00', 'system', NULL,
        NULL);


INSERT INTO space (space_id, name, type, image, created_at, created_by, updated_at, updated_by)
VALUES (1, 'white wall', 'workspace', '/home/space/Sections/Image1.png', NOW(), 'system', NULL, NULL),
       (2, 'long window', 'workspace', '/home/space/Sections/Image2.png', NOW(), 'system', NULL, NULL),
       (3, 'gengs space', 'workspace', '/home/space/Sections/Image3.png', NOW(), 'system', NULL, NULL),
       (4, 'seminar area', 'event space', '/home/space/Sections/Image4.png', NOW(), 'system', NULL, NULL),
       (5, 'center area', 'event space', '/home/space/Sections/Image5.png', NOW(), 'system', NULL, NULL),
       (6, 'aquarium', 'meeting space', '/home/space/Sections/Image6.png', NOW(), 'system', NULL, NULL),
       (7, 'roftop', 'workspace', '/home/space/Sections/Image7.png', NOW(), 'system', NULL, NULL),
       (8, 'hamble space', 'meeting space', '/home/space/Sections/Image8.png', NOW(), 'system', NULL, NULL);

INSERT INTO promos
(code, title, description, discount_type, discount_value, start_at, end_at, is_active, created_at, created_by)
VALUES
    ('CASHBACK25', 'Cashback $2.5,00', 'Ends in 5 minutes!', 'fixed', 2.50, NOW(), DATE_ADD(NOW(), INTERVAL 5 MINUTE), TRUE, NOW(), 'system'),
    ('DISKON50', 'Diskon 50% With Credit or Debit Card', 'Ends in 20 minutes!', 'percentage', 50.00, NOW(), DATE_ADD(NOW(), INTERVAL 20 MINUTE), TRUE, NOW(), 'system'),
    ('FREESHIP2', 'Free shipping $2,00', 'Ends in 10 minutes!', 'free_shipping', 2.00, NOW(), DATE_ADD(NOW(), INTERVAL 10 MINUTE), TRUE, NOW(), 'system'),
    ('SUMMER10', 'Summer Sale 10% OFF', 'Ends in 1 hour!', 'percentage', 10.00, NOW(), DATE_ADD(NOW(), INTERVAL 60 MINUTE), TRUE, NOW(), 'system'),
    ('HOLIDAY15', 'Holiday Special $15 OFF', 'Ends in 2 hours!', 'fixed', 15.00, NOW(), DATE_ADD(NOW(), INTERVAL 120 MINUTE), TRUE, NOW(), 'system'),
    ('NEWUSER5', 'New User $5 OFF', 'Ends in 30 minutes!', 'fixed', 5.00, NOW(), DATE_ADD(NOW(), INTERVAL 30 MINUTE), TRUE, NOW(), 'system');


