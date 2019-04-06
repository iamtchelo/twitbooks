import React, { Component } from 'react';
import createStores from "./stores/createStores";
import { Provider } from "mobx-react";
import { configure } from 'mobx';
import BookPage from "./pages/BookPage";
import "antd/dist/antd.css";
import DevTools from 'mobx-react-devtools';

configure({
    enforceActions: 'always'
});

const stores = createStores();

class App extends Component {
  render() {
    return (
        <div>
            <Provider {...stores}>
                <BookPage />
            </Provider>
            <DevTools />
        </div>
    );
  }
}

export default App;
