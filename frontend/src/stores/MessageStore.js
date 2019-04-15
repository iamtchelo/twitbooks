import { observable, runInAction, computed } from 'mobx';

class MessageStore {

    @observable messages = [];
    @observable currentPage = 0;
    @observable totalPages = 0;

    @computed get count() {
        return this.totalPages * 50;
    }

    client;

    constructor(client) {
        this.client = client;
    }

    clear() {
        runInAction(() => {
            this.totalPages = 0;
            this.currentPage = 0;
            this.messages.replace([]);
        })
    }

    setCurrentPage(page, bookId) {
        runInAction(() => {
            this.currentPage = page - 1;
            this.getMessages(bookId);
        });
    }

    getMessages(bookId) {
        this.client.get(`/messages/${bookId}?page=${this.currentPage}`)
            .then(response => {
                runInAction(() => {
                    const data = response.data;
                    this.messages.replace(data.content);
                    this.totalPages = data.totalPages;
                });
            })
            .catch(e => {
                console.log(e);
            })
    }

}

export default MessageStore;