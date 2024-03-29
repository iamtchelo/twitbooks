import React, { Component } from "react";
import { inject, observer } from 'mobx-react';
import "./SyncProgress.css"
import { Button, Spin } from "antd";

const TEXT_REFRESH_INTERVAL_MS = 1000;
const UPDATE_DATA_INTERVAL_MS = 5000;

@inject('syncProgressStore', 'pageStore', 'bookStore') @observer
class SyncProgress extends Component {

    constructor(props) {
        super(props);
        this.state = { syncDisplay: "", textChangeIntervalId: undefined, dataSyncIntervalId: undefined, loading: true };
    }

    componentDidMount(): void {
        const textChangeIntervalId = setInterval(this.syncProgressIndicatorUpdate, TEXT_REFRESH_INTERVAL_MS);
        const dataSyncIntervalId = setInterval(this.dataProgressUpdate, UPDATE_DATA_INTERVAL_MS);
        this.setState({textChangeIntervalId: textChangeIntervalId, dataSyncIntervalId: dataSyncIntervalId});
        this.triggerSync();
    }

    triggerSync = () => {
        this.props.syncProgressStore.triggerSync();
    };

    componentWillUnmount(): void {
        clearInterval(this.state.textChangeIntervalId);
        clearInterval(this.state.dataSyncIntervalId);
    }

    syncProgressIndicatorUpdate = () => {
        const text = this.state.syncDisplay + ".";
        if (text.length >= 4) {
            this.setState({syncDisplay: ""})
        } else {
            this.setState({syncDisplay: text})
        }
    };

    dataProgressUpdate = async () => {
        await this.props.syncProgressStore.getProgress();
        this.setState({loading: false})
    };

    getContent = () => {
        const loading = this.state.loading;
        let content;
        if (loading) {
            content = this.renderSyncProgress();
        } else {
            content = this.renderContent();
        }
        return content;
    };

    render() {
        return(
            <div className="sync-progress-root">
                {this.getContent()}
            </div>
        )
    }

    renderContent = () => {
        return(
            <div className="sync-content">
                <span className="sync-title">{`Syncing data${this.state.syncDisplay}`}</span>
                {this.renderMessageSync()}
                {this.renderBookSync()}
                {this.renderBookNavigation()}
            </div>
        )
    };

    renderSyncProgress = () => {
        return(
            <div className="sync-loading">
                <Spin tip="Loading" size="large"/>
            </div>
        )
    };

    renderBookNavigation() {
        const books = this.props.syncProgressStore.progress.bookCount || 0;
        if (books === 0) {
            return <div />
        }
        return(
            <Button type="primary" className="" onClick={this.navigateToBooks}>
                Go to Books
            </Button>
        )
    }

    navigateToBooks = () => {
        this.props.bookStore.getBooks();
        this.props.pageStore.hideProgress();
    };

    renderMessageSync() {
        const data = this.props.syncProgressStore.progress;
        const totalMessages = data.totalMessages || 0;
        const syncedMessages = data.syncedMessages || 0;
        return <span className="sync-item">{`${syncedMessages} of ${totalMessages} messages processed`}</span>
    }

    renderBookSync() {
        const books = this.props.syncProgressStore.progress.bookCount || 0;
        return <span className="sync-item">{`${books} books discovered`}</span>
    }

}

export default SyncProgress;