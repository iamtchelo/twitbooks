import React, {Component} from "react";
import { observer, inject } from 'mobx-react';
import BookStore from "../stores/BookStore";

@inject("stores")
@observer
class BookPage extends Component {

    componentDidMount(): void {
        const store: BookStore = this.props.stores.bookStore;
        store.getBooks();
    }

    render() {
        return(
            <div>
                <p>OH HAI</p>
            </div>
        )
    }

}

export default BookPage;