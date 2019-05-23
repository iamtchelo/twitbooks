FROM python:3.7.3-slim

EXPOSE 5000

WORKDIR /app

COPY . .

RUN pip install -r requirements.txt

ENV PYTHONUNBUFFERED=TRUE

RUN python -m spacy download en_core_web_md

CMD ["gunicorn", "-w", "1", "-b", "0.0.0.0:5000", "app:app"]
