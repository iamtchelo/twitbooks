CREATE TABLE users(
    id                  BIGSERIAL PRIMARY KEY,
    twitter_id          TEXT NOT NULL,
    access_token        TEXT,
    access_token_secret TEXT
);

create table friends(
    id                    BIGINT NOT NULL PRIMARY KEY ,
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
    id         BIGINT PRIMARY KEY,
    text       VARCHAR(300) NOT NULL,
    retweet    BOOLEAN NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    friend_id  BIGINT,
    FOREIGN KEY (friend_id) REFERENCES friends(id)
);

create table message_sync_state(
    id        BIGSERIAL NOT NULL PRIMARY KEY ,
    friend_id BIGINT NOT NULL,
    min_id    BIGINT,
    max_id    BIGINT,
    FOREIGN KEY (friend_id) REFERENCES friends(id)
);

create table books(
    id              BIGSERIAL NOT NULL PRIMARY KEY ,
    title           VARCHAR(255),
    key             TEXT,
    small_image_url TEXT,
    image_url       TEXT,
    ignored         BOOLEAN NOT NULL DEFAULT '0',
    created_date    TIMESTAMP WITH TIME ZONE
);

create table book_matches(
    book_id    BIGINT NOT NULL,
    message_id BIGINT NOT NULL,
    FOREIGN KEY (book_id) REFERENCES books(id),
    FOREIGN KEY (message_id) REFERENCES messages(id)
);

create table book_user(
    book_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (book_id) REFERENCES books(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE providers(
                          id BIGINT PRIMARY KEY,
                          key TEXT
);

INSERT INTO providers VALUES(1, 'GOODREADS');
INSERT INTO providers VALUES(2, 'AMAZON');
INSERT INTO providers VALUES(3, 'GOOGLE_BOOKS');

CREATE TABLE book_providers(
                               book_id BIGINT,
                               provider_id BIGINT,
                               FOREIGN KEY (book_id) REFERENCES books(id),
                               FOREIGN KEY (provider_id) REFERENCES providers(id)
)
