import * as React from 'react';
import { Link } from 'react-router'

interface Props {
}

class PasswordEmailSentView extends React.Component<Props, any> {
  constructor(props: any) {
    super(props);
  }

  render() {
    return (
      <div className="row">
        <div className="col-xs-12">
          An email has been sent to the indicated email address. Please follow the instructions to reset your password.
           Go back to the <Link to="login">login</Link> page.
        </div>
      </div>
    );
  }
}

export default PasswordEmailSentView;