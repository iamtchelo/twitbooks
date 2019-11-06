FROM node:dubnium-alpine

ENV NPM_CONFIG_LOGLEVEL warn

WORKDIR /app

COPY . .

RUN npm ci

EXPOSE 3000

CMD ["npm", "start"]