import { types, flow, destroy, getEnv } from 'mobx-state-tree';

const BookStore = types.model({
    apiData: types.map,
    currentPage: types.integer,
    totalPages: types.integer
})
.views(self => ({
    get count() {
        return self.totalPages * 50
    },
    get data() {
        return this.apiData[this.currentPage];
    }
}))
.actions(self => ({
    setCurrentPage(page) {
        self.currentPage = page - 1;
        self.getBooks();
    },
    ignoreBook: flow(function*(book) {
        destroy(book);
        try {
            yield window.fetch(`${getEnv(self).baseUrl}/books?book_id=${book.id}`, {
                method: "PUT"})
        } catch(e) {
            console.log("ERROR", e);
        }
    }),
    getBooks: flow(function*() {
        if (self.apiData[self.currentPage]) {
            return;
        }
        try {
            const response = yield window.fetch(`${getEnv(self)}.baseUrl/books?page=${self.currentPage}`, {
                method: "GET",
                headers: {"Content-Type": "application/json"}
            });
            const responseData = yield response.json();
            self.totalPages = responseData.totalPages;
            self.apiData[self.currentPage] = responseData.content;
        } catch(e) {
            console.log("ERROR", e);
        }
    })
}));

export default BookStore;