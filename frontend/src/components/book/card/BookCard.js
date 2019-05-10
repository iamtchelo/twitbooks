import React, { Component } from 'react';
import { Card } from 'antd';
import "./BookCard.css";

const { Meta } = Card;

class BookCard extends Component {

    book: Book = this.props.book;

    render() {
        return (
            <Card
                onClick={() => this.props.onClickEvent()}
                hoverable
                className="card"
                cover={<img className="card-img" alt="book" src={this.book.imageUrl} />}
            >
                <Meta title={this.book.title}/>
            </Card>
        )
    }
}

export default BookCard;

