import React, { Component } from 'react';
import { Button } from 'antd';

class LoginPage extends Component {

    render() {
        return (
            <Button type="primary" className="login-form-button" onClick={() => this.handleClick()}>
                Log in
            </Button>
        );
    }

    handleClick() {
        console.log("LOGIN ACTION");
    }

}

export default LoginPage;