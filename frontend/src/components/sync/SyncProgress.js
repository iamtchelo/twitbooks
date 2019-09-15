import React, { Component } from "react";
import { inject, observer } from 'mobx-react';
import "./SyncProgress.css"

const TEXT_REFRESH_INTERVAL_MS = 1000;
const UPDATE_DATA_INTERVAL_MS = 5000;

@inject('syncProgressStore') @observer
class SyncProgress extends Component {

    constructor(props) {
        super(props);
        this.state = { syncDisplay: "", textChangeIntervalId: undefined, dataSyncIntervalId: undefined };
    }

    componentDidMount(): void {
        const textChangeIntervalId = setInterval(this.syncProgressIndicatorUpdate, TEXT_REFRESH_INTERVAL_MS);
        const dataSyncIntervalId = setInterval(this.dataProgressUpdate, UPDATE_DATA_INTERVAL_MS);
        this.setState({textChangeIntervalId: textChangeIntervalId, dataSyncIntervalId: dataSyncIntervalId});
    }

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
    };

    render() {
        return(
            <div className="sync-progress-root">
                <div className="sync-content">
                    <span className="sync-title">{`Syncing data${this.state.syncDisplay}`}</span>
                    {this.renderMessageSync()}
                    {this.renderBookSync()}
                </div>
            </div>
        )
    }

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