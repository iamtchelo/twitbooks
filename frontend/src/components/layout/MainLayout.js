import React, { Component } from 'react';
import { Card, Layout } from 'antd';
import "./MainLayout.css"
import auth0Client from '../../auth/Auth'

const { Header, Content } = Layout;

export default class MainLayout extends Component {

    render() {
        return (
            <Layout className="App">
                <Header className="header">
                    <span className="title">TWIT BOOKS</span>
                    <div className="logout-container" onClick={this.logoutAction}>
                        <span className="logout">Logout</span>
                    </div>
                </Header>
                    <Content className="content">
                        <Card>
                            {this.props.children}
                        </Card>
                    </Content>
            </Layout>
        );
    }

    logoutAction = () => {
        auth0Client.signOut();
    };
}
