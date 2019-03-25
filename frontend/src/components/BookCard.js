import React, { Component } from 'react';
import { Card } from 'antd';

const { Meta } = Card;

class BookCard extends Component {

    book: Book = this.props.book;

    render() {
        return (
            <Card
                hoverable
                style={{width: "200px"}}
                cover={<img alt="book" src={this.book.imageUrl} />}
            >
                <Meta title={this.book.title}/>
            </Card>
        )
    }
}

export default BookCard;

