CREATE TABLE users(
    id                  BIGSERIAL PRIMARY KEY,
    twitter_id          TEXT NOT NULL,
    access_token        TEXT,
    access_token_secret TEXT
);

create table friends(
    id                    BIGINT NOT NULL,
    name                  VARCHAR(255) NOT NULL,
    screen_name           VARCHAR(255) NOT NULL,
    profile_image_url     TEXT NOT NULL,
    message_sync_strategy VARCHAR(20)
);

create table friend_sync_status(
    id      BIGSERIAL PRIMARY KEY,
    status  VARCHAR(20) NOT NULL,
    cursor  BIGINT,
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

create table user_friends(
    user_id   BIGINT NOT NULL,
    friend_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (friend_id) REFERENCES friends(id)
);

create table messages(
);

create table message_sync_state(

);

create table books(

);

create table book_matches(

);

create table book_user(
)
