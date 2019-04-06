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
    storeContext = this.store.this;

    componentDidMount(): void {
        this.store.getBooks();
    }

    render() {
        console.log("totallll " + this.store.totalPages);
        return(
            <MainLayout>
                {this.renderBooks(this.store.currentData)}
                {
                    if (totalElements > 0) {
                    return (
                    <Pagination
                    total={totalElements}
                    pageSize={50}
                    hideOnSinglePage={false}
                    onChange={ () => this.store.setCurrentPage.apply(this.storeContext) }
                    />
                    )
                }

                }
            </MainLayout>
        )
    }

    renderPagination(totalElements) {
    }

    renderBooks(books) {

        if (!books) {
            return <div/>
        }

        console.log("HAI");
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