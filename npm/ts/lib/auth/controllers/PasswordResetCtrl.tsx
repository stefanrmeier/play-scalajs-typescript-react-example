import * as React from 'react';
import {connect} from 'react-redux';
import { SubmissionError} from 'redux-form'
import { hashHistory } from 'react-router';
import PasswordForgotView from '../views/PasswordForgotView';
import PasswordEmailSentView from '../views/PasswordEmailSentView';
import {Actions, ActionData, PasswordResetForm} from '../data/PasswordResetDucks';

interface Props {
	submit(data:ActionData<PasswordResetForm>): Function,
}

class PasswordResetCtrl extends React.Component<Props, any> {
	constructor(props:any) {
		super(props);
	}

	forgotPasswordSubmit = (values:any) => {
		const formData:PasswordResetForm = values;
		//Promise for sage
		const sagaPromise = new Promise((resolve, reject) => {
			const sagaForm: ActionData<PasswordResetForm> = {
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
				hashHistory.push('/forgot/done');
			}).catch((error:any) => {
				reject(new SubmissionError({ _error: error })); //trigger onSubmitFail on the form
			});
		});

		return formPromise;
	}
	render() {

		return (
			<PasswordForgotView onSubmit={this.forgotPasswordSubmit}/>
		);
	}
}

const mapDispatchToProps = (dispatch:any) => {
	return {
		submit: (data:ActionData<PasswordResetForm>) => dispatch(Actions.requestPassword(data))
	};
};

export default connect<any, any, any>(() =>{ return {}}, mapDispatchToProps)(PasswordResetCtrl);
