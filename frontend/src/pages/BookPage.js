import React, {Component} from "react";
import { observer, inject } from 'mobx-react';
import BookStore from "../stores/BookStore";
import { Row, Col } from 'antd';
import BookCard from "../components/BookCard";
import MainLayout from "../components/MainLayout";
import  "./BookPage.css";

@inject('bookStore') @observer
class BookPage extends Component {

    store: BookStore = this.props.bookStore;

    componentDidMount(): void {
        this.store.getBooks();
    }

    render() {
        return(
            <MainLayout>
                <div>
                    {this.renderBooks.apply(this)}
                </div>
            </MainLayout>
        )
    }

    renderBooks() {
        const data = this.store.apiData;
        return (
            <Row>
                { data.map(i => {return <Col className="book-card" span={6}><BookCard book={i.book}/></Col>}) }
            </Row>
        )
    }

}

export default BookPage;