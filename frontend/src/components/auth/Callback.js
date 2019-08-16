import React, { Component } from 'react';
import auth0Client from '../../auth/Auth';
import { withRouter } from 'react-router-dom';
import { message } from 'antd';

class Callback extends Component {

    async componentDidMount(): void {
        try {
            await auth0Client.handleAuthentication();
            await auth0Client.validateAuthentication();
            this.props.history.replace('/books')
        } catch (e) {
            message.error("Login error");
            this.props.history.replace('/login')
        }
    }

    render() {
        return (
            <p>Loading profile...</p>
        );
    }

}

export default withRouter(Callback);
