import BookStore from './BookStore'
import client from "../api/client"

export default function createStores() {
    return {
        bookStore: new BookStore(client)
    }
}