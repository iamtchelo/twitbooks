CREATE TABLE friends(
  id                    BIGSERIAL PRIMARY KEY,
  twitter_id            BIGINT UNIQUE NOT NULL,
  name                  VARCHAR(255) NOT NULL,
  screen_name           VARCHAR(255) NOT NULL,
  message_sync_strategy VARCHAR(20)
)