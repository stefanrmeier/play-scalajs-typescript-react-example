import * as React from 'react';

interface Props {
    isOpenLeft: boolean;
    isOpenRight: boolean;
    rightContent:  React.ReactNode;
    leftContent: React.ReactNode;
    closeAll: VoidFunction;
}

export default class MobileNav extends React.Component<Props, {}> {
    constructor(props: any) {
        super(props);
    }

    render() {
        const props = this.props;

        return (
            <div>
                <div id="body-wrapper" className={(this.props.isOpenLeft ? 'open-left' : '') + (this.props.isOpenRight ? 'open-right' : '')} onClick={this.props.closeAll}>
                    <div className="overlay"></div>
                    {this.props.children}
                </div>
                <nav id="mobile-nav-left" className={"navmenu navmenu-default navmenu-fixed-left " + (this.props.isOpenLeft ? 'open-left' : '')} style={{ display: this.props.isOpenLeft ? 'block' : 'none' }}>
                    {this.props.leftContent}
                </nav>
                <nav id="mobile-nav-right" className={"navmenu navmenu-default navmenu-fixed-left " + (this.props.isOpenRight ? 'open-right' : '')} style={{ display: this.props.isOpenRight ? 'block' : 'none' }}>
                    {this.props.rightContent}
                </nav>
            </div>
        );
    }
}
