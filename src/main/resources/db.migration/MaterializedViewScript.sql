// CustomerOrderView TABLE
CREATE TABLE CustomerOrderView (
                                   id VARCHAR(255) PRIMARY KEY AUTO_INCREMENT,
                                   customer_id VARCHAR(255) NOT NULL,
                                   customer_name VARCHAR(255),
                                   order_id VARCHAR(255) NOT NULL,
                                   order_date DEFAULT CURRENT_TIMESTAMP,
                                   order_status VARCHAR(50),
                                   total_amount DECIMAL(10, 2),
                                   UNIQUE KEY (order_id)
);

// ProductSalesView TABLE
CREATE TABLE ProductSalesView (
                                  id VARCHAR(255) PRIMARY KEY AUTO_INCREMENT,
                                  product_id VARCHAR(255) NOT NULL,
                                  product_name VARCHAR(255),
                                  total_quantity_sold INT,
                                  total_revenue DECIMAL(10, 2),
                                  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                                  UNIQUE KEY (product_id)
);

// PaymentSummaryView TABLE
CREATE TABLE PaymentSummaryView (
                                    id VARCHAR(255) PRIMARY KEY AUTO_INCREMENT,
                                    order_id VARCHAR(255) NOT NULL,
                                    payment_id VARCHAR(255) NOT NULL,
                                    payment_status VARCHAR(50),
                                    payment_date TIMESTAMP,
                                    payment_amount DECIMAL(10, 2),
                                    UNIQUE KEY (payment_id)
);

// CustomerProductRate TABLE
CREATE TABLE CustomerProductRate (
                                     id VARCHAR(255) PRIMARY KEY AUTO_INCREMENT,
                                     customer_id VARCHAR(255) NOT NULL,
                                     product_id VARCHAR(255) NOT NULL,
                                     rating DECIMAL(3, 2),
                                     rated_at TIMESTAMP,
                                     UNIQUE KEY (customer_id, product_id)
);

