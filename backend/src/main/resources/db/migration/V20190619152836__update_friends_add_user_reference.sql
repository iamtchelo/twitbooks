ALTER TABLE friend_sync_status ADD COLUMN user_id TEXT REFERENCES users(id);

ALTER TABLE friends ADD COLUMN user_id TEXT REFERENCES users(id)
