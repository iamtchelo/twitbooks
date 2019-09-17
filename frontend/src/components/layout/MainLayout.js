import React, { Component } from 'react';
import { Layout, Menu, Icon } from 'antd';
import "./MainLayout.css"
import auth0Client from '../../auth/Auth'

const { SubMenu } = Menu;
const { Header, Content } = Layout;

export default class MainLayout extends Component {

    constructor(props) {
       super(props);
        this.state = {
            current: ''
        };
    }

    render() {
        return (
            <Layout className="app">
                <Header className="header">
                    <span className="title">TWITBOOKS</span>
                    <div className="logout-container">
                        {this.renderMenu()}
                    </div>
                </Header>
                    <Content className="content">
                        {this.props.children}
                    </Content>
            </Layout>
        );
    }

    handleMenuClick = e => {
        console.log('click', e);
        if (e.key === 'logout') {
            this.logoutAction();
        }
        this.setState({
            current: e.key,
        })
    };

    renderMenu() {
        return(
            <Menu className="menu" onClick={this.handleMenuClick} selectedKeys={[this.state.current]} mode="horizontal">
                <SubMenu className="sub-menu"
                    title={<Icon style={{fontSize: "3.6rem", color: "#ECECFF"}} type="more"/>}
                >
                    <Menu.Item key="progress">Sync Progress</Menu.Item>
                    <Menu.Item key="logout">Logout</Menu.Item>
                </SubMenu>
            </Menu>
        )
    }

    logoutAction = () => {
        auth0Client.signOut();
    };

}
