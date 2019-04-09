import { observable, runInAction } from 'mobx';

class MessageStore {

    @observable messages = [];

    @observable currentPage = 0;

    client;

    constructor(client) {
        this.client = client;
    }

    setCurrentPage(page, bookId) {
        this.currentPage = page - 1;
        this.getMessages(bookId);
    }

    getMessages(bookId) {
        this.client.get(`/messages/${bookId}?page=${this.currentPage}`)
            .then(response => {
                runInAction(() => {
                    const data = response.data;
                    this.totalPages = data.totalPages;
                });
            })
            .catch(e => {
                console.log(e);
            })
    }

}

export default MessageStore;