import { BookStore, Book } from "./BookStore";

const NOOP_CLIENT = ({
    ignoreBook() {
        return Promise.resolve();
    }
});

test("ignoreBook should remove it from the current state", () => {
    const book = Book.create({id: 123});
    const store = BookStore.create({
        apiData: {1: [book]},
        currentPage: 1,
        totalPages: 10,
    }, {client: NOOP_CLIENT});
    store.ignoreBook(book);
    expect(store.apiData.get(1)).toEqual([]);
});

test("test getBooks should update the model", async () => {

    const books = [
        {id: 1},
        {id: 2}
    ];

    const client = ({
        getBooks() {
            return Promise.resolve({
                async json() {
                    return {
                        content: books,
                        totalPages: 5
                    }
                }
            })
        }
    });

    const store = BookStore.create({
        apiData: {},
        currentPage: 0,
        totalPages: 0,
    }, {client: client});

    await store.getBooks();

    expect(store.totalPages).toEqual(5);
    expect(store.apiData.get(0)).toEqual(books);
});