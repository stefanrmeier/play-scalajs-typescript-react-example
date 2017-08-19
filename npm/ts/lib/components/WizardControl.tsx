import * as React from 'react';

interface WizardControlProps {
	next?: VoidFunction,
	prev?: VoidFunction
	nextLabel?: string
	previousLabel?: string
}

class WizardControl extends React.Component<WizardControlProps, any> {
	constructor(props: any) {
		super(props);
	}
	render() {
		return (
			<div className="col-xs-12" style={{paddingLeft: 0, paddingRight: 0, marginTop: '15px'}}>
				<div className="col-xs-6" style={{
					textAlign: 'left'
				}}>
					{this.props.prev && <button onClick={this.props.prev} className="btn btn-primary nextBtn btn-sm" type="button">{this.props.previousLabel ? this.props.previousLabel : 'Previous Step'}</button>
					}
				</div>
				<div className="col-xs-6" style={{
					textAlign: 'right'
				}}>
					{this.props.next && <button onClick={this.props.next} className="btn btn-primary nextBtn btn-sm" type="button">{this.props.nextLabel ? this.props.nextLabel : 'Next Step'}</button>
					}
				</div>
			</div>
		);
	}
}

export default WizardControl;
