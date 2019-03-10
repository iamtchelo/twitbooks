CREATE TABLE friends(
  id                    BIGINT PRIMARY KEY,
  name                  VARCHAR(255) NOT NULL,
  screen_name           VARCHAR(255) NOT NULL,
  message_sync_strategy VARCHAR(20)
)