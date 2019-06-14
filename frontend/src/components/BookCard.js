import React, { Component } from 'react';
import { Card, Icon } from 'antd';
import { inject, observer } from "mobx-react";
import "./BookCard.css";

const { Meta } = Card;

@inject('bookStore') @observer
class BookCard extends Component {

    book: Book = this.props.book;

    render() {
        return (
            <Card
                onClick={() => this.props.onClickEvent()}
                hoverable
                className="card"
                cover={<img className="card-img" alt="book" src={this.book.imageUrl} />}
                actions={[<Icon type="delete" onClick={this.onDelete}/>]}>
                <Meta title={this.book.title}/>
            </Card>
        )
    }

    onDelete = (e) => {
        this.props.bookStore.ignoreBook(this.book);
        e.stopPropagation();
    }
}

export default BookCard;
