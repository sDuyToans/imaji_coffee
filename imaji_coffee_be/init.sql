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
-- Dumping data for table `addresses`
--

LOCK TABLES `addresses` WRITE;
/*!40000 ALTER TABLE `addresses` DISABLE KEYS */;
INSERT INTO `addresses` VALUES (1,18,'LE DUY TOAN NGUYEN','828 Winterton Way','Mississauga','ON','L5V 2A5','CA','11231231231',1,'2025-09-21 00:43:29','System','2025-09-21 00:43:28',NULL,''),(2,18,'LE DUY TOAN NGUYEN','828 Winterton Way','Mississauga','ON','L5V 2A5','AD','11231231231',0,'2025-09-21 15:19:33','System','2025-09-21 15:19:32',NULL,''),(6,21,'LE DUY TOAN NGUYEN','828 Winterton Way','Mississauga','ON','L5V 2A5','AS','11231231231',1,'2025-09-25 15:36:03','System','2025-09-25 15:36:02',NULL,''),(7,21,'LE DUY TOAN NGUYEN','828 Winterton Way','Mississauga','ON','L5V 2A5','BN','11231231231',0,'2025-09-25 15:41:35','System','2025-09-25 15:41:34',NULL,''),(8,21,'LE DUY TOAN NGUYEN','828 Winterton Way','Mississauga','ON','L5V 2A5','AL','11231231231',0,'2025-09-25 15:42:56','System','2025-09-25 15:42:55',NULL,''),(9,21,'LE DUY TOAN NGUYEN','828 Winterton Way','Mississauga','ON','L5V 2A5','AG','11231231231',0,'2025-09-25 18:52:07','System','2025-09-25 18:52:07',NULL,''),(10,21,'LE DUY TOAN NGUYEN','828 Winterton Way','Mississauga','ON','L5V 2A5','BW','11231231231',0,'2025-10-05 04:24:05','System','2025-10-05 04:24:05',NULL,''),(11,21,'','','','','','','',0,'2025-10-19 05:10:35','System','2025-10-19 05:10:35',NULL,'');
/*!40000 ALTER TABLE `addresses` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `cart`
--

LOCK TABLES `cart` WRITE;
/*!40000 ALTER TABLE `cart` DISABLE KEYS */;
INSERT INTO `cart` VALUES (1,17,NULL,2,'2025-09-16 21:33:12','System','2025-09-17 00:48:05','System'),(2,18,1,1,'2025-09-18 02:57:46','System','2025-09-21 15:21:36','System'),(3,19,NULL,1,'2025-09-22 18:51:49','System','2025-09-22 19:03:48','System'),(4,21,2,2,'2025-09-25 15:32:35','System','2025-10-19 05:01:47','System');
/*!40000 ALTER TABLE `cart` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `cart_item`
--

LOCK TABLES `cart_item` WRITE;
/*!40000 ALTER TABLE `cart_item` DISABLE KEYS */;
INSERT INTO `cart_item` VALUES (18,3,1,1,'2025-09-17 00:48:13','System','2025-09-17 00:48:12',NULL),(24,2,3,1,'2025-09-22 18:51:49','System','2025-09-22 18:51:48',NULL),(34,5,4,1,'2025-10-19 03:18:38','System','2025-10-19 03:18:38',NULL);
/*!40000 ALTER TABLE `cart_item` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `order_items`
--

LOCK TABLES `order_items` WRITE;
/*!40000 ALTER TABLE `order_items` DISABLE KEYS */;
INSERT INTO `order_items` VALUES (1,4,1,'Coffee Beans',50.25,1,'2025-09-07 18:12:31','ADMIN','2025-09-07 18:12:30',NULL,'coffee_baverage',''),(2,4,2,'Espresso Machine',50.25,1,'2025-09-07 18:12:31','ADMIN','2025-09-07 18:12:30',NULL,'coffee_baverage',''),(3,7,3,'cappucino',5.00,30,'2025-09-07 20:04:52','ADMIN','2025-09-07 20:04:51',NULL,'coffee_baverage',''),(4,8,3,'cappucino',5.00,30,'2025-09-07 20:27:10','ADMIN','2025-09-07 20:27:10',NULL,'coffee_baverage',''),(5,9,7,'soda beverage',5.00,30,'2025-09-07 21:33:18','ADMIN','2025-09-07 21:33:17',NULL,'coffee_baverage',''),(6,9,1,'ristretto bianco',5.00,30,'2025-09-07 21:33:18','ADMIN','2025-09-07 21:33:17',NULL,'coffee_baverage',''),(7,10,1,'ristretto bianco',5.00,30,'2025-09-08 19:04:56','ADMIN','2025-09-08 19:04:56',NULL,'coffee_baverage',''),(8,10,19,'sandwiches and pickles',5.00,30,'2025-09-08 19:04:56','ADMIN','2025-09-08 19:04:56',NULL,'coffee_baverage',''),(9,24,2,'iced creamy latte',5.00,1,'2025-09-09 23:48:30','ADMIN','2025-09-09 23:48:30',NULL,'coffee_baverage','/menu/coffee_baverage/Sections/Image-1.png'),(10,24,8,'iced coffee with milk',5.00,2,'2025-09-09 23:48:30','ADMIN','2025-09-09 23:48:30',NULL,'coffee_baverage','/menu/coffee_baverage/Sections/Image-7.png'),(11,24,18,'banana cake',5.00,1,'2025-09-09 23:48:30','ADMIN','2025-09-09 23:48:30',NULL,'food_snack','/menu/food_snack/Sections/Image-5.png'),(12,24,29,'at home kalosi',5.00,2,'2025-09-09 23:48:30','ADMIN','2025-09-09 23:48:30',NULL,'at_home','/menu/at_home/Sections/Image-4.png'),(13,25,2,'iced creamy latte',5.00,4,'2025-09-10 00:33:58','ADMIN','2025-09-10 00:33:57',NULL,'coffee_baverage','/menu/coffee_baverage/Sections/Image-1.png'),(14,25,18,'banana cake',5.00,2,'2025-09-10 00:33:58','ADMIN','2025-09-10 00:33:57',NULL,'food_snack','/menu/food_snack/Sections/Image-5.png'),(15,25,5,'milk coffee regal',5.00,2,'2025-09-10 00:33:58','ADMIN','2025-09-10 00:33:57',NULL,'coffee_baverage','/menu/coffee_baverage/Sections/Image-4.png'),(16,26,5,'milk coffee regal',5.00,2,'2025-09-10 00:37:46','ADMIN','2025-09-10 00:37:46',NULL,'coffee_baverage','/menu/coffee_baverage/Sections/Image-4.png'),(17,26,17,'brownies',5.00,1,'2025-09-10 00:37:46','ADMIN','2025-09-10 00:37:46',NULL,'food_snack','/menu/food_snack/Sections/Image-4.png'),(18,26,31,'at home robusta',5.00,2,'2025-09-10 00:37:46','ADMIN','2025-09-10 00:37:46',NULL,'at_home','/menu/at_home/Sections/Image-6.png'),(19,27,2,'iced creamy latte',5.00,1,'2025-09-10 01:00:25','ADMIN','2025-09-10 01:00:25',NULL,'coffee_baverage','/menu/coffee_baverage/Sections/Image-1.png'),(20,27,14,'french toast with sugar',5.00,1,'2025-09-10 01:00:25','ADMIN','2025-09-10 01:00:25',NULL,'food_snack','/menu/food_snack/Sections/Image-1.png'),(21,28,3,'cappucino',5.00,1,'2025-09-10 01:06:40','ADMIN','2025-09-10 01:06:39',NULL,'coffee_baverage','/menu/coffee_baverage/Sections/Image-2.png'),(22,28,20,'spaghetti bolognese',5.00,1,'2025-09-10 01:06:40','ADMIN','2025-09-10 01:06:39',NULL,'food_snack','/menu/food_snack/Sections/Image-7.png'),(23,29,2,'iced creamy latte',5.00,2,'2025-09-10 18:56:45','ADMIN','2025-09-10 18:56:45',NULL,'coffee_baverage','/menu/coffee_baverage/Sections/Image-1.png'),(24,29,15,'chocolate croissant',5.00,15,'2025-09-10 18:56:45','ADMIN','2025-09-10 18:56:45',NULL,'food_snack','/menu/food_snack/Sections/Image-2.png'),(25,30,1,'ristretto bianco',5.00,7,'2025-09-17 00:38:08','System','2025-09-17 00:38:08',NULL,'coffee_baverage','/menu/coffee_baverage/Sections/Image.png'),(26,30,8,'iced coffee with milk',5.00,1,'2025-09-17 00:38:08','System','2025-09-17 00:38:08',NULL,'coffee_baverage','/menu/coffee_baverage/Sections/Image-7.png'),(27,31,8,'iced coffee with milk',5.00,1,'2025-09-17 00:43:48','System','2025-09-17 00:43:48',NULL,'coffee_baverage','/menu/coffee_baverage/Sections/Image-7.png'),(28,31,1,'ristretto bianco',5.00,7,'2025-09-17 00:43:48','System','2025-09-17 00:43:48',NULL,'coffee_baverage','/menu/coffee_baverage/Sections/Image.png'),(29,32,8,'iced coffee with milk',5.00,1,'2025-09-17 00:48:04','System','2025-09-17 00:48:03',NULL,'coffee_baverage','/menu/coffee_baverage/Sections/Image-7.png'),(30,32,1,'ristretto bianco',5.00,7,'2025-09-17 00:48:04','System','2025-09-17 00:48:03',NULL,'coffee_baverage','/menu/coffee_baverage/Sections/Image.png'),(31,33,6,'orange juice',5.00,1,'2025-09-19 14:47:23','System','2025-09-19 14:47:23',NULL,'coffee_baverage','/menu/coffee_baverage/Sections/Image-5.png'),(36,38,7,'soda beverage',5.00,1,'2025-09-21 00:43:29','System','2025-09-21 00:43:28',NULL,'coffee_baverage','/menu/coffee_baverage/Sections/Image-6.png'),(37,39,6,'orange juice',5.00,1,'2025-09-21 15:19:33','System','2025-09-21 15:19:32',NULL,'coffee_baverage','/menu/coffee_baverage/Sections/Image-5.png'),(38,40,2,'iced creamy latte',5.00,1,'2025-09-21 15:21:36','System','2025-09-21 15:21:35',NULL,'coffee_baverage','/menu/coffee_baverage/Sections/Image-1.png'),(44,44,2,'iced creamy latte',5.00,1,'2025-09-25 15:36:03','System','2025-09-25 15:36:02',NULL,'coffee_baverage','/menu/coffee_baverage/Sections/Image-1.png'),(45,44,4,'iced long black',5.00,3,'2025-09-25 15:36:03','System','2025-09-25 15:36:02',NULL,'coffee_baverage','/menu/coffee_baverage/Sections/Image-3.png'),(46,45,7,'soda beverage',5.00,1,'2025-09-25 15:41:35','System','2025-09-25 15:41:34',NULL,'coffee_baverage','/menu/coffee_baverage/Sections/Image-6.png'),(47,46,3,'cappucino',5.00,1,'2025-09-25 15:42:56','System','2025-09-25 15:42:55',NULL,'coffee_baverage','/menu/coffee_baverage/Sections/Image-2.png'),(48,47,2,'iced creamy latte',5.00,1,'2025-09-25 18:52:07','System','2025-09-25 18:52:07',NULL,'coffee_baverage','/menu/coffee_baverage/Sections/Image-1.png'),(49,48,2,'iced creamy latte',5.00,1,'2025-09-25 19:01:41','System','2025-09-25 19:01:40',NULL,'coffee_baverage','/menu/coffee_baverage/Sections/Image-1.png'),(50,49,2,'iced creamy latte',5.00,3,'2025-10-05 04:24:05','System','2025-10-05 04:24:05',NULL,'coffee_baverage','/menu/coffee_baverage/Sections/Image-1.png'),(51,50,2,'iced creamy latte',5.00,1,'2025-10-05 04:39:51','System','2025-10-05 04:39:51',NULL,'coffee_baverage','/menu/coffee_baverage/Sections/Image-1.png'),(52,51,5,'milk coffee regal',5.00,1,'2025-10-19 05:10:35','System','2025-10-19 05:10:35',NULL,'coffee_baverage','/menu/coffee_baverage/Sections/Image-4.png');
/*!40000 ALTER TABLE `order_items` ENABLE KEYS */;
UNLOCK TABLES;

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
  `payment_intent_id` varchar(255) DEFAULT NULL,
  `payment_method` varchar(50) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `created_by` varchar(20) NOT NULL,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `updated_by` varchar(20) DEFAULT NULL,
  `tax_amount` decimal(10,2) DEFAULT '0.00',
  `shipping_amount` decimal(10,2) DEFAULT '0.00',
  `discount_amount` decimal(10,2) DEFAULT '0.00',
  `shipping_method` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES (1,1,'john.doe@example.com','{\"city\": \"Los Angeles\", \"name\": \"John Doe\", \"street\": \"123 Main St\", \"country\": \"US\", \"province\": \"California\", \"apartment\": \"Unit 4B\", \"postalCode\": \"90001\", \"phoneNumber\": \"1234567890\"}',120.50,'USD','PAID',NULL,NULL,'2025-09-07 18:07:45','ADMIN','2025-09-07 18:59:10','ADMIN',0.00,0.00,0.00,NULL),(2,1,'john.doe@example.com','{\"city\": \"Los Angeles\", \"name\": \"John Doe\", \"street\": \"123 Main St\", \"country\": \"US\", \"province\": \"California\", \"apartment\": \"Unit 4B\", \"postalCode\": \"90001\", \"phoneNumber\": \"1234567890\"}',120.50,'USD','PENDING',NULL,NULL,'2025-09-07 18:08:48','ADMIN','2025-09-07 18:08:47',NULL,0.00,0.00,0.00,NULL),(3,1,'john.doe@example.com','{\"city\": \"Los Angeles\", \"name\": \"John Doe\", \"street\": \"123 Main St\", \"country\": \"US\", \"province\": \"California\", \"apartment\": \"Unit 4B\", \"postalCode\": \"90001\", \"phoneNumber\": \"1234567890\"}',120.50,'USD','PENDING',NULL,NULL,'2025-09-07 18:09:12','ADMIN','2025-09-07 18:09:11',NULL,0.00,0.00,0.00,NULL),(4,1,'john.doe@example.com','{\"city\": \"Los Angeles\", \"name\": \"John Doe\", \"street\": \"123 Main St\", \"country\": \"US\", \"province\": \"California\", \"apartment\": \"Unit 4B\", \"postalCode\": \"90001\", \"phoneNumber\": \"1234567890\"}',120.50,'USD','PENDING',NULL,NULL,'2025-09-07 18:12:31','ADMIN','2025-09-07 18:12:30',NULL,0.00,0.00,0.00,NULL),(5,1,'test1@gmail.com','{\"city\": \"Mississauga\", \"name\": \"LE DUY TOAN NGUYEN\", \"street\": \"828 Winterton Way\", \"country\": \"ID\", \"province\": \"ON\", \"apartment\": \"\", \"postalCode\": \"L5V 2A5\", \"phoneNumber\": \"11231231231\"}',13.00,'USD','PENDING',NULL,NULL,'2025-09-07 20:01:14','ADMIN','2025-09-07 20:01:14',NULL,0.00,0.00,0.00,NULL),(6,1,'test1@gmail.com','{\"city\": \"Mississauga\", \"name\": \"LE DUY TOAN NGUYEN\", \"street\": \"828 Winterton Way\", \"country\": \"ID\", \"province\": \"ON\", \"apartment\": \"\", \"postalCode\": \"L5V 2A5\", \"phoneNumber\": \"11231231231\"}',13.00,'USD','PENDING',NULL,NULL,'2025-09-07 20:01:34','ADMIN','2025-09-07 20:01:33',NULL,0.00,0.00,0.00,NULL),(7,1,'test1@gmail.com','{\"city\": \"Mississauga\", \"name\": \"LE DUY TOAN NGUYEN\", \"street\": \"828 Winterton Way\", \"country\": \"ID\", \"province\": \"ON\", \"apartment\": \"\", \"postalCode\": \"L5V 2A5\", \"phoneNumber\": \"11231231231\"}',13.00,'USD','PENDING',NULL,NULL,'2025-09-07 20:04:52','ADMIN','2025-09-07 20:04:51',NULL,0.00,0.00,0.00,NULL),(8,1,'username@sheridancollege.ca','{\"city\": \"Mississauga\", \"name\": \"LE DUY TOAN NGUYEN\", \"street\": \"828 Winterton Way\", \"country\": \"AS\", \"province\": \"ON\", \"apartment\": \"\", \"postalCode\": \"L5V 2A5\", \"phoneNumber\": \"11231231231\"}',31.00,'USD','PAID','pi_3S4pB3578Xxwj3ov0NIW9m9F',NULL,'2025-09-07 20:27:10','ADMIN','2025-09-07 20:27:11',NULL,0.00,0.00,0.00,NULL),(9,1,'test1@gmail.com','{\"city\": \"Mississauga\", \"name\": \"LE DUY TOAN NGUYEN\", \"street\": \"828 Winterton Way\", \"country\": \"AG\", \"province\": \"ON\", \"apartment\": \"\", \"postalCode\": \"L5V 2A5\", \"phoneNumber\": \"11231231231\"}',18.00,'USD','PAID','pi_3S4qD3578Xxwj3ov1zWAssWD',NULL,'2025-09-07 21:33:18','ADMIN','2025-09-07 21:33:19',NULL,0.00,0.00,0.00,NULL),(10,1,'test@gmail.com','{\"city\": \"Mississauga\", \"name\": \"LE DUY TOAN NGUYEN\", \"street\": \"828 Winterton Way\", \"country\": \"AO\", \"province\": \"ON\", \"apartment\": \"\", \"postalCode\": \"L5V 2A5\", \"phoneNumber\": \"11231231231\"}',29.50,'USD','PAID','pi_3S5AN1578Xxwj3ov1MFQoLN0',NULL,'2025-09-08 19:04:56','ADMIN','2025-09-08 19:04:57',NULL,0.00,0.00,0.00,NULL),(11,1,'test2@gmail.com','{\"city\": \"Mississauga\", \"name\": \"test\", \"street\": \"828 Winterton Way\", \"country\": \"GM\", \"province\": \"ON\", \"apartment\": \"\", \"postalCode\": \"L5V 2A5\", \"phoneNumber\": \"11231231231\"}',25.50,'USD','PENDING','pi_3S5aiM578Xxwj3ov1Tpeso0P',NULL,'2025-09-09 23:12:43','ADMIN','2025-09-09 23:12:42',NULL,0.00,0.00,0.00,NULL),(12,1,'test2@gmail.com','{\"city\": \"Mississauga\", \"name\": \"test\", \"street\": \"828 Winterton Way\", \"country\": \"GM\", \"province\": \"ON\", \"apartment\": \"\", \"postalCode\": \"L5V 2A5\", \"phoneNumber\": \"11231231231\"}',25.50,'USD','PENDING','pi_3S5aii578Xxwj3ov1HZ75H5x',NULL,'2025-09-09 23:13:05','ADMIN','2025-09-09 23:13:05',NULL,0.00,0.00,0.00,NULL),(13,1,'test2@gmail.com','{\"city\": \"Mississauga\", \"name\": \"test\", \"street\": \"828 Winterton Way\", \"country\": \"GM\", \"province\": \"ON\", \"apartment\": \"\", \"postalCode\": \"L5V 2A5\", \"phoneNumber\": \"11231231231\"}',25.50,'USD','PENDING','pi_3S5ail578Xxwj3ov1tsonIPT',NULL,'2025-09-09 23:13:08','ADMIN','2025-09-09 23:13:07',NULL,0.00,0.00,0.00,NULL),(14,1,'test2@gmail.com','{\"city\": \"Mississauga\", \"name\": \"test\", \"street\": \"828 Winterton Way\", \"country\": \"GM\", \"province\": \"ON\", \"apartment\": \"\", \"postalCode\": \"L5V 2A5\", \"phoneNumber\": \"11231231231\"}',25.50,'USD','PENDING','pi_3S5ajV578Xxwj3ov1hL7MDoa',NULL,'2025-09-09 23:13:54','ADMIN','2025-09-09 23:13:53',NULL,0.00,0.00,0.00,NULL),(15,1,'test2@gmail.com','{\"city\": \"Mississauga\", \"name\": \"test\", \"street\": \"828 Winterton Way\", \"country\": \"GM\", \"province\": \"ON\", \"apartment\": \"\", \"postalCode\": \"L5V 2A5\", \"phoneNumber\": \"11231231231\"}',25.50,'USD','PENDING','pi_3S5al5578Xxwj3ov14fHKQfI',NULL,'2025-09-09 23:15:32','ADMIN','2025-09-09 23:15:31',NULL,0.00,0.00,0.00,NULL),(16,1,'test2@gmail.com','{\"city\": \"Mississauga\", \"name\": \"test\", \"street\": \"828 Winterton Way\", \"country\": \"GM\", \"province\": \"ON\", \"apartment\": \"\", \"postalCode\": \"L5V 2A5\", \"phoneNumber\": \"11231231231\"}',25.50,'USD','PENDING','pi_3S5amp578Xxwj3ov0YVkcSdg',NULL,'2025-09-09 23:17:38','ADMIN','2025-09-09 23:17:37',NULL,0.00,0.00,0.00,NULL),(24,1,'test1@gmail.com','{\"city\": \"Mississauga\", \"name\": \"LE DUY TOAN NGUYEN\", \"street\": \"828 Winterton Way\", \"country\": \"AO\", \"province\": \"ON\", \"apartment\": \"\", \"postalCode\": \"L5V 2A5\", \"phoneNumber\": \"11231231231\"}',25.50,'USD','PAID','pi_3S5bGz578Xxwj3ov1OYR3K3d',NULL,'2025-09-09 23:48:30','ADMIN','2025-09-09 23:48:31',NULL,0.00,0.00,0.00,NULL),(25,1,'myemal@sheridan.com','{\"city\": \"Mississauga\", \"name\": \"LE DUY TOAN NGUYEN\", \"street\": \"828 Winterton Way\", \"country\": \"BR\", \"province\": \"ON\", \"apartment\": \"\", \"postalCode\": \"L5V 2A5\", \"phoneNumber\": \"11231231231\"}',51.50,'USD','PAID','pi_3S5byz578Xxwj3ov17QVOxly',NULL,'2025-09-10 00:33:58','ADMIN','2025-09-10 00:33:59',NULL,NULL,NULL,NULL,NULL),(26,1,'test@gmail.com','{\"city\": \"Mississauga\", \"name\": \"LE DUY TOAN NGUYEN\", \"street\": \"828 Winterton Way\", \"country\": \"AG\", \"province\": \"ON\", \"apartment\": \"\", \"postalCode\": \"L5V 2A5\", \"phoneNumber\": \"11231231231\"}',35.00,'USD','PAID','pi_3S5c2g578Xxwj3ov1UMOaC2g',NULL,'2025-09-10 00:37:46','ADMIN','2025-09-10 00:37:48',NULL,2.50,7.50,0.00,NULL),(27,1,'test1@gmail.com','{\"city\": \"Mississauga\", \"name\": \"LE DUY TOAN NGUYEN\", \"street\": \"828 Winterton Way\", \"country\": \"AO\", \"province\": \"ON\", \"apartment\": \"\", \"postalCode\": \"L5V 2A5\", \"phoneNumber\": \"11231231232\"}',18.50,'USD','PAID','pi_3S5cOb578Xxwj3ov0WmlQUen',NULL,'2025-09-10 01:00:25','ADMIN','2025-09-10 01:00:26',NULL,1.00,7.50,0.00,NULL),(28,1,'test1@gmail.com','{\"city\": \"Mississauga\", \"name\": \"LE DUY TOAN NGUYEN\", \"street\": \"828 Winterton Way\", \"country\": \"CA\", \"province\": \"ON\", \"apartment\": \"\", \"postalCode\": \"L5V 2A5\", \"phoneNumber\": \"11231231231\"}',16.00,'USD','PAID','pi_3S5cUd578Xxwj3ov10rSlqTQ','card','2025-09-10 01:06:40','ADMIN','2025-09-10 01:06:41',NULL,1.00,7.50,2.50,'Regular Shipping 4 - 7 Days'),(29,1,'username@sheridancollege.ca','{\"city\": \"Mississauga\", \"name\": \"LE DUY TOAN NGUYEN\", \"street\": \"828 Winterton Way\", \"country\": \"EE\", \"province\": \"ON\", \"apartment\": \"\", \"postalCode\": \"L5V 2A5\", \"phoneNumber\": \"11231231231\"}',76.50,'USD','PAID','pi_3S5tCC578Xxwj3ov06WXlbm6','card','2025-09-10 18:56:45','ADMIN','2025-09-10 18:56:46',NULL,8.50,25.50,42.50,'Instant Delivery 2 - 3 Days'),(30,17,'test1@gmail.com','{\"city\": \"Mississauga\", \"name\": \"LE DUY TOAN NGUYEN\", \"street\": \"828 Winterton Way\", \"country\": \"GM\", \"province\": \"ON\", \"apartment\": \"\", \"postalCode\": \"L5V 2A5\", \"phoneNumber\": \"11231231231\"}',46.50,'USD','PENDING','pi_3S89Nr578Xxwj3ov0hy8TLTK','card','2025-09-17 00:38:08','System','2025-09-17 00:38:07',NULL,4.00,22.50,20.00,'Express Shipping 2 - 3 Days'),(31,17,'username@sheridancollege.ca','{\"city\": \"Mississauga\", \"name\": \"LE DUY TOAN NGUYEN\", \"street\": \"828 Winterton Way\", \"country\": \"KH\", \"province\": \"ON\", \"apartment\": \"\", \"postalCode\": \"L5V 2A5\", \"phoneNumber\": \"11231231231\"}',46.50,'USD','PENDING','pi_3S89TM578Xxwj3ov07MLaVaP','card','2025-09-17 00:43:48','System','2025-09-17 00:43:48',NULL,4.00,22.50,20.00,'Express Shipping 2 - 3 Days'),(32,17,'username@sheridancollege.ca','{\"city\": \"Mississauga\", \"name\": \"LE DUY TOAN NGUYEN\", \"street\": \"828 Winterton Way\", \"country\": \"AG\", \"province\": \"ON\", \"apartment\": \"\", \"postalCode\": \"L5V 2A5\", \"phoneNumber\": \"11231231231\"}',31.50,'USD','PENDING','pi_3S89XT578Xxwj3ov17oCz9CV','card','2025-09-17 00:48:04','System','2025-09-17 00:48:03',NULL,4.00,7.50,20.00,'Regular Shipping 4 - 7 Days'),(33,18,'test3@gmail.com','{\"city\": \"Mississauga\", \"name\": \"LE DUY TOAN NGUYEN\", \"street\": \"828 Winterton Way\", \"country\": \"CZ\", \"province\": \"ON\", \"apartment\": \"\", \"postalCode\": \"L5V 2A5\", \"phoneNumber\": \"11231231231\"}',13.00,'USD','PENDING','pi_3S95ao578Xxwj3ov16r38ACj','card','2025-09-19 14:47:23','System','2025-09-19 14:47:23',NULL,0.50,7.50,0.00,'Regular Shipping 4 - 7 Days'),(38,18,'test3@gmail.com','{\"city\": \"Mississauga\", \"name\": \"LE DUY TOAN NGUYEN\", \"street\": \"828 Winterton Way\", \"userId\": null, \"country\": \"CA\", \"province\": \"ON\", \"apartment\": \"\", \"isDefault\": false, \"postalCode\": \"L5V 2A5\", \"phoneNumber\": \"11231231231\"}',28.00,'USD','PENDING','pi_3S9bNE578Xxwj3ov1PVqiRIf','card','2025-09-21 00:43:28','System','2025-09-21 00:43:28',NULL,0.50,22.50,0.00,'Express Shipping 2 - 3 Days'),(39,18,'test3@gmail.com','{\"city\": \"Mississauga\", \"name\": \"LE DUY TOAN NGUYEN\", \"street\": \"828 Winterton Way\", \"userId\": null, \"country\": \"AD\", \"province\": \"ON\", \"apartment\": \"\", \"isDefault\": false, \"postalCode\": \"L5V 2A5\", \"phoneNumber\": \"11231231231\"}',25.50,'USD','PENDING','pi_3S9p32578Xxwj3ov1mu6zxFr','card','2025-09-21 15:19:32','System','2025-09-21 15:19:33','System',0.50,22.50,2.50,'Express Shipping 2 - 3 Days'),(40,18,'test3@gmail.com','{\"city\": \"Mississauga\", \"name\": \"LE DUY TOAN NGUYEN\", \"street\": \"828 Winterton Way\", \"userId\": null, \"country\": \"AD\", \"province\": \"ON\", \"apartment\": \"\", \"isDefault\": false, \"postalCode\": \"L5V 2A5\", \"phoneNumber\": \"11231231231\"}',10.50,'USD','PAID','pi_3S9p51578Xxwj3ov1B1FE6xT','card','2025-09-21 15:21:35','System','2025-09-21 15:21:37','System',0.50,7.50,2.50,'Regular Shipping 4 - 7 Days'),(44,21,'duytoannguyenledh4@gmail.com','{\"city\": \"Mississauga\", \"name\": \"LE DUY TOAN NGUYEN\", \"street\": \"828 Winterton Way\", \"userId\": null, \"country\": \"AS\", \"province\": \"ON\", \"apartment\": \"\", \"isDefault\": false, \"postalCode\": \"L5V 2A5\", \"phoneNumber\": \"11231231231\"}',29.50,'USD','PENDING','pi_3SBHDC578Xxwj3ov10buW2r8','card','2025-09-25 15:36:02','System','2025-09-25 15:36:03','System',2.00,7.50,0.00,'Regular Shipping 4 - 7 Days'),(45,21,'duytoannguyenledh4@gmail.com','{\"city\": \"Mississauga\", \"name\": \"LE DUY TOAN NGUYEN\", \"street\": \"828 Winterton Way\", \"userId\": null, \"country\": \"BN\", \"province\": \"ON\", \"apartment\": \"\", \"isDefault\": false, \"postalCode\": \"L5V 2A5\", \"phoneNumber\": \"11231231231\"}',13.00,'USD','PENDING','pi_3SBHIY578Xxwj3ov08AMRSLU','card','2025-09-25 15:41:34','System','2025-09-25 15:41:35','System',0.50,7.50,0.00,'Regular Shipping 4 - 7 Days'),(46,21,'duytoannguyenledh4@gmail.com','{\"city\": \"Mississauga\", \"name\": \"LE DUY TOAN NGUYEN\", \"street\": \"828 Winterton Way\", \"userId\": null, \"country\": \"AL\", \"province\": \"ON\", \"apartment\": \"\", \"isDefault\": false, \"postalCode\": \"L5V 2A5\", \"phoneNumber\": \"11231231231\"}',13.00,'USD','PENDING','pi_3SBHJr578Xxwj3ov0erDe0xq','card','2025-09-25 15:42:55','System','2025-09-25 15:42:56','System',0.50,7.50,0.00,'Regular Shipping 4 - 7 Days'),(47,21,'duytoannguyenledh4@gmail.com','{\"city\": \"Mississauga\", \"name\": \"LE DUY TOAN NGUYEN\", \"street\": \"828 Winterton Way\", \"userId\": null, \"country\": \"AG\", \"province\": \"ON\", \"apartment\": \"\", \"isDefault\": false, \"postalCode\": \"L5V 2A5\", \"phoneNumber\": \"11231231231\"}',13.00,'USD','PENDING','pi_3SBKGw578Xxwj3ov04sBknfL','card','2025-09-25 18:52:06','System','2025-09-25 18:52:07','System',0.50,7.50,0.00,'Regular Shipping 4 - 7 Days'),(48,21,'duytoannguyenledh4@gmail.com','{\"city\": \"Mississauga\", \"name\": \"LE DUY TOAN NGUYEN\", \"street\": \"828 Winterton Way\", \"userId\": null, \"country\": \"AG\", \"province\": \"ON\", \"apartment\": \"\", \"isDefault\": false, \"postalCode\": \"L5V 2A5\", \"phoneNumber\": \"11231231231\"}',5.50,'USD','PENDING','pi_3SBKQC578Xxwj3ov1EPorcIt','card','2025-09-25 19:01:40','System','2025-09-25 19:01:41','System',0.50,0.00,0.00,'Free Shipping 7 - 10 Days'),(49,21,'duytoannguyenledh4@gmail.com','{\"city\": \"Mississauga\", \"name\": \"LE DUY TOAN NGUYEN\", \"street\": \"828 Winterton Way\", \"userId\": null, \"country\": \"BW\", \"province\": \"ON\", \"apartment\": \"\", \"isDefault\": false, \"postalCode\": \"L5V 2A5\", \"phoneNumber\": \"11231231231\"}',24.00,'USD','PENDING','pi_3SEjUP578Xxwj3ov15VHXvZi','card','2025-10-05 04:24:05','System','2025-10-05 04:24:07','System',1.50,7.50,0.00,'Regular Shipping 4 - 7 Days'),(50,21,'duytoannguyenledh4@gmail.com','{\"city\": \"Mississauga\", \"name\": \"LE DUY TOAN NGUYEN\", \"street\": \"828 Winterton Way\", \"userId\": null, \"country\": \"AG\", \"province\": \"ON\", \"apartment\": \"\", \"isDefault\": false, \"postalCode\": \"L5V 2A5\", \"phoneNumber\": \"11231231231\"}',5.50,'USD','PENDING','pi_3SEjje578Xxwj3ov08CEzLq6','card','2025-10-05 04:39:51','System','2025-10-05 04:39:52','System',0.50,0.00,0.00,'Free Shipping 7 - 10 Days'),(51,21,'duytoannguyenledh4@gmail.com','{\"city\": \"\", \"name\": \"\", \"street\": \"\", \"userId\": null, \"country\": \"\", \"province\": \"\", \"apartment\": \"\", \"isDefault\": false, \"postalCode\": \"\", \"phoneNumber\": \"\"}',10.50,'USD','PENDING',NULL,'card','2025-10-19 05:10:35','System','2025-10-19 05:10:35',NULL,0.50,7.50,2.50,'Free Shipping 7 - 10 Days');
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

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
INSERT INTO `products` VALUES (1,'ristretto bianco','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,6.00,0,9,'coffee_baverage','2025-08-26 19:38:46','admin','2025-09-17 00:48:04','System'),(2,'iced creamy latte','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,6.00,0,14,'coffee_baverage','2025-08-26 19:38:46','admin','2025-10-05 04:39:52','System'),(3,'cappucino','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,6.00,0,28,'coffee_baverage','2025-08-26 19:38:46','admin','2025-09-25 15:42:56','System'),(4,'iced long black','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,6.00,0,27,'coffee_baverage','2025-08-26 19:38:46','admin','2025-09-25 15:36:03','System'),(5,'milk coffee regal','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,6.00,0,25,'coffee_baverage','2025-08-26 19:38:46','admin','2025-10-19 05:10:35','System'),(6,'orange juice','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,6.00,0,28,'coffee_baverage','2025-08-26 19:38:46','admin','2025-09-21 15:19:33','System'),(7,'soda beverage','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,6.00,0,28,'coffee_baverage','2025-08-26 19:38:46','admin','2025-09-25 15:41:35','System'),(8,'iced coffee with milk','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,6.00,0,25,'coffee_baverage','2025-08-26 19:38:46','admin','2025-09-17 00:48:04','System'),(9,'iced americano','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,5.00,0,30,'coffee_baverage','2025-08-26 19:38:46','admin',NULL,NULL),(10,'vegan iced latte','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,5.00,0,30,'coffee_baverage','2025-08-26 19:38:46','admin',NULL,NULL),(11,'iced chocolate','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,5.00,0,30,'coffee_baverage','2025-08-26 19:38:46','admin',NULL,NULL),(12,'autumnal coffee','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,5.00,0,30,'coffee_baverage','2025-08-26 19:38:46','admin',NULL,NULL),(13,'seafood lunch','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,5.00,0,30,'food_snack','2025-08-26 19:38:46','admin',NULL,NULL),(14,'french toast with sugar','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,5.00,0,29,'food_snack','2025-08-26 19:38:46','admin','2025-09-10 01:00:25',NULL),(15,'chocolate croissant','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,6.00,0,15,'food_snack','2025-08-26 19:38:46','admin','2025-09-10 18:56:45',NULL),(16,'potato wedges','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,6.00,0,30,'food_snack','2025-08-26 19:38:46','admin',NULL,NULL),(17,'brownies','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,5.00,0,29,'food_snack','2025-08-26 19:38:46','admin','2025-09-10 00:37:46',NULL),(18,'banana cake','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,5.00,0,27,'food_snack','2025-08-26 19:38:46','admin','2025-09-10 00:33:58',NULL),(19,'sandwiches and pickles','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,5.00,0,30,'food_snack','2025-08-26 19:38:46','admin',NULL,NULL),(20,'spaghetti bolognese','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,5.00,0,29,'food_snack','2025-08-26 19:38:46','admin','2025-09-10 01:06:40',NULL),(21,'sandwich vegan','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,5.00,0,30,'food_snack','2025-08-26 19:38:46','admin',NULL,NULL),(22,'eggs benedict burger','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,5.00,0,30,'food_snack','2025-08-26 19:38:46','admin',NULL,NULL),(23,'corn cheese sandwich','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,6.00,0,30,'food_snack','2025-08-26 19:38:46','admin',NULL,NULL),(24,'buttermilk waffle','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,5.00,0,30,'food_snack','2025-08-26 19:38:46','admin',NULL,NULL),(25,'at home house blend','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,5.00,0,30,'at_home','2025-08-26 19:38:46','admin',NULL,NULL),(26,'at home arabica','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,5.00,0,30,'at_home','2025-08-26 19:38:46','admin',NULL,NULL),(27,'at home classic','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,5.00,0,30,'at_home','2025-08-26 19:38:46','admin',NULL,NULL),(28,'white mug','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,5.00,0,30,'at_home','2025-08-26 19:38:46','admin',NULL,NULL),(29,'at home kalosi','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,5.00,0,28,'at_home','2025-08-26 19:38:46','admin','2025-09-09 23:48:30',NULL),(30,'at home luwak','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,5.00,0,30,'at_home','2025-08-26 19:38:46','admin',NULL,NULL),(31,'at home robusta','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,5.00,0,28,'at_home','2025-08-26 19:38:46','admin','2025-09-10 00:37:46',NULL),(32,'coffee temper 58 MM','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',5.00,5.00,0,30,'at_home','2025-08-26 19:38:46','admin',NULL,NULL),(33,'french press 9 cups','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',25.00,25.00,0,10,'at_home','2025-08-26 19:38:46','admin',NULL,NULL),(34,'glass tea pot teiera (6 cups)','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',13.00,13.00,0,10,'at_home','2025-08-26 19:38:46','admin',NULL,NULL),(35,'french press 3 cup','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',20.00,20.00,0,30,'at_home','2025-08-26 19:38:46','admin',NULL,NULL),(36,'moka pot','An all-time favorite blend with citrus fruit character, caramel flavors, and a pleasant faintly floral aroma.\nLocked scent:\nExcelso prevents air from entering the packaging with a locked aroma, ensuring the coffee\'s freshness.\n\nStorage Way:\nTo maintain the taste of coffee and the freshness of the aroma, store Excelso coffee in an airtight. ',20.00,20.00,0,30,'at_home','2025-08-26 19:38:46','admin',NULL,NULL);
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
INSERT INTO `promos` VALUES (1,'CASHBACK25','Cashback $2.5,00','Ends in 5 minutes!','fixed',2.50,'2025-08-31 02:57:19','2026-09-01 03:27:11',1,'2025-08-31 02:57:19','system',NULL,NULL),(2,'DISKON50','Diskon 50% With Credit or Debit Card','Ends in 20 minutes!','percentage',50.00,'2025-08-31 02:57:19','2026-09-01 03:27:11',1,'2025-08-31 02:57:19','system',NULL,NULL),(3,'FREESHIP2','Free shipping $2,00','Ends in 10 minutes!','free_shipping',2.00,'2025-08-31 02:57:19','2026-09-01 03:27:11',1,'2025-08-31 02:57:19','system',NULL,NULL),(4,'SUMMER10','Summer Sale 10% OFF','Ends in 1 hour!','percentage',10.00,'2025-08-31 02:57:19','2025-09-01 03:27:11',1,'2025-08-31 02:57:19','system',NULL,NULL),(5,'HOLIDAY15','Holiday Special $15 OFF','Ends in 2 hours!','fixed',15.00,'2025-08-31 02:57:19','2025-09-01 03:27:11',1,'2025-08-31 02:57:19','system',NULL,NULL),(6,'NEWUSER5','New User $5 OFF','Ends in 30 minutes!','fixed',5.00,'2025-08-31 02:57:19','2025-09-01 03:27:11',1,'2025-08-31 02:57:19','system',NULL,NULL);
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
INSERT INTO `ship` VALUES (1,'Free Shipping','F','7 - 10 Days',0.00,1,'2025-09-03 03:01:38','system',NULL,NULL),(2,'Regular Shipping','R','4 - 7 Days',7.50,1,'2025-09-03 03:01:38','system',NULL,NULL),(3,'Express Shipping','E','2 - 3 Days',22.50,1,'2025-09-03 03:01:38','system',NULL,NULL),(4,'Instant Delivery','I','2 - 3 Days',25.50,1,'2025-09-03 03:01:38','system',NULL,NULL);
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
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (4,'user@email.com','$2a$10$KihYY2JJmOCr79lN/c.6j.7q0TeluAsXTeBmwOk/SBgnnO/NMMl3G','2025-09-12 23:13:39','Admin',NULL,NULL,'user1',0),(16,'test@gmail.com','$2a$10$C8GA6RW8w/Sah5zrt2P5j.opp0kHAHZ3oNqAfS8BQeQwBo.uHkVRG','2025-09-14 20:08:21','System',NULL,NULL,'testuser1',0),(17,'test2@gmail.com','$2a$10$yqBxkTMikQHFspm3Tt9Bru/m8ZhVsSXI/JDGDISIBZwY6Q46EIj0y','2025-09-16 03:30:10','System',NULL,NULL,'testuser2',0),(18,'test3@gmail.com','$2a$10$DoaBI/KOHQc1NBYNmYnNu.XA.FAb/7jqXeF8L6Qx5Z5yri3VnAOXS','2025-09-17 01:07:25','System',NULL,NULL,'testuser3',0),(19,'duytoanca@gmail.com','$2a$10$rECjiYHS5Obe1bLWWdiaNutIHUnBnEYqO0fGPbcQhY2d06InDG1ie','2025-09-22 18:50:15','System',NULL,NULL,'LE DUY TOAN NGUYEN',0),(21,'duytoannguyenledh4@gmail.com','b80230fb-8268-4f11-9208-cf45e84fd00c','2025-09-24 18:03:05','System',NULL,NULL,'Duy Toàn Nguyễn Lê',0),(22,'duytoannguyenledh@gmail.com','baa20510-40a7-471c-8849-5e5643427fd6','2026-01-12 18:44:09','System',NULL,NULL,'Toan',0);
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

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
-- Dumping data for table `user_role`
--

LOCK TABLES `user_role` WRITE;
/*!40000 ALTER TABLE `user_role` DISABLE KEYS */;
INSERT INTO `user_role` VALUES (4,1,'2025-09-12 23:13:39','System',NULL,NULL),(16,1,'2025-09-14 20:08:21','System',NULL,NULL),(17,1,'2025-09-16 03:30:10','System',NULL,NULL),(18,1,'2025-09-17 01:07:25','System',NULL,NULL),(19,1,'2025-09-22 18:50:15','System',NULL,NULL),(21,1,'2025-09-24 18:03:05','System',NULL,NULL),(22,1,'2026-01-12 18:44:09','System',NULL,NULL);
/*!40000 ALTER TABLE `user_role` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-28 20:56:30
