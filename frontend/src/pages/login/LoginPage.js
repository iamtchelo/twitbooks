import React, { Component } from 'react';
import { Button } from 'antd';
import auth0Client from '../../auth/Auth';

class LoginPage extends Component {

    render() {
        return (
            <Button type="primary" className="login-form-button" onClick={() => this.handleClick()}>
                Log in
            </Button>
        );
    }

    handleClick() {
        auth0Client.signIn();
    }

}

export default LoginPage;