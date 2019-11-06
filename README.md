# TwitBooks

## About

A lot of my book recommendations come from my Twitter feed. So Twitbooks was created for extracing book recommendations from Twitter feeds.
It uses [Named Entity Recognition](https://en.wikipedia.org/wiki/Named-entity_recognition)
to extract book information from your feed and uses books APIs to grab book information to display.

## Architecture

The `backend` consists of a Spring Boot application with a job that synchronizes Twitter data,
sends that data to a NER engine to extract book names and uses a book API to retrieve book information.

You can choose between two NER engines:

* A self-hosted Flask application that uses [spaCy](https://spacy.io/)
* A PaaS solution from AWS aka [AWS Comprehend](https://aws.amazon.com/comprehend/)

And two books APIs :

* [Google Books](https://books.google.com/)
* [Goodreads](https://www.goodreads.com/)

The `frontend` consists of a React SPA that uses mobx-state-tree to handle state. It shows
the books that have been extracted from your feed and displays the synchronization progress.

### Quickstart

The quickest way to get up and running is to use the default profiles `dev,spacy,google`.
Go to the [Twitter Developer page](https://developer.twitter.com/en/apps) and create a new
application. After creating the app, enter it and on the tab `Keys and tokens` you should
see four keys (2 consumer keys and 2 api keys).

Either put them directly on your `application.properties` or add the following variables
to your PATH: `TWITTER_CONSUMER_KEY`, `TWITTER_CONSUMER_SECRET`, `TWITTER_ACCESS_TOKEN` and `TWITTER_ACCESS_TOKEN_SECRET`

after that run `docker-compose up -d` to start the Postgres, Frontend app and the NER service and run the backend app.

### Goodreads Configuration

To use Goodreads you need to request a developer API [Here](https://www.goodreads.com/api/keys). Then put your
key on the file `application-goodreads.properties` (or use the `GOODREADS_KEY` env variable). Then you need to
set the `backend` app to use the Goodreads API by adding `goodreads` to `spring.active.profiles`. Note that the
`google` and `goodreads` profiles are mutually exclusive.

NOTE: Goodreads imposes a limitation of 1 request per second which makes processing books quite slow, so keep
that in mind if you're going to use it.

### Google Books Setup

The API used by Google Books doesn't need authentication. So just setting `google` on the active profiles and
it will work. Note that the `google` and `goodreads` profiles are mutually exclusive.

NOTE: I haven't found anywhere saying about rate limits for this API. During my tests it does have some
throttling. The application itself doesn't handle rate limiting for this API, like it does for Goodreads,
so be careful when using it!

### SpaCy Setup

Spin up the NER container using `docker-compose up -d` and configure the services address on
`application-spacy.properties` or use the `NER_CLIENT_URL` env variable. If you're on linux the
default value: `http://localhost:5000/` should work out of the box.

### AWS Comprehend Setup

From their website: `Amazon Comprehend is a machine learning powered service that makes it easy to find insights and relationships in text.`
As per usual, you need to provide access keys for consuming AWS services. Once you have your keys either edit the file `application-comprehend.properties`
or have the `AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY` and `AWS_REGION` environment variables. Then you need to tell the `backend` app to use the
Comprehend NER engine by adding `comprehend` to the `spring.active.profiles`.
