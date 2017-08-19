import * as React from 'react';
import { Field, reduxForm } from 'redux-form';
import { Link } from 'react-router'

interface Props {
  onSubmit: any;
  handleSubmit?: any;
  pristine?: any;
  submitting?: any;
}

class PasswordForgotView extends React.Component<Props, any> {
  constructor(props: any) {
    super(props);
  }

  render() {
    const { handleSubmit, pristine, submitting } = this.props
    return (
      <div className="row">
        <div className="col-xs-12">
          <form onSubmit={handleSubmit}>
            Please enter your email address and we will send you an email with further instructions to reset your password.
            <div className="form-group">
              <label className="control-label" htmlFor="email">Email</label>
              <Field component="input" type="email" name="email" className="form-control form-control input-sm" placeholder="(e.g. firstname.lastname@domain.com)" />
            </div>
            <div className="form-group">
              <div>
                <button type="submit" disabled={pristine || submitting} className="btn btn-sm btn-primary">Send</button>
              </div>
            </div>
          </form>
        </div>
      </div>
    );
  }
}

export default reduxForm({
  form: 'PasswordForgotForm'
})<any>(PasswordForgotView);