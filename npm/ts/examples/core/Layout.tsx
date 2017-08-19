import * as React from 'react';

class Layout extends React.Component<any, any> {
	constructor(props:any) {
		super(props);
	}
	
	render() {
		return (
			<div>
				<div className="header">Examples (coming soon)</div>
				<div className="container-fluid">
					{ this.props.children  }
				</div>
				<div className="footer"></div>
			</div>
		);
	}
}

export default Layout;
