import React, { Component } from 'react';
import createStores from "./stores/createStores";
import { Provider } from "mobx-react";
import { configure } from 'mobx';
import BookPage from "./pages/book/BookPage";
import { Switch, Route, BrowserRouter } from 'react-router-dom';
import MessagePage from "./pages/message/MessagePage";
import LoginPage from "./pages/login/LoginPage";
import SecuredRoute from "./components/route/SecuredRoute";
import auth0Client from './auth/Auth';

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
        try {
            await auth0Client.silentAuth();
            this.forceUpdate();
        } catch (err) {
            if (err.error !== 'login_required') console.log(err.error);
        }
        this.setState({checkingSession: false})
    }

    render() {
        return (
            <Provider {...stores}>
                <BrowserRouter>
                    <Switch>
                        <SecuredRoute path="/messages/:bookId" component={MessagePage}/>
                        <Route path="/login" component={LoginPage}/>
                        <SecuredRoute path="/" component={BookPage}/>
                    </Switch>
                </BrowserRouter>
            </Provider>
        );
    }
}

export default App;
