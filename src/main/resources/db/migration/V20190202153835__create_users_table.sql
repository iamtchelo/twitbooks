CREATE TABLE friends(
  id                BIGSERIAL PRIMARY KEY,
  twitter_id        BIGINT UNIQUE,
  name              VARCHAR(255),
  screen_name       VARCHAR(255)
)