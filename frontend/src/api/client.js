import axios from 'axios'

const client = axios.create({
    baseURL: process.env.REACT_APP_ENDPOINT || "http://localhost:8080/"
});

export default client;