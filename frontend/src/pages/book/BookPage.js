import React, { Component } from "react";
import { observer, inject } from 'mobx-react';
import { Row, Col, Pagination, Card, Skeleton } from 'antd';
import BookCard from "../../components/book/card/BookCard";
import MainLayout from "../../components/layout/MainLayout";
import { TransitionGroup, CSSTransition } from 'react-transition-group';
import  "./BookPage.css";
import SyncProgress from "../../components/sync/SyncProgress";

@inject('bookStore', 'pageStore') @observer
class BookPage extends Component {

    bookStore = this.props.bookStore;

    constructor(props) {
        super(props);
        this.state = {
            spanSize: this.calculateSpanSize(),
            renderGrid: this.shouldRenderGrid()
        };
    }

    componentDidMount(): void {
        this.bookStore.getBooks();
    }

    componentWillMount(): void {
        window.addEventListener("resize", this.resizeListener)
    }

    calculateSpanSize() {
        if (window.innerWidth < 600) {
            return 12;
        } else if (window.innerWidth < 1500) {
            return 6;
        }
        else {
            return 3
        }
    }

    shouldRenderGrid() {
        return window.innerWidth >= 400;
    }

    resizeListener = () => {
        this.setState({
            spanSize: this.calculateSpanSize(),
            renderGrid: this.shouldRenderGrid()})
    };

    componentWillUnmount(): void {
        window.removeEventListener("resize", this.resizeListener);
    }

    render() {
        return(
            <MainLayout>
                {this.renderBooksOrSync()}
            </MainLayout>
        )
    }

    renderBooksOrSync() {
        const books = this.bookStore.data;
        console.log("SHOW PROGRESS", this.props.pageStore.progress);
        if ((books && books.length === 0) || this.props.pageStore.progress) {
            return this.renderSyncProgress();
        } else {
            return(
                <Card className="books-container">
                    { this.renderBooks(this.bookStore.data) }
                    { this.renderPagination(this.bookStore.count) }
                </Card>
            )
        }
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
        window.scrollTo(0, 0);
        this.bookStore.setCurrentPage(page);
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
            return this.renderLoading();
        }
        else if (books.length === 0) {
            return <div/>
        }
        else {
            if (this.state.renderGrid) {
                return this.renderBookGrid(books);
            } else {
                return this.renderBookList(books);
            }
        }
    }

    renderSyncProgress() {
        return <SyncProgress />
    }

    renderBookGrid(books) {
        return (
            <Row>
                <TransitionGroup>
                {
                    books.map(i => {
                        return this.renderBookCard(i)
                    })
                }
                </TransitionGroup>
            </Row>
        )
    }

    renderBookList(books) {
        return (
            <div style={{flex: 1, flexDirection: "column"}}>
                <TransitionGroup>
                    {
                        books.map(i => {
                            return (
                                <CSSTransition key={i.id} timeout={500} classNames="grid-item">
                                    <BookCard
                                        onClickEvent={this.onClick(i)}
                                        book={i}/>
                                </CSSTransition>
                            )
                        })
                    }
                </TransitionGroup>
            </div>
        )
    }

    renderBookCard(book) {
        return (
            <CSSTransition key={book.id} timeout={500} classNames="grid-item">
                <Col className="book-card" span={this.state.spanSize}>
                    <BookCard onClickEvent={this.onClick(book)} book={book}/>
                </Col>
            </CSSTransition>
        )
    }

    onClick(book) {
        const url = book.detailsUrl;
        return () => { window.open(url) }
    }

}

export default BookPage;
