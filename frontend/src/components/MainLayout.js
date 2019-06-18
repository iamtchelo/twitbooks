import React, { Component } from 'react';
import { Card, Layout } from 'antd';
import "./MainLayout.css"

const { Header, Content } = Layout;

export default class MainLayout extends Component {

    render() {
        return (
            <Layout className="App">
                <Header>
                    <div className="title">TWIT BOOKS</div>
                </Header>
                    <Content className="content">
                        <Card>
                            {this.props.children}
                        </Card>
                    </Content>
            </Layout>
        );
    }

}
