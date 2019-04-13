FROM python:3.7.3-slim

EXPOSE 5000

WORKDIR /app

RUN pip install -r requirements.txt

COPY . .

RUN python -m spacy download en_core_web_md

CMD ["waitress-serve", "--port=5000", "--call", "app:create_app"]
