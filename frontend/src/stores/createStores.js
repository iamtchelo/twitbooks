import BookStore from './BookStore'
import client from "../api/client"
import MessageStore from "./MessageStore";

export default function createStores() {
    return {
        bookStore: new BookStore(client),
        messageStore: new MessageStore(client)
    }
}