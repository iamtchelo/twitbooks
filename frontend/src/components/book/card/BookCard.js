import React, { Component } from 'react';
import { Card, Icon } from 'antd';
import { inject, observer } from "mobx-react";
import "./BookCard.css";

const { Meta } = Card;

@inject('bookStore') @observer
class BookCard extends Component {

    book = this.props.book;

    render() {
        return (
            <Card
                style={this.props.style}
                hoverable
                className="card"
                cover={<img onClick={() => this.props.onClickEvent()} className="card-img" alt="book" src={this.book.imageUrl} />}
                actions={[this.deleteAction()]}>
                <Meta onClick={() => this.props.onClickEvent()} title={this.book.title}/>
            </Card>
        )
    }

    deleteAction() {
        return(
            <Icon type="delete" onClick={this.onDelete} style={{width: '2rem', height: '2rem'}}/>
        )
    }

    onDelete = (e) => {
        this.props.bookStore.ignoreBook(this.book);
        e.stopPropagation();
    }
}

export default BookCard;
