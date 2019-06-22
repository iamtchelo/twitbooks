import React from 'react';
import { Route } from 'react-router-dom';
import auth0Client from '../../auth/Auth';
import LoginPage from "../../pages/login/LoginPage";

function SecuredRoute(props) {
    const { component: Component, path, checkingSession } = props;
    return (
        <Route path={path} render={() => {
            if (checkingSession) {
                return <h3 className="text-center">Validating session...</h3>
            }
            if (!auth0Client.isAuthenticated()) {
                return <LoginPage/>;
            }
            return <Component />
        }} />
    );
}

export default SecuredRoute;