import React, {Component} from "react";
import { observer, inject } from 'mobx-react';
import BookStore from "../stores/BookStore";

@inject('bookStore') @observer
class BookPage extends Component {

    //store: BookStore;

    componentDidMount(): void {
        console.log(this.props);
        this.props.bookStore.getBooks();
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
        if (this.props.bookStore == null) {
            return <div></div>;
        } else {
            return this.props.bookStore.books.map(b => {
                console.log(b);
                return <div key={b.book.id}>{b.book.title}</div>
            });
        }
    }

}

export default BookPage;