import MessageStore from "./MessageStore";
import client from "../api/client";
import { BookStore } from "./BookStore";
import makeInspectable from 'mobx-devtools-mst';

const baseUrl = process.env.REACT_APP_ENDPOINT || "http://localhost:8080/";
const profile = process.env.REACT_APP_PROFILE || "dev";

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

const debugStore = (store) => {
    if (profile === "dev") {
        makeInspectable(store);
    }
    return store;
};

export default function createStores() {
    return {
        bookStore: debugStore(BookStore.create(booksInitialState, { client: apiClient })),
        messageStore: debugStore(MessageStore.create(messagesInitialState, { client: apiClient }))
    }
}