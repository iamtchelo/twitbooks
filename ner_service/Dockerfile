FROM python:3.7.3-slim

EXPOSE 5000

WORKDIR /app

RUN apt-get update && apt-get -y install build-essential

COPY requirements.txt . 

RUN pip install -r requirements.txt

RUN python -m spacy download en_core_web_md

COPY . .

ENV PYTHONUNBUFFERED=TRUE

CMD ["uwsgi", "--http-socket", "0.0.0.0:5000", "--die-on-term", "--vacuum", "--master", "--disable-logging","--module", "app:app"]
