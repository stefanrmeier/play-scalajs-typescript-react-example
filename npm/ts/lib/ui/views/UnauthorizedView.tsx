import * as React from 'react';

class UnauthorizedView extends React.Component<any, any> {
	render() {
		return (
			<div>
				You are not authorized to view this page.
			</div>
		);
	}
}

export default UnauthorizedView;