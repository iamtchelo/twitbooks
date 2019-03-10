// @flow
import {observable, runInAction, action} from 'mobx'

class BookStore {
    @observable
    books = [];
    page: number = 1;
    totalPage: number = 0;

    client: any;

    constructor(client) {
        this.client = client;
    }

    // getBooks() {
    //     this.client.get("/books")
    //         .then(response => {
    //             console.log(response.data)
    //         })
    //         .catch(e => {
    //             console.log(e);
    //         })
    // }

}

export default BookStore;