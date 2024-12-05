CREATE TABLE `products` (
                             `product_id` varchar(255) NOT NULL,
                             `name` varchar(255) DEFAULT NULL,
                             `quantity` int(11) DEFAULT NULL,
                             `price` double DEFAULT NULL,
                             `currency` varchar(255) DEFAULT NULL,
                             CHECK(stock >= 0 and price >=0 ),
                             PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


INSERT INTO `products` (`product_id`,`name`,`quantity`,`price`,`currency`)  VALUES ("UUID-1","Bike","25","500", "VND")  VALUES ("UUID-2","MotorBike","50","1500", "VND")  VALUES ("UUID-3","Car","250","5500", "VND")  VALUES ("UUID-4","Car","285","8500", "VND")  VALUES ("UUID-5","Bike","15","250", "VND")  VALUES ("UUID-6","Shirt","2005","50", "VND")  VALUES ("UUID-7","Laptop","650","800", "VND")  VALUES ("UUID-8","Desk","780","300", "VND")  VALUES ("UUID-9","Sneaker Shoe","2500","320", "VND")  VALUES ("UUID-10","Towel","1000","30", "VND") ;
