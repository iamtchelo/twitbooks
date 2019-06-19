ALTER TABLE friend_sync_status ADD COLUMN user_id BIGINT REFERENCES users(id);

ALTER TABLE friends ADD COLUMN user_id bigint REFERENCES users(id)
