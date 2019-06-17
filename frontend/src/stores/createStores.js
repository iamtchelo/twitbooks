import BookStore from './BookStore'
import MessageStore from "./MessageStore";
import client from "../api/client";

const baseUrl = process.env.REACT_APP_ENDPOINT || "http://localhost:8080/";

const apiClient = client(baseUrl);

const booksInitialState = {
    apiData:  {},
    currentPage: 0,
    totalPages: 0,
};

const messagesInitialState = {
    messages: [],
    currentPage: 0,
    totalPages: 0
};

export default function createStores() {
    return {
        bookStore: BookStore.create(booksInitialState, { client: apiClient }),
        messageStore: MessageStore.create(messagesInitialState, { client: apiClient })
    }
}