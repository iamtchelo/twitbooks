import React, { Component } from 'react';
import auth0Client from '../../auth/Auth';
import { withRouter } from 'react-router-dom';

class Callback extends Component {

    async componentDidMount(): void {
        await auth0Client.handleAuthentication();
        await auth0Client.validateAuthentication();
        this.props.history.replace('/books')
    }

    render() {
        return (
            <p>Loading profile...</p>
        );
    }

}

export default withRouter(Callback);
