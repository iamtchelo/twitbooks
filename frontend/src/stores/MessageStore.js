import { types, flow, getEnv } from 'mobx-state-tree';

export  const Message = types.model({
    id: types.integer,
    text: types.string
});

export const MessageStore = types.model({
    messages: types.array(Message),
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
            const response = yield getEnv(self).client.getMessages(bookId, self.currentPage);
            const data = yield response.json();
            self.totalPages = data.totalPages;
            self.messages = data.content;
        } catch(e) {
            // TODO error handling
        }
    })
}));

export default MessageStore;