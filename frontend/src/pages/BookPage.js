import React, {Component} from "react";
import { observer, inject } from 'mobx-react';

@inject("stores")
@observer
class BookPage extends Component {

    componentDidMount(): void {
        console.log(this.props);
    }

    render() {
        return(
            <div>
                <p>OH HAI</p>
            </div>
        )
    }

}

export default BookPage;