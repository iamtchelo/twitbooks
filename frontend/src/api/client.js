import axios from 'axios'
import auth0Client from '../auth/Auth';

const client = axios.create({
    baseURL: process.env.REACT_APP_ENDPOINT || "http://localhost:8080/api/v1"
});

client.interceptors.request.use(
    config => {
        config.headers.Authorization = `Bearer ${auth0Client.getIdToken()}`;
        return config;
    },
    error => Promise.reject(error)
);

export default client;