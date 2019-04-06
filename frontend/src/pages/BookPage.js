import React, {Component} from "react";
import { observer, inject } from 'mobx-react';
import BookStore from "../stores/BookStore";
import { Row, Col, Pagination } from 'antd';
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
                { this.renderBooks(this.store.currentData) }
                { this.renderPagination(this.store.count) }
            </MainLayout>
        )
    }

    renderPagination(totalElements) {
        if (totalElements > 0) {
            return (
                <Pagination
                    total={totalElements}
                    pageSize={50}
                    hideOnSinglePage={false}
                    onChange={(page) => this.doOnChange(page)}
                />
            )
        }
    }

    doOnChange(page) {
        this.store.setCurrentPage(page);
    }

    renderBooks(books) {

        console.log("books", books);

        if (!books) {
            return <div/>
        }

        console.log("rendering books");
        return (
            <Row>
                {
                    books.map(i => {
                        return (
                            <Col className="book-card" span={6}>
                                <BookCard book={i.book}/>
                            </Col>
                        )
                    })
                }
            </Row>
        )
    }

}

export default BookPage;