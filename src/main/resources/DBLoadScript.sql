CREATE TABLE `account` (
                           `id` varchar(255) NOT NULL,
                           `balance` double DEFAULT NULL,
                           `currency` varchar(255) DEFAULT NULL,
                           `status` varchar(255) DEFAULT NULL,
                           `operation` varchar(255) DEFAULT NULL,
                           PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `operation` (
                             `id` varchar(255) NOT NULL,
                             `date` varchar(255) DEFAULT NULL,
                             `amount` int(11) DEFAULT NULL,
                             `type` varchar(255) DEFAULT NULL,
                             `account` varchar(255) DEFAULT NULL,
                             PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;




INSERT INTO `account` (`id`,`balance`,`currency`,`status`)  VALUES ("UUID-1","125","2021-05-16","CREATED") ,("UUID-2","56","2021-09-16","CREATED") ,("UUID-3","56","2021-02-05","CREATED") ,("UUID-4","5896","2022-03-28","ACTIVATED") ,("UUID-5","899","2021-01-12","ACTIVATED") ,("UUID-6","478","2022-03-17","ACTIVATED") ,("UUID-7","2000","2021-04-16","ACTIVATED") ,("UUID-8","58","2021-07-20","ACTIVATED") ,("UUID-9","99","2021-01-02","ACTIVATED") ,("UUID-10","8686","2022-04-03","SUSPENDED") ;


INSERT INTO `operation` (`id`,`amount`,`date`,`type`,`account`)  VALUES ("UUID-1","125","2021-05-16","Amity","Stokes") ,("UUID-2","56","2021-09-16","Blossom","Valentine") ,("UUID-3","56","2021-02-05","Nina","Gill") ,("UUID-4","5896","2022-03-28","Martha","Rivers") ,("UUID-5","899","2021-01-12","Judith","Best") ,("UUID-6","478","2022-03-17","Sara","Carson") ,("UUID-7","2000","2021-04-16","Zahir","Levine") ,("UUID-8","58","2021-07-20","Cathleen","Bernard") ,("UUID-9","99","2021-01-02","Jamalia","Crane") ,("UUID-10","8686","2022-04-03","Mia","Ewing");
