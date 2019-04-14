FROM python:3.7.3-slim

EXPOSE 5000

WORKDIR /app

COPY . .

RUN pip install -r requirements.txt

RUN python -m spacy download en_core_web_md

CMD ["waitress-serve", "--port=5000", "--call", "app:create_app"]
