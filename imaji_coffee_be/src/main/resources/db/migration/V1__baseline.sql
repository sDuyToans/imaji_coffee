-- MySQL dump 10.13  Distrib 9.3.0, for Linux (aarch64)
--
-- Host: localhost    Database: imajicoffee
-- ------------------------------------------------------
-- Server version	9.3.0

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `addresses`
--

DROP TABLE IF EXISTS `addresses`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `addresses` (
                             `address_id` bigint NOT NULL AUTO_INCREMENT,
                             `user_id` bigint DEFAULT NULL,
                             `name` varchar(100) NOT NULL,
                             `street` varchar(255) NOT NULL,
                             `city` varchar(100) NOT NULL,
                             `province` varchar(100) DEFAULT NULL,
                             `postal_code` varchar(20) DEFAULT NULL,
                             `country` varchar(2) NOT NULL,
                             `phone_number` varchar(20) DEFAULT NULL,
                             `is_default` tinyint(1) DEFAULT '0',
                             `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             `created_by` varchar(20) NOT NULL,
                             `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                             `updated_by` varchar(20) DEFAULT NULL,
                             `apartment` varchar(255) DEFAULT NULL,
                             PRIMARY KEY (`address_id`),
                             KEY `idx_address_user_id` (`user_id`),
                             CONSTRAINT `addresses_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table `addresses` intentionally seeded with no rows in this baseline
-- (original dump contained real customer data; omitted from source control)
--

--
-- Table structure for table `cart`
--

DROP TABLE IF EXISTS `cart`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart` (
                        `cart_id` bigint NOT NULL AUTO_INCREMENT,
                        `user_id` bigint NOT NULL,
                        `promo_id` bigint DEFAULT NULL,
                        `ship_method_id` bigint DEFAULT NULL,
                        `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        `created_by` varchar(20) NOT NULL,
                        `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                        `updated_by` varchar(20) DEFAULT NULL,
                        PRIMARY KEY (`cart_id`),
                        KEY `fk_cart_user` (`user_id`),
                        KEY `fk_cart_promo` (`promo_id`),
                        KEY `fk_cart_ship` (`ship_method_id`),
                        CONSTRAINT `fk_cart_promo` FOREIGN KEY (`promo_id`) REFERENCES `promos` (`id`),
                        CONSTRAINT `fk_cart_ship` FOREIGN KEY (`ship_method_id`) REFERENCES `ship` (`method_id`),
                        CONSTRAINT `fk_cart_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table `cart` intentionally seeded with no rows in this baseline
-- (original dump contained real customer data; omitted from source control)
--

--
-- Table structure for table `cart_item`
--

DROP TABLE IF EXISTS `cart_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cart_item` (
                             `cart_item_id` bigint NOT NULL AUTO_INCREMENT,
                             `product_id` bigint NOT NULL,
                             `cart_id` bigint NOT NULL,
                             `quantity` int NOT NULL,
                             `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             `created_by` varchar(20) NOT NULL,
                             `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                             `updated_by` varchar(20) DEFAULT NULL,
                             PRIMARY KEY (`cart_item_id`),
                             KEY `fk_cart_item_product` (`product_id`),
                             KEY `fk_cart_item_cart_id` (`cart_id`),
                             CONSTRAINT `fk_cart_item_cart_id` FOREIGN KEY (`cart_id`) REFERENCES `cart` (`cart_id`),
                             CONSTRAINT `fk_cart_item_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`),
                             CONSTRAINT `cart_item_chk_1` CHECK ((`quantity` > 0))
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table `cart_item` intentionally seeded with no rows in this baseline
-- (original dump contained real customer data; omitted from source control)
--

--
-- Table structure for table `event`
--

DROP TABLE IF EXISTS `event`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `event` (
                         `event_id` bigint NOT NULL AUTO_INCREMENT,
                         `name` varchar(255) NOT NULL,
                         `image` varchar(500) NOT NULL,
                         `start_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         `duration` time NOT NULL,
                         `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         `created_by` varchar(20) NOT NULL,
                         `updated_at` timestamp NULL DEFAULT NULL,
                         `updated_by` varchar(20) DEFAULT NULL,
                         PRIMARY KEY (`event_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event`
--

LOCK TABLES `event` WRITE;
/*!40000 ALTER TABLE `event` DISABLE KEYS */;
INSERT INTO `event` VALUES (1,'latter art workshop','/home/event/card/Sections/Image1.png','2026-05-20 00:00:00','02:00:00','2023-02-20 00:00:00','admin',NULL,NULL),(2,'EXHIBITION COFFEE HARDWARE','/home/event/card/Sections/Image2.png','2026-04-20 00:00:00','02:00:00','2023-03-20 00:00:00','admin',NULL,NULL),(3,'Factory visit','/home/event/card/Sections/Image3.png','2026-03-20 00:00:00','02:00:00','2023-04-20 00:00:00','admin',NULL,NULL),(4,'Bezzera Latte Art Competition','/event/Sections/Image1.png','2023-02-20 00:00:00','02:00:00','2025-08-23 19:08:32','system',NULL,NULL),(5,'SENSORY AND CUPPING CLASS','/event/Sections/Image2.png','2023-03-20 00:00:00','02:00:00','2025-08-23 19:08:32','system',NULL,NULL),(6,'Public Cupping','/event/Sections/Image3.png','2023-04-20 00:00:00','02:00:00','2025-08-23 19:08:32','system',NULL,NULL),(7,'Competitions and Showcases','/event/Sections/Image4.png','2023-05-20 00:00:00','02:00:00','2025-08-23 19:08:32','system',NULL,NULL),(8,'Art and Coffee Festival','/event/Sections/Image5.png','2023-06-20 00:00:00','02:00:00','2025-08-23 19:08:32','system',NULL,NULL);
/*!40000 ALTER TABLE `event` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `news`
--

DROP TABLE IF EXISTS `news`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `news` (
                        `new_id` bigint NOT NULL AUTO_INCREMENT,
                        `title` varchar(255) NOT NULL,
                        `description` text,
                        `image` varchar(500) NOT NULL,
                        `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        `created_by` varchar(20) NOT NULL,
                        `updated_at` timestamp NULL DEFAULT NULL,
                        `updated_by` varchar(20) DEFAULT NULL,
                        PRIMARY KEY (`new_id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `news`
--

LOCK TABLES `news` WRITE;
/*!40000 ALTER TABLE `news` DISABLE KEYS */;
INSERT INTO `news` VALUES (1,'Visited Doesoen Sirap Coffee, The Producer of Robusta in Central Java','We are a small cafe, with a big dream of becoming a global cafe. We started our operations in Singapore, and we are now expanding to Singapore. We will be opening our first cafe in Beijing soon, and we also plan to open more cafes across the globe. Realizing The Mission of Global Expansion, Open its First Cafe in Singapore\n\nThe cafe is located at Dempsey Hill, one of the most sought-after areas in Singapore. It\'\'s a family-friendly neighborhood with a wide range of restaurants and cafes. With a diverse population and many people from different cultures, Global Expansion Cafe aims to bring international food culture to Singaporeans from all walks of life.\n\nWe are currently running our first pop-up shop at Dempsey Hill which will run until mid-November. Global Expansion Cafe will be open for lunch and dinner on weekdays as well as weekends. Global Expansion Cafe will introduce new tastes and experiences to Singaporeans by introducing international food culture.\n\nWe are currently running our pop-up shop in Singapore, and hope to launch our own café soon. A real estate company is established in Asia that develops communities and provides financial services globally. We believe in love and happiness, a sense of belonging, and the importance of a shared experience. Here you can be yourself, share stories and make friends.\n\nWe partner with companies who LOVE the world. The cafe is a place where people can go to enjoy the food and coffee, but also a place where they can meet new friends. The cafe is located in Pasir Ris. The cafe has a wide variety of menu items to choose from. In addition to the usual coffee and tea, there are also some specialty drinks such as smoothies, milkshakes and frozen yogurt.\n\nThere are many types of food available at the cafe such as sandwiches, soups, salads and desserts. The menu also includes vegetarian options for those who have special dietary needs. People can order their food online via the website or by calling the number given on the website. They will be able to choose their preferred time for pickup so that they don\'\'t miss out on any orders!','/news/Sections/Image1.png','2022-08-19 00:00:00','admin',NULL,NULL),(2,'Cold Brew Coffee, How to Drink Cold Coffee is More Enjoyable','We are a small cafe, with a big dream of becoming a global cafe. We started our operations in Singapore, and we are now expanding to Singapore. We will be opening our first cafe in Beijing soon, and we also plan to open more cafes across the globe. Realizing The Mission of Global Expansion, Open its First Cafe in Singapore\n\nThe cafe is located at Dempsey Hill, one of the most sought-after areas in Singapore. It\'\'s a family-friendly neighborhood with a wide range of restaurants and cafes. With a diverse population and many people from different cultures, Global Expansion Cafe aims to bring international food culture to Singaporeans from all walks of life.\n\nWe are currently running our first pop-up shop at Dempsey Hill which will run until mid-November. Global Expansion Cafe will be open for lunch and dinner on weekdays as well as weekends. Global Expansion Cafe will introduce new tastes and experiences to Singaporeans by introducing international food culture.\n\nWe are currently running our pop-up shop in Singapore, and hope to launch our own café soon. A real estate company is established in Asia that develops communities and provides financial services globally. We believe in love and happiness, a sense of belonging, and the importance of a shared experience. Here you can be yourself, share stories and make friends.\n\nWe partner with companies who LOVE the world. The cafe is a place where people can go to enjoy the food and coffee, but also a place where they can meet new friends. The cafe is located in Pasir Ris. The cafe has a wide variety of menu items to choose from. In addition to the usual coffee and tea, there are also some specialty drinks such as smoothies, milkshakes and frozen yogurt.\n\nThere are many types of food available at the cafe such as sandwiches, soups, salads and desserts. The menu also includes vegetarian options for those who have special dietary needs. People can order their food online via the website or by calling the number given on the website. They will be able to choose their preferred time for pickup so that they don\'\'t miss out on any orders!','/news/Sections/Image2.png','2022-08-19 00:00:00','admin',NULL,NULL),(3,'Meet Coffee Tonic, the Sensation of Drinking Coffee-Flavored Soda','We are a small cafe, with a big dream of becoming a global cafe. We started our operations in Singapore, and we are now expanding to Singapore. We will be opening our first cafe in Beijing soon, and we also plan to open more cafes across the globe. Realizing The Mission of Global Expansion, Open its First Cafe in Singapore\n\nThe cafe is located at Dempsey Hill, one of the most sought-after areas in Singapore. It\'\'s a family-friendly neighborhood with a wide range of restaurants and cafes. With a diverse population and many people from different cultures, Global Expansion Cafe aims to bring international food culture to Singaporeans from all walks of life.\n\nWe are currently running our first pop-up shop at Dempsey Hill which will run until mid-November. Global Expansion Cafe will be open for lunch and dinner on weekdays as well as weekends. Global Expansion Cafe will introduce new tastes and experiences to Singaporeans by introducing international food culture.\n\nWe are currently running our pop-up shop in Singapore, and hope to launch our own café soon. A real estate company is established in Asia that develops communities and provides financial services globally. We believe in love and happiness, a sense of belonging, and the importance of a shared experience. Here you can be yourself, share stories and make friends.\n\nWe partner with companies who LOVE the world. The cafe is a place where people can go to enjoy the food and coffee, but also a place where they can meet new friends. The cafe is located in Pasir Ris. The cafe has a wide variety of menu items to choose from. In addition to the usual coffee and tea, there are also some specialty drinks such as smoothies, milkshakes and frozen yogurt.\n\nThere are many types of food available at the cafe such as sandwiches, soups, salads and desserts. The menu also includes vegetarian options for those who have special dietary needs. People can order their food online via the website or by calling the number given on the website. They will be able to choose their preferred time for pickup so that they don\'\'t miss out on any orders!','/news/Sections/Image3.png','2022-08-19 00:00:00','admin',NULL,NULL),(4,'Workshop Coffee Sharing Session','We are a small cafe, with a big dream of becoming a global cafe. We started our operations in Singapore, and we are now expanding to Singapore. We will be opening our first cafe in Beijing soon, and we also plan to open more cafes across the globe. Realizing The Mission of Global Expansion, Open its First Cafe in Singapore\n\nThe cafe is located at Dempsey Hill, one of the most sought-after areas in Singapore. It\'\'s a family-friendly neighborhood with a wide range of restaurants and cafes. With a diverse population and many people from different cultures, Global Expansion Cafe aims to bring international food culture to Singaporeans from all walks of life.\n\nWe are currently running our first pop-up shop at Dempsey Hill which will run until mid-November. Global Expansion Cafe will be open for lunch and dinner on weekdays as well as weekends. Global Expansion Cafe will introduce new tastes and experiences to Singaporeans by introducing international food culture.\n\nWe are currently running our pop-up shop in Singapore, and hope to launch our own café soon. A real estate company is established in Asia that develops communities and provides financial services globally. We believe in love and happiness, a sense of belonging, and the importance of a shared experience. Here you can be yourself, share stories and make friends.\n\nWe partner with companies who LOVE the world. The cafe is a place where people can go to enjoy the food and coffee, but also a place where they can meet new friends. The cafe is located in Pasir Ris. The cafe has a wide variety of menu items to choose from. In addition to the usual coffee and tea, there are also some specialty drinks such as smoothies, milkshakes and frozen yogurt.\n\nThere are many types of food available at the cafe such as sandwiches, soups, salads and desserts. The menu also includes vegetarian options for those who have special dietary needs. People can order their food online via the website or by calling the number given on the website. They will be able to choose their preferred time for pickup so that they don\'\'t miss out on any orders!','/news/Sections/Image4.png','2022-08-19 00:00:00','admin',NULL,NULL),(5,'Workshop Coffee Brewing','We are a small cafe, with a big dream of becoming a global cafe. We started our operations in Singapore, and we are now expanding to Singapore. We will be opening our first cafe in Beijing soon, and we also plan to open more cafes across the globe. Realizing The Mission of Global Expansion, Open its First Cafe in Singapore\n\nThe cafe is located at Dempsey Hill, one of the most sought-after areas in Singapore. It\'\'s a family-friendly neighborhood with a wide range of restaurants and cafes. With a diverse population and many people from different cultures, Global Expansion Cafe aims to bring international food culture to Singaporeans from all walks of life.\n\nWe are currently running our first pop-up shop at Dempsey Hill which will run until mid-November. Global Expansion Cafe will be open for lunch and dinner on weekdays as well as weekends. Global Expansion Cafe will introduce new tastes and experiences to Singaporeans by introducing international food culture.\n\nWe are currently running our pop-up shop in Singapore, and hope to launch our own café soon. A real estate company is established in Asia that develops communities and provides financial services globally. We believe in love and happiness, a sense of belonging, and the importance of a shared experience. Here you can be yourself, share stories and make friends.\n\nWe partner with companies who LOVE the world. The cafe is a place where people can go to enjoy the food and coffee, but also a place where they can meet new friends. The cafe is located in Pasir Ris. The cafe has a wide variety of menu items to choose from. In addition to the usual coffee and tea, there are also some specialty drinks such as smoothies, milkshakes and frozen yogurt.\n\nThere are many types of food available at the cafe such as sandwiches, soups, salads and desserts. The menu also includes vegetarian options for those who have special dietary needs. People can order their food online via the website or by calling the number given on the website. They will be able to choose their preferred time for pickup so that they don\'\'t miss out on any orders!','/news/Sections/Image5.png','2022-08-19 00:00:00','admin',NULL,NULL);
/*!40000 ALTER TABLE `news` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_items`
--

DROP TABLE IF EXISTS `order_items`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `order_items` (
                               `order_item_id` bigint NOT NULL AUTO_INCREMENT,
                               `order_id` bigint NOT NULL,
                               `product_id` bigint NOT NULL,
                               `product_name` varchar(255) NOT NULL,
                               `price` decimal(10,2) NOT NULL,
                               `quantity` int NOT NULL,
                               `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               `created_by` varchar(20) NOT NULL,
                               `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                               `updated_by` varchar(20) DEFAULT NULL,
                               `product_category` enum('coffee_baverage','food_snack','at_home') NOT NULL,
                               `product_img` varchar(512) NOT NULL,
                               PRIMARY KEY (`order_item_id`),
                               KEY `order_id` (`order_id`),
                               KEY `product_id` (`product_id`),
                               KEY `idx_order_items_order_id` (`order_item_id`),
                               CONSTRAINT `order_items_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE CASCADE,
                               CONSTRAINT `order_items_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE RESTRICT
) ENGINE=InnoDB AUTO_INCREMENT=53 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table `order_items` intentionally seeded with no rows in this baseline
-- (original dump contained real customer data; omitted from source control)
--

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
                          `order_id` bigint NOT NULL AUTO_INCREMENT,
                          `user_id` bigint DEFAULT NULL,
                          `email` varchar(100) NOT NULL,
                          `shipping_address` json NOT NULL,
                          `total_amount` decimal(10,2) NOT NULL,
                          `currency` varchar(3) NOT NULL,
                          `status` enum('PENDING','PAYMENT_FAILED','PAID','PROCESSING','SHIPPED','DELIVERED','CANCELLED','REFUNDED') DEFAULT 'PENDING',
                          `payment_status` varchar(20) NOT NULL DEFAULT 'UNPAID',
                          `payment_intent_id` varchar(255) DEFAULT NULL,
                          `external_payment_id` varchar(100) DEFAULT NULL,
                          `checkout_request_id` varchar(100) DEFAULT NULL,
                          `stock_released` tinyint(1) NOT NULL DEFAULT '0',
                          `confirmation_email_sent` tinyint(1) NOT NULL DEFAULT '0',
                          `confirmation_email_sent_at` timestamp NULL DEFAULT NULL,
                          `payment_method` varchar(50) DEFAULT NULL,
                          `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          `created_by` varchar(20) NOT NULL,
                          `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          `updated_by` varchar(20) DEFAULT NULL,
                          `tax_amount` decimal(10,2) DEFAULT '0.00',
                          `shipping_amount` decimal(10,2) DEFAULT '0.00',
                          `discount_amount` decimal(10,2) DEFAULT '0.00',
                          `shipping_method` varchar(100) DEFAULT NULL,
                          PRIMARY KEY (`order_id`),
                          UNIQUE KEY `uq_orders_user_checkout_request` (`user_id`,`checkout_request_id`)
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table `orders` intentionally seeded with no rows in this baseline
-- (original dump contained real customer data; omitted from source control)
--

--
-- Table structure for table `product_images`
--

DROP TABLE IF EXISTS `product_images`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `product_images` (
                                  `product_image_id` bigint NOT NULL AUTO_INCREMENT,
                                  `product_id` bigint NOT NULL,
                                  `image_url` varchar(500) NOT NULL,
                                  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  `created_by` varchar(20) NOT NULL,
                                  `updated_at` timestamp NULL DEFAULT NULL,
                                  `updated_by` varchar(20) DEFAULT NULL,
                                  `is_main` tinyint(1) DEFAULT '0',
                                  PRIMARY KEY (`product_image_id`),
                                  KEY `product_id` (`product_id`),
                                  CONSTRAINT `product_images_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=145 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `product_images`
--

LOCK TABLES `product_images` WRITE;
/*!40000 ALTER TABLE `product_images` DISABLE KEYS */;
INSERT INTO `product_images` VALUES (1,1,'/menu/coffee_baverage/Sections/Image.png','2025-08-26 19:39:15','admin',NULL,NULL,1),(2,2,'/menu/coffee_baverage/Sections/Image-1.png','2025-08-26 19:39:15','admin',NULL,NULL,1),(3,3,'/menu/coffee_baverage/Sections/Image-2.png','2025-08-26 19:39:15','admin',NULL,NULL,1),(4,4,'/menu/coffee_baverage/Sections/Image-3.png','2025-08-26 19:39:15','admin',NULL,NULL,1),(5,5,'/menu/coffee_baverage/Sections/Image-4.png','2025-08-26 19:39:15','admin',NULL,NULL,1),(6,6,'/menu/coffee_baverage/Sections/Image-5.png','2025-08-26 19:39:15','admin',NULL,NULL,1),(7,7,'/menu/coffee_baverage/Sections/Image-6.png','2025-08-26 19:39:15','admin',NULL,NULL,1),(8,8,'/menu/coffee_baverage/Sections/Image-7.png','2025-08-26 19:39:15','admin',NULL,NULL,1),(9,9,'/menu/coffee_baverage/Sections/Image-8.png','2025-08-26 19:39:15','admin',NULL,NULL,1),(10,10,'/menu/coffee_baverage/Sections/Image-9.png','2025-08-26 19:39:15','admin',NULL,NULL,1),(11,11,'/menu/coffee_baverage/Sections/Image-10.png','2025-08-26 19:39:15','admin',NULL,NULL,1),(12,12,'/menu/coffee_baverage/Sections/Image-11.png','2025-08-26 19:39:15','admin',NULL,NULL,1),(13,13,'/menu/food_snack/Sections/Image.png','2025-08-26 19:39:15','admin',NULL,NULL,1),(14,14,'/menu/food_snack/Sections/Image-1.png','2025-08-26 19:39:15','admin',NULL,NULL,1),(15,15,'/menu/food_snack/Sections/Image-2.png','2025-08-26 19:39:15','admin',NULL,NULL,1),(16,16,'/menu/food_snack/Sections/Image-3.png','2025-08-26 19:39:15','admin',NULL,NULL,1),(17,17,'/menu/food_snack/Sections/Image-4.png','2025-08-26 19:39:15','admin',NULL,NULL,1),(18,18,'/menu/food_snack/Sections/Image-5.png','2025-08-26 19:39:15','admin',NULL,NULL,1),(19,19,'/menu/food_snack/Sections/Image-6.png','2025-08-26 19:39:15','admin',NULL,NULL,1),(20,20,'/menu/food_snack/Sections/Image-7.png','2025-08-26 19:39:15','admin',NULL,NULL,1),(21,21,'/menu/food_snack/Sections/Image-8.png','2025-08-26 19:39:15','admin',NULL,NULL,1),(22,22,'/menu/food_snack/Sections/Image-9.png','2025-08-26 19:39:15','admin',NULL,NULL,1),(23,23,'/menu/food_snack/Sections/Image-10.png','2025-08-26 19:39:15','admin',NULL,NULL,1),(24,24,'/menu/food_snack/Sections/Image-11.png','2025-08-26 19:39:15','admin',NULL,NULL,1),(25,25,'/menu/at_home/Sections/Image.png','2025-08-26 19:39:15','admin',NULL,NULL,1),(26,26,'/menu/at_home/Sections/Image-1.png','2025-08-26 19:39:15','admin',NULL,NULL,1),(27,27,'/menu/at_home/Sections/Image-2.png','2025-08-26 19:39:15','admin',NULL,NULL,1),(28,28,'/menu/at_home/Sections/Image-3.png','2025-08-26 19:39:15','admin',NULL,NULL,1),(29,29,'/menu/at_home/Sections/Image-4.png','2025-08-26 19:39:15','admin',NULL,NULL,1),(30,30,'/menu/at_home/Sections/Image-5.png','2025-08-26 19:39:15','admin',NULL,NULL,1),(31,31,'/menu/at_home/Sections/Image-6.png','2025-08-26 19:39:15','admin',NULL,NULL,1),(32,32,'/menu/at_home/Sections/Image-7.png','2025-08-26 19:39:15','admin',NULL,NULL,1),(33,33,'/menu/at_home/Sections/Image-8.png','2025-08-26 19:39:15','admin',NULL,NULL,1),(34,34,'/menu/at_home/Sections/Image-9.png','2025-08-26 19:39:15','admin',NULL,NULL,1),(35,35,'/menu/at_home/Sections/Image-10.png','2025-08-26 19:39:15','admin',NULL,NULL,1),(36,36,'/menu/at_home/Sections/Image-11.png','2025-08-26 19:39:15','admin',NULL,NULL,1),(37,1,'/detail/Sections/image-1.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(38,1,'/detail/Sections/image-2.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(39,1,'/detail/Sections/image-3.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(40,2,'/detail/Sections/image-1.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(41,2,'/detail/Sections/image-2.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(42,2,'/detail/Sections/image-3.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(43,3,'/detail/Sections/image-1.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(44,3,'/detail/Sections/image-2.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(45,3,'/detail/Sections/image-3.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(46,4,'/detail/Sections/image-1.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(47,4,'/detail/Sections/image-2.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(48,4,'/detail/Sections/image-3.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(49,5,'/detail/Sections/image-1.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(50,5,'/detail/Sections/image-2.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(51,5,'/detail/Sections/image-3.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(52,6,'/detail/Sections/image-1.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(53,6,'/detail/Sections/image-2.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(54,6,'/detail/Sections/image-3.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(55,7,'/detail/Sections/image-1.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(56,7,'/detail/Sections/image-2.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(57,7,'/detail/Sections/image-3.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(58,8,'/detail/Sections/image-1.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(59,8,'/detail/Sections/image-2.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(60,8,'/detail/Sections/image-3.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(61,9,'/detail/Sections/image-1.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(62,9,'/detail/Sections/image-2.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(63,9,'/detail/Sections/image-3.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(64,10,'/detail/Sections/image-1.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(65,10,'/detail/Sections/image-2.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(66,10,'/detail/Sections/image-3.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(67,11,'/detail/Sections/image-1.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(68,11,'/detail/Sections/image-2.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(69,11,'/detail/Sections/image-3.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(70,12,'/detail/Sections/image-1.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(71,12,'/detail/Sections/image-2.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(72,12,'/detail/Sections/image-3.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(73,13,'/detail/Sections/image-1.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(74,13,'/detail/Sections/image-2.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(75,13,'/detail/Sections/image-3.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(76,14,'/detail/Sections/image-1.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(77,14,'/detail/Sections/image-2.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(78,14,'/detail/Sections/image-3.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(79,15,'/detail/Sections/image-1.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(80,15,'/detail/Sections/image-2.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(81,15,'/detail/Sections/image-3.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(82,16,'/detail/Sections/image-1.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(83,16,'/detail/Sections/image-2.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(84,16,'/detail/Sections/image-3.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(85,17,'/detail/Sections/image-1.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(86,17,'/detail/Sections/image-2.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(87,17,'/detail/Sections/image-3.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(88,18,'/detail/Sections/image-1.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(89,18,'/detail/Sections/image-2.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(90,18,'/detail/Sections/image-3.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(91,19,'/detail/Sections/image-1.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(92,19,'/detail/Sections/image-2.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(93,19,'/detail/Sections/image-3.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(94,20,'/detail/Sections/image-1.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(95,20,'/detail/Sections/image-2.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(96,20,'/detail/Sections/image-3.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(97,21,'/detail/Sections/image-1.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(98,21,'/detail/Sections/image-2.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(99,21,'/detail/Sections/image-3.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(100,22,'/detail/Sections/image-1.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(101,22,'/detail/Sections/image-2.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(102,22,'/detail/Sections/image-3.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(103,23,'/detail/Sections/image-1.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(104,23,'/detail/Sections/image-2.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(105,23,'/detail/Sections/image-3.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(106,24,'/detail/Sections/image-1.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(107,24,'/detail/Sections/image-2.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(108,24,'/detail/Sections/image-3.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(109,25,'/detail/Sections/image-1.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(110,25,'/detail/Sections/image-2.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(111,25,'/detail/Sections/image-3.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(112,26,'/detail/Sections/image-1.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(113,26,'/detail/Sections/image-2.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(114,26,'/detail/Sections/image-3.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(115,27,'/detail/Sections/image-1.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(116,27,'/detail/Sections/image-2.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(117,27,'/detail/Sections/image-3.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(118,28,'/detail/Sections/image-1.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(119,28,'/detail/Sections/image-2.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(120,28,'/detail/Sections/image-3.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(121,29,'/detail/Sections/image-1.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(122,29,'/detail/Sections/image-2.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(123,29,'/detail/Sections/image-3.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(124,30,'/detail/Sections/image-1.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(125,30,'/detail/Sections/image-2.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(126,30,'/detail/Sections/image-3.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(127,31,'/detail/Sections/image-1.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(128,31,'/detail/Sections/image-2.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(129,31,'/detail/Sections/image-3.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(130,32,'/detail/Sections/image-1.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(131,32,'/detail/Sections/image-2.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(132,32,'/detail/Sections/image-3.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(133,33,'/detail/Sections/image-1.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(134,33,'/detail/Sections/image-2.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(135,33,'/detail/Sections/image-3.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(136,34,'/detail/Sections/image-1.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(137,34,'/detail/Sections/image-2.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(138,34,'/detail/Sections/image-3.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(139,35,'/detail/Sections/image-1.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(140,35,'/detail/Sections/image-2.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(141,35,'/detail/Sections/image-3.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(142,36,'/detail/Sections/image-1.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(143,36,'/detail/Sections/image-2.png','2025-08-28 19:06:17','admin',NULL,NULL,0),(144,36,'/detail/Sections/image-3.png','2025-08-28 19:06:17','admin',NULL,NULL,0);
/*!40000 ALTER TABLE `product_images` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `products`
--

DROP TABLE IF EXISTS `products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `products` (
                            `product_id` bigint NOT NULL AUTO_INCREMENT,
                            `name` varchar(255) NOT NULL,
                            `description` text,
                            `price` decimal(10,2) NOT NULL,
                            `old_price` decimal(10,2) DEFAULT NULL,
                            `is_available_at_web` tinyint(1) DEFAULT '1',
                            `quantity` int DEFAULT '30',
                            `category` enum('coffee_baverage','food_snack','at_home') NOT NULL DEFAULT 'coffee_baverage',
                            `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            `created_by` varchar(20) NOT NULL,
                            `updated_at` timestamp NULL DEFAULT NULL,
                            `updated_by` varchar(20) DEFAULT NULL,
                            PRIMARY KEY (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `products`
--

LOCK TABLES `products` WRITE;
/*!40000 ALTER TABLE `products` DISABLE KEYS */;
INSERT INTO `products` VALUES (1,'ristretto bianco','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,6.00,0,9,'coffee_baverage','2025-08-26 19:38:46','admin','2025-09-17 00:48:04','System'),(2,'iced creamy latte','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,6.00,0,14,'coffee_baverage','2025-08-26 19:38:46','admin','2025-10-05 04:39:52','System'),(3,'cappucino','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,6.00,1,28,'coffee_baverage','2025-08-26 19:38:46','admin','2025-09-25 15:42:56','System'),(4,'iced long black','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,6.00,1,27,'coffee_baverage','2025-08-26 19:38:46','admin','2025-09-25 15:36:03','System'),(5,'milk coffee regal','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,6.00,1,25,'coffee_baverage','2025-08-26 19:38:46','admin','2025-10-19 05:10:35','System'),(6,'orange juice','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,6.00,1,28,'coffee_baverage','2025-08-26 19:38:46','admin','2025-09-21 15:19:33','System'),(7,'soda beverage','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,6.00,1,28,'coffee_baverage','2025-08-26 19:38:46','admin','2025-09-25 15:41:35','System'),(8,'iced coffee with milk','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,6.00,1,25,'coffee_baverage','2025-08-26 19:38:46','admin','2025-09-17 00:48:04','System'),(9,'iced americano','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,5.00,1,30,'coffee_baverage','2025-08-26 19:38:46','admin',NULL,NULL),(10,'vegan iced latte','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,5.00,1,30,'coffee_baverage','2025-08-26 19:38:46','admin',NULL,NULL),(11,'iced chocolate','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,5.00,1,30,'coffee_baverage','2025-08-26 19:38:46','admin',NULL,NULL),(12,'autumnal coffee','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,5.00,1,30,'coffee_baverage','2025-08-26 19:38:46','admin',NULL,NULL),(13,'seafood lunch','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,5.00,1,30,'food_snack','2025-08-26 19:38:46','admin',NULL,NULL),(14,'french toast with sugar','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,5.00,1,29,'food_snack','2025-08-26 19:38:46','admin','2025-09-10 01:00:25',NULL),(15,'chocolate croissant','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,6.00,1,15,'food_snack','2025-08-26 19:38:46','admin','2025-09-10 18:56:45',NULL),(16,'potato wedges','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,6.00,1,30,'food_snack','2025-08-26 19:38:46','admin',NULL,NULL),(17,'brownies','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,5.00,1,29,'food_snack','2025-08-26 19:38:46','admin','2025-09-10 00:37:46',NULL),(18,'banana cake','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,5.00,1,27,'food_snack','2025-08-26 19:38:46','admin','2025-09-10 00:33:58',NULL),(19,'sandwiches and pickles','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,5.00,1,30,'food_snack','2025-08-26 19:38:46','admin',NULL,NULL),(20,'spaghetti bolognese','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,5.00,1,29,'food_snack','2025-08-26 19:38:46','admin','2025-09-10 01:06:40',NULL),(21,'sandwich vegan','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,5.00,1,30,'food_snack','2025-08-26 19:38:46','admin',NULL,NULL),(22,'eggs benedict burger','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,5.00,1,30,'food_snack','2025-08-26 19:38:46','admin',NULL,NULL),(23,'corn cheese sandwich','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,6.00,1,30,'food_snack','2025-08-26 19:38:46','admin',NULL,NULL),(24,'buttermilk waffle','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,5.00,1,30,'food_snack','2025-08-26 19:38:46','admin',NULL,NULL),(25,'at home house blend','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,5.00,1,30,'at_home','2025-08-26 19:38:46','admin',NULL,NULL),(26,'at home arabica','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,5.00,1,30,'at_home','2025-08-26 19:38:46','admin',NULL,NULL),(27,'at home classic','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,5.00,1,30,'at_home','2025-08-26 19:38:46','admin',NULL,NULL),(28,'white mug','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,5.00,1,30,'at_home','2025-08-26 19:38:46','admin',NULL,NULL),(29,'at home kalosi','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,5.00,1,28,'at_home','2025-08-26 19:38:46','admin','2025-09-09 23:48:30',NULL),(30,'at home luwak','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,5.00,1,30,'at_home','2025-08-26 19:38:46','admin',NULL,NULL),(31,'at home robusta','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,5.00,1,28,'at_home','2025-08-26 19:38:46','admin','2025-09-10 00:37:46',NULL),(32,'coffee temper 58 MM','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,5.00,1,30,'at_home','2025-08-26 19:38:46','admin',NULL,NULL),(33,'french press 9 cups','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',25.00,25.00,1,10,'at_home','2025-08-26 19:38:46','admin',NULL,NULL),(34,'glass tea pot teiera (6 cups)','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',13.00,13.00,1,10,'at_home','2025-08-26 19:38:46','admin',NULL,NULL),(35,'french press 3 cup','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',20.00,20.00,1,30,'at_home','2025-08-26 19:38:46','admin',NULL,NULL),(36,'moka pot','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',20.00,20.00,1,30,'at_home','2025-08-26 19:38:46','admin',NULL,NULL);
/*!40000 ALTER TABLE `products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `promo_products`
--

DROP TABLE IF EXISTS `promo_products`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `promo_products` (
                                  `promo_id` bigint NOT NULL,
                                  `product_id` bigint NOT NULL,
                                  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  `created_by` varchar(20) NOT NULL,
                                  `updated_at` timestamp NULL DEFAULT NULL,
                                  `updated_by` varchar(20) DEFAULT NULL,
                                  PRIMARY KEY (`promo_id`,`product_id`),
                                  KEY `product_id` (`product_id`),
                                  CONSTRAINT `promo_products_ibfk_1` FOREIGN KEY (`promo_id`) REFERENCES `promos` (`id`),
                                  CONSTRAINT `promo_products_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `promo_products`
--

LOCK TABLES `promo_products` WRITE;
/*!40000 ALTER TABLE `promo_products` DISABLE KEYS */;
INSERT INTO `promo_products` VALUES (1,1,'2025-08-31 03:00:02','system',NULL,NULL),(1,5,'2025-08-31 03:00:59','system',NULL,NULL),(1,6,'2025-08-31 03:00:59','system',NULL,NULL),(1,7,'2025-08-31 03:00:59','system',NULL,NULL),(1,9,'2025-08-31 03:00:59','system',NULL,NULL),(1,11,'2025-08-31 03:00:59','system',NULL,NULL),(1,13,'2025-08-31 03:01:23','system',NULL,NULL),(1,15,'2025-08-31 03:01:23','system',NULL,NULL),(1,17,'2025-08-31 03:01:23','system',NULL,NULL),(1,21,'2025-08-31 03:01:23','system',NULL,NULL),(1,22,'2025-08-31 03:01:23','system',NULL,NULL),(1,23,'2025-08-31 03:01:58','system',NULL,NULL),(1,25,'2025-08-31 03:01:58','system',NULL,NULL),(1,27,'2025-08-31 03:01:58','system',NULL,NULL),(1,31,'2025-08-31 03:01:58','system',NULL,NULL),(1,32,'2025-08-31 03:01:58','system',NULL,NULL),(1,33,'2025-08-31 03:01:58','system',NULL,NULL),(1,35,'2025-08-31 03:01:58','system',NULL,NULL),(2,1,'2025-08-31 03:00:02','system',NULL,NULL),(2,2,'2025-08-31 03:00:34','system',NULL,NULL),(2,6,'2025-08-31 03:00:59','system',NULL,NULL),(2,8,'2025-08-31 03:00:59','system',NULL,NULL),(2,10,'2025-08-31 03:00:59','system',NULL,NULL),(2,11,'2025-08-31 03:00:59','system',NULL,NULL),(2,14,'2025-08-31 03:01:23','system',NULL,NULL),(2,15,'2025-08-31 03:01:23','system',NULL,NULL),(2,17,'2025-08-31 03:01:23','system',NULL,NULL),(2,18,'2025-08-31 03:01:23','system',NULL,NULL),(2,22,'2025-08-31 03:01:23','system',NULL,NULL),(2,24,'2025-08-31 03:01:58','system',NULL,NULL),(2,25,'2025-08-31 03:01:58','system',NULL,NULL),(2,27,'2025-08-31 03:01:58','system',NULL,NULL),(2,28,'2025-08-31 03:01:58','system',NULL,NULL),(2,32,'2025-08-31 03:01:58','system',NULL,NULL),(2,34,'2025-08-31 03:01:58','system',NULL,NULL),(2,35,'2025-08-31 03:01:58','system',NULL,NULL),(3,1,'2025-08-31 03:00:02','system',NULL,NULL),(3,2,'2025-08-31 03:00:34','system',NULL,NULL),(3,3,'2025-08-31 03:00:59','system',NULL,NULL),(3,7,'2025-08-31 03:00:59','system',NULL,NULL),(3,12,'2025-08-31 03:00:59','system',NULL,NULL),(3,13,'2025-08-31 03:01:23','system',NULL,NULL),(3,16,'2025-08-31 03:01:23','system',NULL,NULL),(3,17,'2025-08-31 03:01:23','system',NULL,NULL),(3,18,'2025-08-31 03:01:23','system',NULL,NULL),(3,19,'2025-08-31 03:01:23','system',NULL,NULL),(3,23,'2025-08-31 03:01:58','system',NULL,NULL),(3,26,'2025-08-31 03:01:58','system',NULL,NULL),(3,27,'2025-08-31 03:01:58','system',NULL,NULL),(3,28,'2025-08-31 03:01:58','system',NULL,NULL),(3,29,'2025-08-31 03:01:58','system',NULL,NULL),(3,33,'2025-08-31 03:01:58','system',NULL,NULL),(3,36,'2025-08-31 03:01:58','system',NULL,NULL),(4,2,'2025-08-31 03:00:34','system',NULL,NULL),(4,3,'2025-08-31 03:00:59','system',NULL,NULL),(4,4,'2025-08-31 03:00:59','system',NULL,NULL),(4,8,'2025-08-31 03:00:59','system',NULL,NULL),(4,9,'2025-08-31 03:00:59','system',NULL,NULL),(4,12,'2025-08-31 03:00:59','system',NULL,NULL),(4,13,'2025-08-31 03:01:23','system',NULL,NULL),(4,15,'2025-08-31 03:01:23','system',NULL,NULL),(4,18,'2025-08-31 03:01:23','system',NULL,NULL),(4,19,'2025-08-31 03:01:23','system',NULL,NULL),(4,20,'2025-08-31 03:01:23','system',NULL,NULL),(4,24,'2025-08-31 03:01:58','system',NULL,NULL),(4,25,'2025-08-31 03:01:58','system',NULL,NULL),(4,28,'2025-08-31 03:01:58','system',NULL,NULL),(4,29,'2025-08-31 03:01:58','system',NULL,NULL),(4,30,'2025-08-31 03:01:58','system',NULL,NULL),(4,34,'2025-08-31 03:01:58','system',NULL,NULL),(4,35,'2025-08-31 03:01:58','system',NULL,NULL),(5,3,'2025-08-31 03:00:59','system',NULL,NULL),(5,4,'2025-08-31 03:00:59','system',NULL,NULL),(5,5,'2025-08-31 03:00:59','system',NULL,NULL),(5,7,'2025-08-31 03:00:59','system',NULL,NULL),(5,10,'2025-08-31 03:00:59','system',NULL,NULL),(5,11,'2025-08-31 03:00:59','system',NULL,NULL),(5,14,'2025-08-31 03:01:23','system',NULL,NULL),(5,16,'2025-08-31 03:01:23','system',NULL,NULL),(5,19,'2025-08-31 03:01:23','system',NULL,NULL),(5,20,'2025-08-31 03:01:23','system',NULL,NULL),(5,21,'2025-08-31 03:01:23','system',NULL,NULL),(5,23,'2025-08-31 03:01:58','system',NULL,NULL),(5,26,'2025-08-31 03:01:58','system',NULL,NULL),(5,29,'2025-08-31 03:01:58','system',NULL,NULL),(5,30,'2025-08-31 03:01:58','system',NULL,NULL),(5,31,'2025-08-31 03:01:58','system',NULL,NULL),(5,33,'2025-08-31 03:01:58','system',NULL,NULL),(5,36,'2025-08-31 03:01:58','system',NULL,NULL),(6,4,'2025-08-31 03:00:59','system',NULL,NULL),(6,5,'2025-08-31 03:00:59','system',NULL,NULL),(6,6,'2025-08-31 03:00:59','system',NULL,NULL),(6,8,'2025-08-31 03:00:59','system',NULL,NULL),(6,9,'2025-08-31 03:00:59','system',NULL,NULL),(6,10,'2025-08-31 03:00:59','system',NULL,NULL),(6,12,'2025-08-31 03:00:59','system',NULL,NULL),(6,14,'2025-08-31 03:01:23','system',NULL,NULL),(6,16,'2025-08-31 03:01:23','system',NULL,NULL),(6,20,'2025-08-31 03:01:23','system',NULL,NULL),(6,21,'2025-08-31 03:01:23','system',NULL,NULL),(6,22,'2025-08-31 03:01:23','system',NULL,NULL),(6,24,'2025-08-31 03:01:58','system',NULL,NULL),(6,26,'2025-08-31 03:01:58','system',NULL,NULL),(6,30,'2025-08-31 03:01:58','system',NULL,NULL),(6,31,'2025-08-31 03:01:58','system',NULL,NULL),(6,32,'2025-08-31 03:01:58','system',NULL,NULL),(6,34,'2025-08-31 03:01:58','system',NULL,NULL),(6,36,'2025-08-31 03:01:58','system',NULL,NULL);
/*!40000 ALTER TABLE `promo_products` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `promos`
--

DROP TABLE IF EXISTS `promos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `promos` (
                          `id` bigint NOT NULL AUTO_INCREMENT,
                          `code` varchar(50) NOT NULL,
                          `title` varchar(255) NOT NULL,
                          `description` text,
                          `discount_type` enum('percentage','fixed','free_shipping') DEFAULT NULL,
                          `discount_value` decimal(10,2) DEFAULT NULL,
                          `start_at` timestamp NULL DEFAULT NULL,
                          `end_at` timestamp NULL DEFAULT NULL,
                          `is_active` tinyint(1) DEFAULT '1',
                          `status` varchar(20) NOT NULL DEFAULT 'ACTIVE',
                          `minimum_order_amount` decimal(10,2) DEFAULT NULL,
                          `max_total_uses` int DEFAULT NULL,
                          `max_uses_per_user` int DEFAULT NULL,
                          `usage_count` int NOT NULL DEFAULT '0',
                          `eligible_category` varchar(50) DEFAULT NULL,
                          `restricted_user_id` bigint DEFAULT NULL,
                          `stackable` tinyint(1) NOT NULL DEFAULT '0',
                          `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          `created_by` varchar(20) NOT NULL,
                          `updated_at` timestamp NULL DEFAULT NULL,
                          `updated_by` varchar(20) DEFAULT NULL,
                          PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `promos`
--

LOCK TABLES `promos` WRITE;
/*!40000 ALTER TABLE `promos` DISABLE KEYS */;
INSERT INTO `promos` (`id`,`code`,`title`,`description`,`discount_type`,`discount_value`,`start_at`,`end_at`,`is_active`,`created_at`,`created_by`,`updated_at`,`updated_by`) VALUES (1,'CASHBACK25','Cashback $2.5,00','Ends in 5 minutes!','fixed',2.50,'2025-08-31 02:57:19','2026-09-01 03:27:11',1,'2025-08-31 02:57:19','system',NULL,NULL),(2,'DISKON50','Diskon 50% With Credit or Debit Card','Ends in 20 minutes!','percentage',50.00,'2025-08-31 02:57:19','2026-09-01 03:27:11',1,'2025-08-31 02:57:19','system',NULL,NULL),(3,'FREESHIP2','Free shipping $2,00','Ends in 10 minutes!','free_shipping',2.00,'2025-08-31 02:57:19','2026-09-01 03:27:11',1,'2025-08-31 02:57:19','system',NULL,NULL),(4,'SUMMER10','Summer Sale 10% OFF','Ends in 1 hour!','percentage',10.00,'2025-08-31 02:57:19','2025-09-01 03:27:11',1,'2025-08-31 02:57:19','system',NULL,NULL),(5,'HOLIDAY15','Holiday Special $15 OFF','Ends in 2 hours!','fixed',15.00,'2025-08-31 02:57:19','2025-09-01 03:27:11',1,'2025-08-31 02:57:19','system',NULL,NULL),(6,'NEWUSER5','New User $5 OFF','Ends in 30 minutes!','fixed',5.00,'2025-08-31 02:57:19','2025-09-01 03:27:11',1,'2025-08-31 02:57:19','system',NULL,NULL);
/*!40000 ALTER TABLE `promos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
                         `role_id` bigint NOT NULL AUTO_INCREMENT,
                         `name` varchar(50) NOT NULL,
                         PRIMARY KEY (`role_id`),
                         UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (2,'ROLE_ADMIN'),(1,'ROLE_USER');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ship`
--

DROP TABLE IF EXISTS `ship`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ship` (
                        `method_id` bigint NOT NULL AUTO_INCREMENT,
                        `method_name` varchar(100) NOT NULL,
                        `code` varchar(5) NOT NULL,
                        `expected_arrival` varchar(100) NOT NULL,
                        `price` decimal(10,2) NOT NULL,
                        `is_active` tinyint(1) DEFAULT '1',
                        `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        `created_by` varchar(20) NOT NULL,
                        `updated_at` timestamp NULL DEFAULT NULL,
                        `updated_by` varchar(20) DEFAULT NULL,
                        PRIMARY KEY (`method_id`),
                        UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ship`
--

LOCK TABLES `ship` WRITE;
/*!40000 ALTER TABLE `ship` DISABLE KEYS */;
INSERT INTO `ship` VALUES (1,'Free Shipping','F','7 - 10 Business Days',0.00,1,'2025-09-03 03:01:38','system',NULL,NULL),(2,'Standard Shipping','S','4 - 6 Business Days',3.99,1,'2025-09-03 03:01:38','system',NULL,NULL),(3,'Express Shipping','E','2 - 3 Business Days',6.99,1,'2025-09-03 03:01:38','system',NULL,NULL),(4,'Instant Delivery','I','Same Day Delivery',9.99,1,'2025-09-03 03:01:38','system',NULL,NULL);
/*!40000 ALTER TABLE `ship` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `space`
--

DROP TABLE IF EXISTS `space`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `space` (
                         `space_id` bigint NOT NULL AUTO_INCREMENT,
                         `name` varchar(255) NOT NULL,
                         `type` varchar(50) NOT NULL,
                         `image` varchar(500) NOT NULL,
                         `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         `created_by` varchar(20) NOT NULL,
                         `updated_at` timestamp NULL DEFAULT NULL,
                         `updated_by` varchar(20) DEFAULT NULL,
                         PRIMARY KEY (`space_id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `space`
--

LOCK TABLES `space` WRITE;
/*!40000 ALTER TABLE `space` DISABLE KEYS */;
INSERT INTO `space` VALUES (1,'white wall','workspace','/home/space/Sections/Image1.png','2025-08-24 15:17:41','system',NULL,NULL),(2,'long window','workspace','/home/space/Sections/Image2.png','2025-08-24 15:17:41','system',NULL,NULL),(3,'gengs space','workspace','/home/space/Sections/Image3.png','2025-08-24 15:17:41','system',NULL,NULL),(4,'seminar area','event space','/home/space/Sections/Image4.png','2025-08-24 15:17:41','system',NULL,NULL),(5,'center area','event space','/home/space/Sections/Image5.png','2025-08-24 15:17:41','system',NULL,NULL),(6,'aquarium','meeting space','/home/space/Sections/Image6.png','2025-08-24 15:17:41','system',NULL,NULL),(7,'roftop','workspace','/home/space/Sections/Image7.png','2025-08-24 15:17:41','system',NULL,NULL),(8,'hamble space','meeting space','/home/space/Sections/Image8.png','2025-08-24 15:17:41','system',NULL,NULL);
/*!40000 ALTER TABLE `space` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
                        `user_id` bigint NOT NULL AUTO_INCREMENT,
                        `email` varchar(100) NOT NULL,
                        `password` varchar(255) NOT NULL,
                        `phone` varchar(20) DEFAULT NULL,
                        `token_version` int NOT NULL DEFAULT '0',
                        `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        `created_by` varchar(20) NOT NULL,
                        `updated_at` timestamp NULL DEFAULT NULL,
                        `updated_by` varchar(20) DEFAULT NULL,
                        `user_name` varchar(255) NOT NULL,
                        `guest` tinyint(1) DEFAULT '0',
                        PRIMARY KEY (`user_id`),
                        UNIQUE KEY `email` (`email`),
                        UNIQUE KEY `user_name` (`user_name`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table `user` intentionally seeded with no rows in this baseline
-- (original dump contained real customer data; omitted from source control)
--

--
-- Table structure for table `user_role`
--

DROP TABLE IF EXISTS `user_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_role` (
                             `user_id` bigint NOT NULL,
                             `role_id` bigint NOT NULL,
                             `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             `created_by` varchar(20) NOT NULL,
                             `updated_at` timestamp NULL DEFAULT NULL,
                             `updated_by` varchar(20) DEFAULT NULL,
                             PRIMARY KEY (`user_id`,`role_id`),
                             KEY `role_id` (`role_id`),
                             CONSTRAINT `user_role_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON DELETE CASCADE,
                             CONSTRAINT `user_role_ibfk_2` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table `user_role` intentionally seeded with no rows in this baseline
-- (original dump contained real customer data; omitted from source control)
--
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

CREATE TABLE chat_conversation
(
    conversation_id BIGINT AUTO_INCREMENT PRIMARY KEY,

    customer_id     BIGINT      NOT NULL,
    admin_id        BIGINT NULL,

    status          VARCHAR(20) NOT NULL DEFAULT 'OPEN', -- OPEN | CLOSED | PENDING
    customer_last_read_message_id BIGINT DEFAULT NULL,
    admin_last_read_message_id    BIGINT DEFAULT NULL,

    created_at      TIMESTAMP            DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by      VARCHAR(20) NOT NULL,
    updated_at      TIMESTAMP            DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by      VARCHAR(20)          DEFAULT NULL,

    CONSTRAINT fk_chat_customer FOREIGN KEY (customer_id) REFERENCES user (user_id),
    CONSTRAINT fk_chat_admin FOREIGN KEY (admin_id) REFERENCES user (user_id)
);

CREATE TABLE chat_message
(
    message_id      BIGINT AUTO_INCREMENT PRIMARY KEY,

    conversation_id BIGINT                                NOT NULL,

    sender_id       BIGINT                                NOT NULL,
    sender_type     VARCHAR(20)                           NOT NULL, -- USER | ADMIN | AI

    content         TEXT                                  NOT NULL,

    created_at      TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by      VARCHAR(20)                           NOT NULL,
    updated_at      TIMESTAMP   DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by      VARCHAR(20) DEFAULT NULL,
    sender_name     VARCHAR(255) DEFAULT NULL,

    CONSTRAINT fk_message_conversation
        FOREIGN KEY (conversation_id) REFERENCES chat_conversation (conversation_id)
            ON DELETE CASCADE,

    CONSTRAINT fk_message_sender
        FOREIGN KEY (sender_id) REFERENCES user (user_id)
);

CREATE TABLE IF NOT EXISTS payment_webhook_event
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    provider   VARCHAR(20)  NOT NULL,
    event_id   VARCHAR(120) NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_payment_webhook_provider_event UNIQUE (provider, event_id)
    );

CREATE TABLE promo_usage
(
    promo_usage_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    promo_id       BIGINT                                NOT NULL,
    user_id        BIGINT                                NOT NULL,
    order_id       BIGINT                                NOT NULL,
    used_at        TIMESTAMP   DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_by     VARCHAR(20)                           NOT NULL,
    CONSTRAINT fk_promo_usage_promo FOREIGN KEY (promo_id) REFERENCES promos (id),
    CONSTRAINT uq_promo_usage_promo_user_order UNIQUE (promo_id, user_id, order_id)
);
-- Dump completed on 2026-03-28 20:56:30

