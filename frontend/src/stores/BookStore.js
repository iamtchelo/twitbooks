import {observable, runInAction, computed} from 'mobx'

class BookStore {
    @observable apiData = {};
    @observable currentPage = 0;
    @observable totalPages = 0;
    @observable currentData = [];

    // @computed get count() {
    //     return this.totalPages * 50;
    // }

    client: any;

    constructor(client) {
        this.client = client;
    }

    setCurrentPage(page) {
        console.log("setCurrentPage");
        console.log(this);
        runInAction(() => {
            console.log(page);
            console.log(this.currentPage);
            console.log("test 1 " + page);
            this.currentPage = page - 1;
            this.getBooks();
        })
    }

    getBooks() {
        console.log("getting book page " + this.currentPage);
        if (this.apiData[this.currentPage]) {
            console.log("hai");
            this.currentData = this.apiData[this.currentPage];
        }
        this.client.get(`/books?page=${this.currentPage}`)
            .then(response => {
                runInAction(() => {
                    const data = response.data;
                    const content = response.data.content;
                    this.totalPages = data.totalPages;
                    this.apiData[this.currentPage] = content;
                    this.currentData = this.apiData[this.currentPage];
                    console.log("total pages = " + this.totalPages);
                });
            })
            .catch(e => {
                console.log(e);
            })
    }

}

export default BookStore;