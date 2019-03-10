import BookStore from './BookStore'

export default function createStores() {
    return {
        bookStore: new BookStore()
    }
}