import React, {Component} from "react";
import { observer, inject } from 'mobx-react/index';
import BookStore from "../../stores/BookStore";
import { Row, Col, Pagination, Card, Skeleton } from 'antd';
import BookCard from "../../components/BookCard";
import MainLayout from "../../components/MainLayout";
import "./BookPage.css";

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

    renderLoading() {
        return(
            <Row>
                {
                    [...Array(10).keys()].map(i => {
                        return(
                            <Col key={i} className="loading-card" span={6}>
                                <Card>
                                    <div>
                                        <Skeleton active
                                                  title={false}
                                                  className="loading-card-content"
                                                  paragraph={{rows: 6}}
                                        />
                                    </div>
                                </Card>
                            </Col>
                        )
                    })
                }
            </Row>
        )
    }

    renderBooks(books) {

        if (!books) {
            return this.renderLoading()
        } else {
            return (
                <Row>
                    {
                        books.map(i => {
                            return (
                                <Col key={i.book.id} className="book-card" span={6}>
                                    <BookCard onClickEvent={this.onClick(i.book)} book={i.book}/>
                                </Col>
                            )
                        })
                    }
                </Row>
            )
        }

    }

    onClick(book) {
        if (process.env.NODE_ENV === 'development') {
            return () => {this.props.history.push(`/messages/${book.id}`)}
        } else {
            const url = encodeURI(`https://www.goodreads.com/book/title?id=${book.title}`);
            return () => { window.open(url) }
        }
    }

}

export default BookPage;