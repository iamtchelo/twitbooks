import React, { Component } from 'react';
import client from '../../api/client'
import { Route } from 'react-router-dom';

class DevSecureRoute extends Component {

    constructor(props) {
        super(props);
        this.state = {loading: true};
    }

    async componentDidMount(): void {
        await client.login();
        this.setState({ loading: false })
    }

    render() {
        const { component: Component, path } = this.props;
        const { loading } = this.state;
        return(
            <Route
                path={path}
                render={() => {
                    if (loading) {
                        return <h3 className="text-center">Validating session...</h3>
                    } else {
                        return <Component />
                    }
                }}
            />
        )
    }
}

export default DevSecureRoute;
