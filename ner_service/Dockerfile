FROM python:3.7.3-slim

EXPOSE 5000

WORKDIR /app

RUN apt-get update && apt-get -y install build-essential

COPY . .

RUN pip install -r requirements.txt

ENV PYTHONUNBUFFERED=TRUE

RUN python -m spacy download en_core_web_md

CMD ["uwsgi", "--http", "0.0.0.0:5000", "--module", "app:app"]
