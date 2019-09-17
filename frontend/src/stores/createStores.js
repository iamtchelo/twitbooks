import MessageStore from "./MessageStore";
import client from "../api/client";
import { BookStore } from "./BookStore";
import makeInspectable from 'mobx-devtools-mst';
import { SyncProgressStore } from "./SyncProgressStore";
import {PageStore} from "./PageStore";

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

const syncProgressInitialState = {
    progress: {
        totalMessages: 0,
        syncedMessages: 0,
        bookCount: 0
    }
};

const pageStoreInitialState = {
    progress: false
};

const debugStore = (store) => {
    if (profile === "dev") {
        makeInspectable(store);
    }
    return store;
};

export default function createStores() {
    const apiClient = {client: client};
    return {
        bookStore: debugStore(BookStore.create(booksInitialState, apiClient)),
        messageStore: debugStore(MessageStore.create(messagesInitialState, apiClient)),
        syncProgressStore: debugStore(SyncProgressStore.create(syncProgressInitialState, apiClient)),
        pageStore: debugStore(PageStore.create(pageStoreInitialState))
    }
}