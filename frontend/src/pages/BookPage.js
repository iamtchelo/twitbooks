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
                { this.renderBooks(this.store.data) }
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

        if (!books) {
            return <div/>
        } else {
            return (
                <Row>
                    {
                        books.map(i => {
                            return (
                                <Col key = {i.book.id} className="book-card" span={6}>
                                    <BookCard book={i.book}/>
                                </Col>
                            )
                        })
                    }
                </Row>
            )
        }

    }

}

export default BookPage;