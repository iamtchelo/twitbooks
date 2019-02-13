CREATE TABLE friend_sync_status(
  id        BIGSERIAL PRIMARY KEY,
  status    VARCHAR(20) NOT NULL,
  sync_when TIMESTAMP WITH TIME ZONE,
  cursor_id BIGINT
)