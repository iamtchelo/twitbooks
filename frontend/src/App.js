import React, { Component } from 'react';
import logo from './logo.svg';
import './App.css';
import createStores from "./stores/createStores";
import { Provider } from "mobx-react";
import { configure } from 'mobx';

configure({
    enforceActions: true
});

const stores = createStores();

class App extends Component {
  render() {
    return (
        <Provider {...stores}>
          <div className="App">
            <header className="App-header">
              <img src={logo} className="App-logo" alt="logo" />
              <p>
                Edit <code>src/App.js</code> and save to reload.
              </p>
              <a
                  className="App-link"
                  href="https://reactjs.org"
                  target="_blank"
                  rel="noopener noreferrer"
              >
                Learn React
              </a>
            </header>
          </div>
        </Provider>
    );
  }
}

export default App;
