import React, { Component } from 'react';
import createStores from "./stores/createStores";
import { Provider } from "mobx-react";
import { configure } from 'mobx';
import BookPage from "./pages/BookPage";

// configure({
//     enforceActions: 'always'
// });

// const stores = createStores();

class App extends Component {
  render() {
    return (
        <Provider bookStore={createStores().bookStore}>
            <BookPage />
        </Provider>
    );
  }
}

export default App;
