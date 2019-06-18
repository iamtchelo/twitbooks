import React, { Component } from "react";
import { observer, inject } from 'mobx-react';
import { Row, Col, Pagination, Card, Skeleton } from 'antd';
import BookCard from "../../components/book/card/BookCard";
import MainLayout from "../../components/layout/MainLayout";
import  "./BookPage.css";

@inject('bookStore') @observer
class BookPage extends Component {

    store = this.props.bookStore;

    constructor(props) {
        super(props);
        this.state = {
            spanSize: this.initialSpanSize(),
            renderGrid: this.initialRenderWindow()
        };
    }

    componentDidMount(): void {
        this.store.getBooks();
    }

    componentWillMount(): void {
        window.addEventListener("resize", this.resizeListener)
    }

    initialSpanSize() {
        if (window.innerWidth < 600) {
            return 12;
        } else {
            return 6
        }
    }

    initialRenderWindow() {
        if (window.innerWidth < 400) {
            return false;
        } else {
            return true;
        }
    }

    resizeListener = () => {
        if (window.innerWidth < 600 && this.state.spanSize !== 12) {
            this.setState({spanSize: 12});
        } else {
            if (window.innerWidth > 600 && this.state.spanSize !== 6) {
                this.setState({spanSize: 6})
            }
        }
        if (window.innerWidth < 400 && this.state.renderGrid === true) {
            this.setState({renderGrid: false})
        }
        if (window.innerWidth > 400 && this.state.renderGrid === false) {
            this.setState({renderGrid: true})
        }
    };

    componentWillUnmount(): void {
        window.removeEventListener("resize", this.resizeListener);
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
        if (this.state.renderGrid) {
            return this.renderLoadingGrid()
        } else {
            return this.renderLoadingList()
        }
    }

    renderLoadingList() {
        return(
            <div style={{flex: 1, flexDirection: "column"}}>
                {
                    [...Array(10).keys()].map(i => {
                        return(
                            <div key={i} className="loading-card">
                                <Skeleton active
                                          title={false}
                                          className="loading-card-content"
                                          paragraph={{rows: 6}}
                                />
                            </div>
                        )
                    })
                }
            </div>
        )
    }

    renderLoadingGrid() {
        return(
            <Row>
                {
                    [...Array(10).keys()].map(i => {
                        return(
                            <Col key={i} className="loading-card" span={this.state.spanSize}>
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
            if (this.state.renderGrid) {
                return this.renderBookGrid(books)
            } else {
                return this.renderBookList(books)
            }
        }
    }

    renderBookGrid(books) {
        return (
            <Row>
                {
                    books.map(i => {
                        return this.renderBookCard(i)
                    })
                }
            </Row>
        )
    }

    renderBookList(books) {
        return (
            <div style={{flex: 1, flexDirection: "column"}}>
                {
                    books.map(i => {
                        return (
                            <BookCard key={i.book.id} onClickEvent={this.onClick(i.book)} book={i.book}/>
                        )
                    })
                }
            </div>
        )
    }

    renderBookCard(book) {
        return (
            <Col key={book.id} className="book-card" span={this.state.spanSize}>
                <BookCard onClickEvent={this.onClick(book)} book={book}/>
            </Col>
        )
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