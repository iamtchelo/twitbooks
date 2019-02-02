CREATE TABLE messages(
  id        BIGSERIAL PRIMARY KEY,
  message   VARCHAR(280),
  friend_id BIGINT,
  FOREIGN KEY (friend_id) REFERENCES friends(id)
)