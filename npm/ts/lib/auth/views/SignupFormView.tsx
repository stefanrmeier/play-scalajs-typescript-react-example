import * as React from 'react';
import { Field, reduxForm } from 'redux-form';
import { Link } from 'react-router'

interface Props {
  onSubmit: any;
  handleSubmit?: any;
  pristine?: any;
  submitting?: any;
  error?: any;
}

class SignupFormView extends React.Component<Props, any> {
  constructor(props: any) {
    super(props);
  }

  render() {
    const { error, handleSubmit, pristine, submitting } = this.props
    return (
      <div>
        <div className="row">
          <div className="col-xs-12">
            <form onSubmit={handleSubmit}>
              {error && <strong style={{ color: 'red' }}>{error}</strong>}
              <div className="form-group">
                <label className="control-label sr-only" htmlFor="email">Email</label>
                <Field component="input" type="email" name="email" className="form-control form-control input-sm" placeholder="Email" />
              </div>
              <section>
                <div className="form-group ">
                  <label className="control-label sr-only" htmlFor="password">Password</label>
                  <Field component="input" type="password" name="password" className="form-control form-control input-sm" data-pwd="true" placeholder="Password" />
                </div>
              </section>
              <div className="form-group">
                <button type="submit" disabled={pristine || submitting} className="btn btn-sm btn-primary" style={{ width: '100%', textAlign: 'left' }}>Sign up</button>
              </div>
            </form>
          </div>
        </div>
      </div>
    );
  }
}

export default reduxForm({
  form: 'signup'
})<any>(SignupFormView);