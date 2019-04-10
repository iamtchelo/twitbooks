import React, { Component } from 'react';
import MainLayout from "../components/MainLayout";
import { observer, inject } from 'mobx-react';
import {Pagination} from "antd";

@inject('messageStore') @observer
class MessagePage extends Component {

    store = this.props.messageStore;
    bookId = 0;

    render() {
        console.log(this.props);
        return(
            <MainLayout>
            </MainLayout>
        )
    }

    renderPagination(totalElements) {
        if (totalElements > 0) {
            return (
                <Pagination
                    total={totalElements}
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