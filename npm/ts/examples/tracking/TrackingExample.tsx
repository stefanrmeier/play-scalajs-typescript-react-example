import * as React from 'react';
import {connect} from 'react-redux';
import { TrackingEvent, Actions as TrackingActions } from 'lib/core/tracking/TrackingDucks';

interface Props {
	loginSubmit: Function,
	track(event:TrackingEvent): Function
}

class TrackingExample extends React.Component<Props, any> {
	constructor(props:any) {
		super(props);
	}

	track = () => {
		const event:TrackingEvent = {
			eventName: "TRACKING_TEST"
		};
		this.props.track(event);
	}
	render() {
		return (
			<div>
                <a onClick={this.track}>Track me</a>
            </div>
		);
	}
}

const mapDispatchToProps = (dispatch:any) => {
	return {
		track :(event:TrackingEvent) => dispatch(TrackingActions.trackEvent(event))
	};
};

export default connect<any, any, any>(() =>{ return {}}, mapDispatchToProps)(TrackingExample);
