import React, { Component } from 'react';
import MainLayout from "../components/MainLayout";
import { observer, inject } from 'mobx-react';
import { Pagination, List } from "antd";

@inject('messageStore') @observer
class MessagePage extends Component {

    store = this.props.messageStore;
    bookId = 0;

    componentDidMount(): void {
        this.store.clear();
        this.store.getMessages(this.props.match.params.bookId);
    }

    render() {
        return(
            <MainLayout>
                { this.renderMessages(this.store.messages) }
                { this.renderPagination(this.store.totalPages) }
            </MainLayout>
        )
    }

    renderMessages(messages) {
        return(
            <List
                bordered
                dataSource={messages}
                renderItem={item => (<List.Item> {item.text} </List.Item>)}
            >
            </List>
        )
    }

    renderPagination(totalElements) {
        if (totalElements > 0) {
            return (
                <Pagination
                    total={totalElements}
                    hideOnSinglePage={true}
                    pageSize={50}
                    onChange={(page) => this.doOnChange(page)}
                />
            )
        }
    }

    doOnChange(page) {
        this.store.setCurrentPage(page, this.bookId);
    }

}


export default MessagePage;