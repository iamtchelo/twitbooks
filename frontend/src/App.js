import React, { Component } from 'react';
import createStores from "./stores/createStores";
import { Provider } from "mobx-react";
import { configure } from 'mobx';
import BookPage from "./pages/book/BookPage";
import { Switch, Route, BrowserRouter, Redirect } from 'react-router-dom';
import MessagePage from "./pages/message/MessagePage";
import LoginPage from "./pages/login/LoginPage";

configure({
    enforceActions: 'always'
});

const stores = createStores();

class App extends Component {
  render() {
    return (
        <Provider {...stores}>
            <BrowserRouter>
                <Switch>
                    <Route path="/messages/:bookId" component={MessagePage}/>
                    <Route path="/login" component={LoginPage}/>
                    <Route path="/books" component={BookPage}/>
                    <Redirect from="/" to="/books"/>
                </Switch>
            </BrowserRouter>
        </Provider>
    );
  }
}

export default App;
