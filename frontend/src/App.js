import React, { Component } from 'react';
import createStores from "./stores/createStores";
import { Provider } from "mobx-react";
import { configure } from 'mobx';
import BookPage from "./pages/book/BookPage";
import { Switch, Route, BrowserRouter } from 'react-router-dom';
import MessagePage from "./pages/message/MessagePage";
import LoginPage from "./pages/login/LoginPage";
import SecuredRoute from "./components/route/SecuredRoute";
import getAuthClient from './auth/Auth';
import Callback from "./components/auth/Callback";
import DevSecureRoute from "./components/route/DevSecureRoute";

configure({
    enforceActions: 'always'
});

const stores = createStores();

class App extends Component {

    constructor(props) {
        super(props);
        this.state = {
            checkingSession: true,
        }
    }

    async componentDidMount(): void {
        if (process.env.NODE_ENV === "production") {
            try {
                await getAuthClient().silentAuth();
                this.forceUpdate();
            } catch (err) {
                if (err.error !== 'login_required') {
                    // TODO error handling
                }
            }
            this.setState({checkingSession: false})
        }
    }

    getRoutes = () => {
        if (process.env.NODE_ENV === "production") {
            return this.getProductionRoutes();
        } else {
            return this.getDevelopmentRoutes();
        }
    };

    getProductionRoutes = () => {
        return(
            <Switch>
                <Route path="/messages/:bookId" component={MessagePage} checkingSession={this.state.checkingSession}/>
                <Route path="/login" component={LoginPage}/>
                <Route path="/callback" component={Callback}/>
                <SecuredRoute path="/" component={BookPage} checkingSession={this.state.checkingSession}/>
            </Switch>
        )
    };

    getDevelopmentRoutes = () => {
        return(
            <Switch>
                <Route path="/messages/:bookId" component={MessagePage} />
                <DevSecureRoute path="/" component={BookPage} />
            </Switch>
        )
    };

    render() {
        return (
            <Provider {...stores}>
                <BrowserRouter>
                    {this.getRoutes()}
                </BrowserRouter>
            </Provider>
        );
    }
}

export default App;
