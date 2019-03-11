// @flow
import {observable, runInAction, action} from 'mobx'

class BookStore {
    @observable books = [];
    page: number = 1;
    totalPages: number = 0;

    client: any;

    constructor(client) {
        this.client = client;
    }

    @action.bound
    getBooks() {
        this.client.get(`/books?page=${this.page}`)
            .then(response => {
                runInAction(() => {
                    const data = response.data;
                    const content = response.data.content;
                    console.log("content", content);
                    this.totalPages = data.totalPages;
                    this.page++;
                    this.books = [...this.books, ...content];
                    console.log("Store ", this);
                });
            })
            .catch(e => {
                console.log(e);
            })
    }

}

export default BookStore;