CREATE TABLE messages(
  id          BIGSERIAL PRIMARY KEY,
  text        VARCHAR(280),
  twitter_id  BIGINT UNIQUE,
  retweet     BOOLEAN,
  created_at  TIMESTAMP WITH TIME ZONE,
  friend_id   BIGINT,
  FOREIGN KEY (friend_id) REFERENCES friends(id)
)