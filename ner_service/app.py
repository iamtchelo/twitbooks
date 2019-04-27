from flask import Flask, jsonify, request
import spacy
# nlp = spacy.load('en_core_web_md')

def create_app():
    app = Flask(__name__)

    @app.route('/process', methods=["POST"])
    def process():
        message = request.json['message']
        doc = nlp(message)
        ents = [e.text for e in doc.ents if e.label_ == 'WORK_OF_ART']
        result = {'entities': ents}
        return jsonify(result)

    return app
