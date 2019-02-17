CREATE TABLE message_sync_status(
  id                    BIGSERIAL PRIMARY KEY,
  friend_id             BIGINT,
  message_sync_strategy VARCHAR(20),
  FOREIGN KEY (friend_id) REFERENCES friends(id)
)