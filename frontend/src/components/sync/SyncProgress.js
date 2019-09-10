import React, { Component } from "react";
import "./SyncProgress.css"

class SyncProgress extends Component {

    constructor(props) {
        super(props);
        this.state = { syncDisplay: "", intervalId: undefined };
    }

    componentDidMount(): void {
        const intervalId = setInterval(this.syncProgressIndicatorUpdate, 1000);
        this.setState({intervalId: intervalId})
    }

    componentWillUnmount(): void {
        clearInterval(this.state.intervalId);
    }

    syncProgressIndicatorUpdate = () => {
        const text = this.state.syncDisplay + ".";
        if (text.length >= 4) {
            this.setState({syncDisplay: ""})
        } else {
            this.setState({syncDisplay: text})
        }
    };

    render() {
        return(
            <div className="sync-progress-root">
                <div className="sync-content">
                    <span className="sync-title">{`Syncing data${this.state.syncDisplay}`}</span>
                    {this.renderFriendSync()}
                    {this.renderMessageSync()}
                    {this.renderBookSync()}
                </div>
            </div>
        )
    }

    renderFriendSync() {
        const data = this.props.data;
        const totalFriends = data.totalFriends || 0;
        const syncedFriends = data.syncedFriends || 0;
        return <span className="sync-item">{`${syncedFriends} of ${totalFriends} friends`}</span>
    }

    renderMessageSync() {
        const data = this.props.data;
        const totalMessages = data.totalMessages || 0;
        const syncedMessages = data.syncedMessages || 0;
        return <span className="sync-item">{`${syncedMessages} of ${totalMessages} messages`}</span>
    }

    renderBookSync() {
        const books = this.props.data.bookCount || 0;
        return <span className="sync-item">{`${books} books discovered`}</span>
    }

}

export default SyncProgress;