import React, {Component} from "react";
import { observer, inject } from 'mobx-react';
import BookStore from "../stores/BookStore";

@inject('stores') @observer
class BookPage extends Component {

    store: BookStore;

    componentDidMount(): void {
        console.log(this.props);
        this.store = this.props.stores.bookStore;
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
        if (this.store == null) {
            return <div></div>;
        } else {
            return this.store.books.map(b => {
                return <div key={b.id}>{b.title}</div>
            });
        }
    }

}

export default BookPage;