import { BookStore, Book } from "./BookStore";

const NOOP_CLIENT = ({
    ignoreBook() {
        return Promise.resolve();
    },
    getBooks() {
        return Promise.resolve({json: () => Promise.resolve({
                content: [],
                totalPages: 0
            })})
    }
});

test("ignoreBook should remove it from the current state", () => {
    const book = Book.create({id: 123, title: "Hai", imageUrl: "http://imagelink"});
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
        {id: 1, title: "SomeTitle", imageUrl: "SomeImageUrl"},
        {id: 2, title: "SomeOtherTitle", imageUrl: "SomeOtherImageUrl"}
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

test("Should return in-memory list if it is already downloaded for the requested page", () => {
    const books = [Book.create({id: 123, title: "Hay", imageUrl: "imageUrl"})];
    const store = BookStore.create({
        apiData: {1: books},
        currentPage: 1,
        totalPages: 0,
    }, {});
    store.getBooks();
    expect(store.apiData.get(1)).toEqual(books);
});

test("set page should subtract one from the input as to conform to the API", () => {
    const store = BookStore.create({
        apiData: {},
        currentPage: 1,
        totalPages: 0,
    }, {client: NOOP_CLIENT});
    store.setCurrentPage(3);
    expect(store.currentPage).toEqual(2);
});