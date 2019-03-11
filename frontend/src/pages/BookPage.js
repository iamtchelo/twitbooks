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
        console.log("render");
        return(
            <div>
                {this.renderBooks()}
            </div>
        )
    }

    renderBooks() {
        return this.store.books.map(b => {
            console.log(b);
            return <div key={b.book.id}>{b.book.title}</div>
        });
    }

}

export default BookPage;