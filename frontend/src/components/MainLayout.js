import React, { Component } from 'react';
import { Layout} from 'antd';
import "./MainLayout.css"

const { Header, Content, Footer } = Layout;

export default class MainLayout extends Component {

    render() {
        return (
            <Layout>
                <Header/>
                <Content>
                    {this.props.children}
                </Content>
            </Layout>
        );
    }

}
