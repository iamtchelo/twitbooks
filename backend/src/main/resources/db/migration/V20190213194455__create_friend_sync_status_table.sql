CREATE TABLE friend_sync_status(
  id        BIGSERIAL PRIMARY KEY,
  status    VARCHAR(20) NOT NULL,
  cursor_id BIGINT
)