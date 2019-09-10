import React, { Component } from 'react';
import { Layout } from 'antd';
import "./MainLayout.css"
import auth0Client from '../../auth/Auth'

const { Header, Content } = Layout;

export default class MainLayout extends Component {

    render() {
        return (
            <Layout className="app">
                <Header className="header">
                    <span className="title">TWITBOOKS</span>
                    <div className="logout-container" onClick={this.logoutAction}>
                        <span className="logout">Logout</span>
                    </div>
                </Header>
                    <Content className="content">
                        {this.props.children}
                    </Content>
            </Layout>
        );
    }

    logoutAction = () => {
        auth0Client.signOut();
    };
}
