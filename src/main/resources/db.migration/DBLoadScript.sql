CREATE TABLE `product` (
                           `product_id` varchar(255) NOT NULL,
                           `name` varchar(255) DEFAULT NULL,
                           `stock` int(11) DEFAULT NULL,
                           `price` double DEFAULT NULL,
                           `currency` varchar(255) DEFAULT NULL,
                           `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                           `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                           CHECK(stock >= 0 and price >=0 ),
                           PRIMARY KEY (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


INSERT INTO `product` (`product_id`,`name`,`stock`,`price`,`currency`, `created_at`, `updated_at`)  VALUES ("55a56931-6039-4f9a-8958-2ce0dce8a726","Bike","25","500", "VND") , ("49d2f5ac-f2bd-4d9f-b8cb-72fe8743a368","MotorBike","50","1500", "VND") , ("35fae9e6-4ecc-4c38-b2de-9d91e77d5147","Car","250","5500", "VND")  , ("b96eac9c-b17e-4e55-8550-af734a66253e","Car","285","8500", "VND")  , ("13ada175-9aef-411c-b48e-c5ee711c84f3","Bike","15","250", "VND")  , ("5e0bbc2c-9e68-4165-a4a5-c37d26a1cb22","Shirt","2005","50", "VND")  , ("06064bbc-3f5a-4d10-a5e7-dcebb3bc29ca","Laptop","650","800", "VND")  , ("2ac528f3-f8fa-4792-8a4c-44d702b36ba1","Desk","780","300", "VND")  , ("d0c3a9eb-13e0-4926-b6a6-e3939a324a3e","Sneaker Shoe","2500","320", "VND")  , ("ccc13c24-5953-43ff-8eaa-f520972f7e56","Towel","1000","30", "VND") ;

CREATE TABLE `customer` (
                            `customer_id` varchar(255) NOT NULL,
                            `name` varchar(255) DEFAULT NULL,
                            `email` varchar(255) DEFAULT NULL,
                            `phone_number` varchar(255) DEFAULT NULL,
                            `balance` Decimal DEFAULT NULL,
                            `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            PRIMARY KEY (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `customer` (`customer_id`,`name`,`email`,`phone_number`,`balance`,`created_at`, `updated_at`)   VALUES ("2aca2985-4e8b-49a3-96d1-4ed26d6afda5","Carl","carl@gmail.com","8497853214568","25500", "2024-12-30 10:10:10", "2024-12-30 10:10:10")  , ("b262c269-4c10-4b54-8eb3-9523c2ce0e34","Keith","keith@gmail.com","4956871236489","12500", "2024-12-30 10:10:10", "2024-12-30 10:10:10")  , ("4e99c184-7de7-46e4-b675-1560ee36793a","John","john@gmail.com","0143658792365","222200", "2024-12-30 10:10:10", "2024-12-30 10:10:10")   , ("2691c02a-451d-4d30-94a1-26333c49dd35","Quinn","quinn@gmail.com","0156489723245","355600", "2024-12-30 10:10:10", "2024-12-30 10:10:10")   , ("2a804c10-0a8b-4ba2-9de6-5f53d47f676c","Jenny","jenny@gmail.com","86789532478","155600", "2024-12-30 10:10:10", "2024-12-30 10:10:10")   , ("10f12044-da8b-40b3-b4aa-6075d0590742","Sonny","sonny12@gmail.com","256981255673","266600", "2024-12-30 10:10:10", "2024-12-30 10:10:10")   , ("fad0447a-da42-460a-85c6-a0bce7eeb115","Long", "long14@gmail.com", "842365981365","560500", "2024-12-30 10:10:10", "2024-12-30 10:10:10")   , ("e00f1841-8cd9-4a85-a4ea-beac160da6df","Tim","timmothy@gmail.com","023659856132","325600", "2024-12-30 10:10:10", "2024-12-30 10:10:10")   , ("005eda12-4239-40a7-90b8-8946a0049b08","Glashory","glashory@gmail.com","896236515862","105500", "2024-12-30 10:10:10", "2024-12-30 10:10:10")   , ("9fb35cd9-94b0-4a34-8257-4becbc3bb271","Zack","zack@gmail.com","1658465235786","68500", "2024-12-30 10:10:10", "2024-12-30 10:10:10"), ("3597a1e5-c732-4941-8cc8-dede47147646","Nret","nret@gmail.com","849864359788","305000", "2024-12-30 10:10:10", "2024-12-30 10:10:10");
