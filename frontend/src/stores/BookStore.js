import { observable, runInAction, computed } from 'mobx'

class BookStore {
    @observable apiData = {};
    @observable currentPage = 0;
    @observable totalPages = 0;
    @observable currentData = [];

    @computed get count() {
        return this.totalPages * 50;
    }

    client: any;

    constructor(client) {
        this.client = client;
    }

    setCurrentPage(page) {
        runInAction(() => {
            this.currentPage = page - 1;
            this.getBooks();
        })
    }

    getBooks() {
        console.log("getting book page " + this.currentPage);
        if (this.apiData[this.currentPage]) {
            console.log("Using cached page " + this.currentPage);
            runInAction(() => {})
            this.currentData = this.apiData[this.currentPage];
        }
        this.client.get(`/books?page=${this.currentPage}`)
            .then(response => {
                runInAction(() => {
                    console.log("page downloaded " + this.currentPage);
                    const data = response.data;
                    const content = response.data.content;
                    this.totalPages = data.totalPages;
                    this.apiData[this.currentPage] = content;
                    this.currentData = this.apiData[this.currentPage];
                    console.log("downloaded", this.currentData);
                });
            })
            .catch(e => {
                console.log(e);
            })
    }

}

export default BookStore;