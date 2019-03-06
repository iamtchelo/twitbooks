CREATE TABLE books(
  id BIGINT PRIMARY KEY
);

CREATE TABLE book_matches(
  book_id BIGINT,
  message_id BIGINT,
  FOREIGN KEY (book_id) REFERENCES books(id),
  FOREIGN KEY (message_id) REFERENCES messages(id)
)
