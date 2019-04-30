# TwitBooks

## What it is ?

Twitbooks is an app that extracts book suggestions from your Twitter friends.
It consists of a Spring Boot project that synchronizes Twitter data and provides
an API endpoint for clients. It integrates with another service that uses spaCy's
[Named Entity Recognition](https://spacy.io/usage/linguistic-features#named-entities)
features to identify works of arts and that information is fed to [Goodreads](`https://www.goodreads.com/)
API to fetch book data.

## Running

You need to provide both Twitter and Goodreads API keys to the `backend` project. They can be set
through the respective environment variables:
```
```
