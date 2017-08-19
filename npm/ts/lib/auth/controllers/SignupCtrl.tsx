import * as React from 'react';
import {connect} from 'react-redux';
import { SubmissionError} from 'redux-form'
import SignupFormView from '../views/SignupFormView';
import { hashHistory } from 'react-router';
import {Actions, ActionData, SignupForm} from '../data/SignupDucks';
import AccountActivateView from '../views/AccountActivateView'

interface Props {
	submit(data:ActionData<SignupForm>): Function;
}


class SignupCtrl extends React.Component<Props, any> {
	constructor(props:any) {
		super(props);
	}

	handleSubmit = (values:any) => {
		const formData:SignupForm = values;
		const sagaPromise = new Promise((resolve, reject) => {
			const sagaForm: ActionData<SignupForm> = {
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
				hashHistory.push('/account/activate');

			}).catch((error:any) => {
				reject(new SubmissionError({ _error: error })); //trigger onSubmitFail on the form
			});
		});

		return formPromise;
	}
	render() {
		return (
			<SignupFormView onSubmit={this.handleSubmit}/>
		);
	}
}

const mapDispatchToProps = (dispatch:any) => {
	return {
		submit: (data:ActionData<SignupForm>) => dispatch(Actions.signup(data))
	};
};

export default connect<any, any, any>(() =>{ return {}}, mapDispatchToProps)(SignupCtrl);
