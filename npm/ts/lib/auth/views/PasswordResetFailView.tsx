import * as React from 'react';
import { Link } from 'react-router'

interface Props {
}

class PasswordResetFailView extends React.Component<Props, any> {
  constructor(props: any) {
    super(props);
  }

  render() {
    return (
      <div className="row">
        <div className="col-xs-12">
          You can login with your new password now.
          Go back to the <Link to="login">login</Link> page.
        </div>
      </div>
    );
  }
}

export default PasswordResetFailView;