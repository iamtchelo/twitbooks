import React, { Component } from 'react';
import createStores from "./stores/createStores";
import { Provider } from "mobx-react";
import { configure } from 'mobx';
import BookPage from "./pages/BookPage";
import "antd/dist/antd.css";

configure({
    enforceActions: 'always'
});

const stores = createStores();

class App extends Component {
  render() {
    return (
        <Provider {...stores}>
            <BookPage />
        </Provider>
    );
  }
}

export default App;
