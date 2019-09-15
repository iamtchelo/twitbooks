import { types, flow, getEnv } from 'mobx-state-tree';

export const Book = types.model({
    id: types.integer,
    title: types.string,
    imageUrl: types.string,
    detailsUrl: types.string,
});

export const BookStore = types.model({
    apiData: types.map(types.array(Book)),
    currentPage: types.integer,
    totalPages: types.integer
})
.views(self => ({
    get count() {
        return self.totalPages * 50
    },
    get data() {
        return this.apiData.get(self.currentPage);
    }
}))
.actions(self => ({
    setCurrentPage(page) {
        self.currentPage = page - 1;
        self.getBooks();
    },
    ignoreBook: flow(function*(book) {
        try {
            const bookId = book.id;
            self.apiData.get(self.currentPage).remove(book);
            yield getEnv(self).client.ignoreBook(bookId);
        } catch(e) {
            // TODO error handling
        }
    }),
    getBooks: flow(function*() {
        if (self.apiData.get(self.currentPage)) {
            return;
        }
        try {
            const response = yield getEnv(self).client.getBooks(self.currentPage);
            const responseData = yield response.json();
            self.totalPages = responseData.totalPages;
            self.apiData.set(self.currentPage,responseData.content);
        } catch(e) {
            // TODO error handling
        }
    })
}));
