import React, { Component } from 'react';
import { Button } from 'antd';
import getAuthClient from '../../auth/Auth';
import "./login.css";

class LoginPage extends Component {

    render() {
        return (
            <div className="rootContainer">
                <div className="loginContainer">
                    <span className="loginText">Twitbooks</span>
                    <Button type="primary" className="login-form-button loginButton" onClick={() => this.handleClick()}>
                        Log in
                    </Button>
                </div>
            </div>
        );
    }

    handleClick() {
        getAuthClient().signIn();
    }

}

export default LoginPage;