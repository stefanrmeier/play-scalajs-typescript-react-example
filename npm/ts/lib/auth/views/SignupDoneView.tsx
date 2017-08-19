import * as React from 'react';
import { Link } from 'react-router'

interface Props {
}

class SignupDoneView extends React.Component<Props, any> {
  constructor(props: any) {
    super(props);
  }

  render() {
    return (
      <div className="row">
        <div className="col-xs-12">
          Your account has been created successfully.
          Go back to the <Link to="login">login page</Link> page.
        </div>
      </div>
    );
  }
}

export default SignupDoneView;