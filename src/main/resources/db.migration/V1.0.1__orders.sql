-- CREATE SEQUENCE order_seq;

CREATE TABLE `order` (
                         id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                         amount DECIMAL(10, 2),
                         customer_email VARCHAR(255)
);
