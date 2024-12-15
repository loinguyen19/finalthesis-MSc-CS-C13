-- Script to create the Outbox Message table

CREATE TABLE outbox (
                        id VARCHAR(255) AUTO_INCREMENT PRIMARY KEY, -- Change datatype from BIGINT to VARCHAR(255)
                        aggregate_id VARCHAR(255) NOT NULL,     -- Aggregate ID (e.g., Order ID)
                        event_type VARCHAR(255) NOT NULL,      -- Event type (e.g., OrderCreatedEvent)
                        payload TEXT NOT NULL,                 -- Serialized event payload
                        status VARCHAR(50) DEFAULT 'PENDING',  -- Message status (e.g., PENDING, PROCESSED, FAILED)
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
