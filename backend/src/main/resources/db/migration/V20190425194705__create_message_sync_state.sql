CREATE TABLE message_sync_state(
    id        BIGSERIAL PRIMARY KEY,
    friend_id BIGINT,
    min_id    BIGINT,
    max_id    BIGINT,
    FOREIGN KEY (friend_id) REFERENCES friends(id)
)