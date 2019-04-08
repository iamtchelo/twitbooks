import React, { Component } from 'react';
import createStores from "./stores/createStores";
import { Provider } from "mobx-react";
import { configure } from 'mobx';
import BookPage from "./pages/BookPage";
import "antd/dist/antd.css";
import { Switch, Route, BrowserRouter, Redirect } from 'react-router-dom';

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
                    <Route path="/books" component={BookPage}/>
                    <Redirect from="/" to="/books"/>
                </Switch>
            </BrowserRouter>
        </Provider>
    );
  }
}

export default App;
