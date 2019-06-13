import React, {Component} from "react";
import { observer, inject } from 'mobx-react';
import BookStore from "../stores/BookStore";
import { Row, Col, Pagination, Card, Spin, Skeleton } from 'antd';
import BookCard from "../components/BookCard";
import MainLayout from "../components/MainLayout";
import  "./BookPage.css";

@inject('bookStore') @observer
class BookPage extends Component {

    store: BookStore = this.props.bookStore;

    constructor(props) {
        super(props);
        this.state = {
            spanSize: this.initialSpanSize(),
            renderGrid: this.initialRenderWindow()
        };
    }

    componentDidMount(): void {
        console.log("HAI");
        this.store.getBooks();
    }

    componentWillMount(): void {
        window.addEventListener("resize", this.resizeListener)
    }

    initialSpanSize() {
        console.log("INITIAL SPAN CALCULATION");
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
        console.log("TEST", window.innerWidth);
        if (window.innerWidth < 600 && this.state.spanSize !== 12) {
            console.log("SPAN SET TO 12");
            this.setState({spanSize: 12});
            console.log("STATE", this.state);
        } else {
            if (window.innerWidth > 600 && this.state.spanSize !== 6) {
                console.log("SPAN SET TO 6");
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
            if (this.state.renderGrid) {
                console.log("RENDERING GRID");
                return this.renderBookGrid(books)
            } else {
                console.log("RENDERING LIST");
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
            <Col key={book.book.id} className="book-card" span={this.state.spanSize}>
                <BookCard onClickEvent={this.onClick(book.book)} book={book.book}/>
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