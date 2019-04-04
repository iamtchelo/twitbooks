import React, { Component } from 'react';
import { Layout} from 'antd';
import "./MainLayout.css"

const { Header, Content, Footer } = Layout;

export default class MainLayout extends Component {

    render() {
        return (
            <Layout>
                <Header>
                    <div class="title">TWIT BOOKS</div>
                </Header>
                <Content className="content">
                    {this.props.children}
                </Content>
            </Layout>
        );
    }

}
