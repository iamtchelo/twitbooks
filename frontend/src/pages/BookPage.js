import React, {Component} from "react";
import { observer, inject } from 'mobx-react';
import BookStore from "../stores/BookStore";

@inject('bookStore') @observer
class BookPage extends Component {

    store: BookStore = this.props.bookStore;

    componentDidMount(): void {
        this.store.getBooks();
    }

    render() {
        return(
            <div>
                {this.renderBooks()}
            </div>
        )
    }

    renderBooks() {
        return this.store.apiData.map(apiData => {
            const {book} = apiData;
            return <div key={book.id}>{book.title}</div>
        });
    }

}

export default BookPage;