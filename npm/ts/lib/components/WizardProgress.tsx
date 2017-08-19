import * as React from 'react';

interface WizardProgressProps {
	steps: Array<any>,
	activeStepId: number
}

class WizardProgress extends React.Component<WizardProgressProps, any> {
	constructor(props:any) {
		super(props);
	}
	render() {
		const steps = this.props.steps.map(data => {
			return (<div key={data.id} className="wizard-step">
				<span className={data.id === this.props.activeStepId
					? 'circle wizard-step-current'
					: 'circle'}>{data.id}</span>
				<p>{data.text}</p>
			</div>);
		});
		return (
			<div className="wizard-progress-round">
				<div className="wizard-row">
					{steps}
				</div>
			</div>
		);
	}
}

// WizardProgress.propTypes = {
// 	activeStepId: React.PropTypes.number.isRequired,
// 	steps: React.PropTypes.array.isRequired
// };

export default WizardProgress;
