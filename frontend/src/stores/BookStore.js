import {observable, runInAction, action} from 'mobx'

class BookStore {
    @observable
    books = [];
    page = 1;
}

export default BookStore;