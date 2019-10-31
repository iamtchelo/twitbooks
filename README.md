# TwitBooks

## About

Twitbooks is an app that extracts book suggestions from your Twitter feed.
It uses [Named Entity Recognition](https://spacy.io/usage/linguistic-features#named-entities)
to extract book names from your feed and uses either [Goodreads](`https://www.goodreads.com/)
or [Google Books](https://books.google.com/) to fetch book data.

## Architecture

The structure is simple. The `backend` is a Spring Boot project that interfaces with the Twitter API
and saves Twitter data in a Postgres database. A job periodically hits the `ner` service which is a
Flask application that uses `spaCy` which contains a pre trained model for identifying works of art
through named entity recognition. The `ner` service exposes a single API which receives the message
and returns any book names that could be extracted from it.

The `frontend` directory contains a React client app for visualizing the books that were found.

## Running the whole thing
