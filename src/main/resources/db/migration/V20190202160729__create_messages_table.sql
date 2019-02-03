CREATE TABLE messages(
  id          BIGSERIAL PRIMARY KEY,
  text        VARCHAR(280) NOT NULL,
  twitter_id  BIGINT UNIQUE NOT NULL,
  retweet     BOOLEAN NOT NULL,
  created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
  friend_id   BIGINT NOT NULL,
  FOREIGN KEY (friend_id) REFERENCES friends(id)
)