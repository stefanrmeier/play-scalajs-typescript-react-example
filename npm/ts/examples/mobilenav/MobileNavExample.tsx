import * as React from 'react';
import Spinner from 'lib/components/Spinner';
import Mobile from 'lib/components/MobileNav'
import {connect} from 'react-redux';

interface Props {
}

interface State {
    isOpenLeft: boolean;
    isOpenRight: boolean;
}

class SideNavExamle extends React.Component<Props, any> {
    constructor(props: any) {
        super(props);
        this.state = {
            isOpenLeft: false,
            isOpenRight: false
        }
    }
    componentWillMount() {   
    }

    leftMenuTrigger = () => {
        this.setState((prevState: State) => ({
            isOpenLeft: !prevState.isOpenLeft
        }));
    }

    rightMenuTrigger = () => {
        this.setState((prevState: State) => ({
            isOpenRight: !prevState.isOpenRight
        }));
    }

    closeAllMenus = () => {
        if (this.state.isOpenLeft || this.state.isOpenRight) {
            this.setState((prevState: State) => ({
                isOpenLeft: false,
                isOpenRight: false
            }));
        }
    }

    render() {
        return (
            <div>
                <Mobile 
                isOpenLeft={this.state.isOpenLeft} 
                isOpenRight={this.state.isOpenRight} 
                leftContent={<div>Menu content left</div>} 
                rightContent={<div>Menu content right</div>} 
                closeAll={this.closeAllMenus}>
                <nav className="navbar-fixed-top navbar">
                    <div className="header">
                        <div className="container-fluid page">
                            <div className="row">
                                <div className="col-sm-10 col-sm-offset-2">
                                    <div className="logo">example</div>
                                </div>
                                <div className="hidden-sm hidden-md hidden-lg">
                                    <div onClick={this.rightMenuTrigger} className="hamburger"><i className="fa fa-bars" aria-hidden="true"></i></div>
                                    <div onClick={this.leftMenuTrigger} className="hamburger"><i className="fa fa-bars" aria-hidden="true"></i></div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="container-fluid page">
                        <div className="row">
                            <div className="col-sm-10 col-sm-offset-2">
                                <ul className="nav nav-tabs">
                                    <li><a href="#" className="active">Menu 1</a></li>
                                    <li><a href="#">Menu 2</a></li>
                                    <li><a href="#">Menu 3</a></li>
                                    <li><a href="#">Menu 4</a></li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </nav>

                <div className="container-fluid content page">
                    

                    {/* ContentPane  */}
                    <div className="col-xs-12 col-sm-7 content-main">
                        <h3 className="pg-heading hidden-xs">Top topics</h3>
                        <div>Content</div>
                    </div>

                    {/* End ContentPane  */}

                    <div className="col-xs-12 navbar-bottom navbar-fixed-bottom hidden-sm hidden-md hidden-lg">
                        <div className="row">
                            <div className="col-xs-12 text-center">
                                Navbar bootom
                            </div>
                        </div>
                    </div>
                </div>
                <div className="footer"></div>
                </Mobile>
            </div>

        );
    }
}


export default SideNavExamle;
