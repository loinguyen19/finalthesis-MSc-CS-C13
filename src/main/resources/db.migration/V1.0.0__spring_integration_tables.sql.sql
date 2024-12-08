-- Taken from https://raw.githubusercontent.com/spring-projects/spring-integration/main/spring-integration-jdbc/src/main/resources/org/springframework/integration/jdbc/schema-postgresql.sql

-- Table for _spring_integration_MESSAGE
CREATE TABLE _spring_integration_MESSAGE
(
    MESSAGE_ID    CHAR(36)     NOT NULL,
    REGION        VARCHAR(100) NOT NULL,
    CREATED_DATE  TIMESTAMP    NOT NULL,
    MESSAGE_BYTES BLOB,  -- MySQL uses BLOB instead of BYTEA for binary data
    PRIMARY KEY (MESSAGE_ID, REGION)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Index for _spring_integration_MESSAGE
CREATE INDEX _spring_integration_MESSAGE_IX1 ON _spring_integration_MESSAGE (CREATED_DATE);

-- Table for _spring_integration_GROUP_TO_MESSAGE
CREATE TABLE _spring_integration_GROUP_TO_MESSAGE
(
    GROUP_KEY  CHAR(36) NOT NULL,
    MESSAGE_ID CHAR(36) NOT NULL,
    REGION     VARCHAR(100),
    PRIMARY KEY (GROUP_KEY, MESSAGE_ID, REGION)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table for _spring_integration_MESSAGE_GROUP
CREATE TABLE _spring_integration_MESSAGE_GROUP
(
    GROUP_KEY              CHAR(36)     NOT NULL,
    REGION                 VARCHAR(100) NOT NULL,
    GROUP_CONDITION        VARCHAR(255),
    COMPLETE               BIGINT,
    LAST_RELEASED_SEQUENCE BIGINT,
    CREATED_DATE           TIMESTAMP    NOT NULL,
    UPDATED_DATE           TIMESTAMP DEFAULT NULL,
    PRIMARY KEY (GROUP_KEY, REGION)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Table for _spring_integration_LOCK
CREATE TABLE _spring_integration_LOCK
(
    LOCK_KEY     CHAR(36)     NOT NULL,
    REGION       VARCHAR(100) NOT NULL,
    CLIENT_ID    CHAR(36),
    CREATED_DATE TIMESTAMP    NOT NULL,
    PRIMARY KEY (LOCK_KEY, REGION)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Sequence for _spring_integration_MESSAGE_SEQ (MySQL does not use sequences in the same way as PostgreSQL)
-- MySQL uses AUTO_INCREMENT instead, so we can alter the column definition in the next table

-- Table for _spring_integration_CHANNEL_MESSAGE
CREATE TABLE _spring_integration_CHANNEL_MESSAGE
(
    MESSAGE_ID       CHAR(36)     NOT NULL,
    GROUP_KEY        CHAR(36)     NOT NULL,
    CREATED_DATE     BIGINT       NOT NULL,
    MESSAGE_PRIORITY BIGINT,
    MESSAGE_SEQUENCE BIGINT       NOT NULL AUTO_INCREMENT,  -- AUTO_INCREMENT to replace NEXTVAL
    MESSAGE_BYTES    BLOB,
    REGION           VARCHAR(100) NOT NULL,
    PRIMARY KEY (REGION, GROUP_KEY, CREATED_DATE, MESSAGE_SEQUENCE)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Index for _spring_integration_CHANNEL_MESSAGE
CREATE INDEX _spring_integration_CHANNEL_MSG_DELETE_IDX ON _spring_integration_CHANNEL_MESSAGE (REGION, GROUP_KEY, MESSAGE_ID);

-- Table for _spring_integration_METADATA_STORE
CREATE TABLE _spring_integration_METADATA_STORE
(
    METADATA_KEY   VARCHAR(255) NOT NULL,
    METADATA_VALUE VARCHAR(4000),
    REGION         VARCHAR(100) NOT NULL,
    PRIMARY KEY (METADATA_KEY, REGION)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Manually create the Flyway schema history table.
CREATE TABLE flyway_schema_history (
                                       installed_rank INT NOT NULL,
                                       version VARCHAR(50),
                                       description VARCHAR(200) NOT NULL,
                                       type VARCHAR(20) NOT NULL,
                                       script VARCHAR(1000) NOT NULL,
                                       checksum INTEGER,
                                       installed_by VARCHAR(100) NOT NULL,
                                       installed_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       execution_time INTEGER NOT NULL,
                                       success BOOLEAN NOT NULL,
                                       PRIMARY KEY (installed_rank)
);


-- Insert a baseline entry: into Flyway schema history table
INSERT INTO flyway_schema_history (
    installed_rank, version, description, type, script, checksum, installed_by, execution_time, success
) VALUES
(
     1, '1', '<< Flyway Baseline >>', 'BASELINE', '<< Flyway Baseline >>', NULL, 'your_user', 0, TRUE
);





-- This is only needed if using MySQLChannelMessageSubscriber

-- Creating a table to store the notifications, which could be monitored by an external system
CREATE TABLE _spring_integration_CHANNEL_MESSAGE_NOTIFICATIONS (
                                                                   id INT AUTO_INCREMENT PRIMARY KEY,
                                                                   region VARCHAR(100),
                                                                   group_key CHAR(36),
                                                                   created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Creating the trigger to insert into the notifications table after an insert on _spring_integration_CHANNEL_MESSAGE
DELIMITER $$

CREATE TRIGGER _spring_integration_CHANNEL_MESSAGE_NOTIFY_TRG
    AFTER INSERT ON _spring_integration_CHANNEL_MESSAGE
    FOR EACH ROW
BEGIN
    INSERT INTO _spring_integration_CHANNEL_MESSAGE_NOTIFICATIONS (region, group_key)
    VALUES (NEW.REGION, NEW.GROUP_KEY);
END $$

DELIMITER ;
