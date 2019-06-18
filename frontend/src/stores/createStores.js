import MessageStore from "./MessageStore";
import client from "../api/client";
import { BookStore } from "./BookStore";
import makeInspectable from 'mobx-devtools-mst';

const profile = process.env.REACT_APP_PROFILE || "dev";

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
        bookStore: debugStore(BookStore.create(booksInitialState, { client: client })),
        messageStore: debugStore(MessageStore.create(messagesInitialState, { client: client }))
    }
}