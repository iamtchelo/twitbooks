import { types, flow, getEnv } from 'mobx-state-tree';

const MessageStore = types.model({
    messages: types.array,
    currentPage: types.integer,
    totalPages: types.integer
})
.views(self => ({
    get count() {
        return self.totalPages * 50;
    }
}))
.actions(self => ({
    clear() {
        self.totalPages = 0;
        self.currentPage = 0;
        self.messages.replace([])
    },
    setCurrentPage(page, bookId) {
        self.currentPage = page - 1;
        self.getMessages(bookId);
    },
    getMessages: flow(function*(bookId) {
        try {
            const response = yield window.fetch(`${getEnv(self)}.baseUrl/messages/${bookId}?page=${self.currentPage}`, {
                method: 'GET',
                headers: {"Content-Type": 'application/json'}
            });
            const data = yield response.json();
            self.totalPages = data.totalPages;
            self.messages = data.content;
        } catch(e) {
            console.log(e);
        }
    })
}));

export default MessageStore;