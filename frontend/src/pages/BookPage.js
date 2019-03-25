import React, {Component} from "react";
import { observer, inject } from 'mobx-react';
import BookStore from "../stores/BookStore";
import { Row, Col } from 'antd';
import BookCard from "../components/BookCard";

@inject('bookStore') @observer
class BookPage extends Component {

    store: BookStore = this.props.bookStore;

    componentDidMount(): void {
        this.store.getBooks();
    }

    render() {
        return(
            <div>
                {this.renderBooks.apply(this)}
            </div>
        )
    }

    renderBooks() {
        const data = this.store.apiData;
        return (
            <Row>
                { data.map(i => {return <Col span={6}><BookCard book={i.book}/></Col>}) }) }
            </Row>
        )
    }

}

export default BookPage;