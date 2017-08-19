import * as React from 'react';
import {connect} from 'react-redux';
import LoginFormView from '../views/LoginFormView';
import {Actions, LoginForm, ActionData} from '../data/LoginDucks';
import { TrackingEvent, Actions as TrackingActions } from 'lib/core/tracking/TrackingDucks';
import { SubmissionError} from 'redux-form'

interface Props {
	route: {redirectURL: string};
	submit? (data:ActionData<LoginForm>): Function;
	track?(event:TrackingEvent) : Function;
}

class LoginCtrl extends React.Component<Props, any> {
	constructor(props:any) {
		super(props);
	}

	componentWillMount() {
		const event:TrackingEvent = {
			eventName: 'LOGIN_INIT'
		}
		this.props.track(event);
	}

	handleSubmit = (values:any) => {
		const formData:LoginForm = values;
		const sagaPromise = new Promise((resolve, reject) => {
			const sagaForm: ActionData<LoginForm> = {
				data: formData,
				onSuccess: resolve,
				onFail: reject
			}
			this.props.submit(sagaForm);
		});
		//Promise for redux-form
		const formPromise = new Promise((resolve, reject) => {
			sagaPromise.then(() => {
				resolve(); //trigger onSubmitSuccess on the form
				//After successful login, completely reload the page
				window.location.href = this.props.route.redirectURL;
			}).catch((error:any) => {
				reject(new SubmissionError({ _error: error })); //trigger onSubmitFail on the form
			});
		});

		return formPromise;
	}
	render() {
		return (
			<LoginFormView onSubmit={this.handleSubmit}/>
		);
	}
}

const mapDispatchToProps = (dispatch:any) => {
	return {
		submit: (data:ActionData<LoginForm>) => dispatch(Actions.login(data)),
		track: (event:TrackingEvent) => dispatch(TrackingActions.trackEvent(event))
	};
};

export default connect<{},{}, Props>(() =>{ return {}}, mapDispatchToProps)(LoginCtrl);