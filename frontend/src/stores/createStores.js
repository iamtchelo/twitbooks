import BookStore from './BookStore'
import MessageStore from "./MessageStore";

const baseUrl = process.env.REACT_APP_ENDPOINT || "http://localhost:8080/";

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
        bookStore: BookStore.create(booksInitialState, { baseUrl: baseUrl }),
        messageStore: MessageStore.create(messagesInitialState, { baseUrl: baseUrl })
    }
}