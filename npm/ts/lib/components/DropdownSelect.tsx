import * as React from 'react';
import {Map, List} from 'immutable';

interface DropdownSelectProps {
	//  {
	//   'id': 1, <- Unique ID
	//   'text': 'Option1', <- Option text in the select
	//   'active': true <- selected or not
	//  }, {
	//   'id': 2,
	//   'text': 'Option2',
	//   'active': false
	//  }, {
	//   'id': 3,
	//   'text': 'Option3',
	//   'active': false
	//  }
	values: List<any>,
	save: Function,
	label: string
}
class DropdownSelect extends React.Component<DropdownSelectProps, any> {
	constructor(props: any) {
		super(props);
	}
	save = (event: any) => {
		const eventValue = event.target.value;
		const data = this.props.values.map((value) => {
			if (eventValue === value.get('text')) {
				return Map({'id': value.get('id'), 'text': value.get('text'), 'active': true});
			}
			return Map({'id': value.get('id'), 'text': value.get('text'), 'active': false});
		});
		this.props.save(data);
	}
	render() {
		const data = this.props.values.map((a) => {
			return <option key={a.get('id')}>{a.get('text')}</option>;
		});

		const activeElement = this.props.values.filter(d => d.get('active') === true);
		const selectedValueText = (activeElement.size > 0) ? activeElement.get(0).get('text') : '';

		return (
			<div className="form-group required">
				<label className="control-label">{this.props.label}</label>
				<select value={selectedValueText} className="form-control" onChange={this.save}>
					{data}
				</select>
			</div>
		);
	}
}

// DropdownSelect.propTypes = {
// 	label: React.PropTypes.string.isRequired,
// 	//  {
// 	//   'id': 1, <- Unique ID
// 	//   'text': 'Option1', <- Option text in the select
// 	//   'active': true <- selected or not
// 	//  }, {
// 	//   'id': 2,
// 	//   'text': 'Option2',
// 	//   'active': false
// 	//  }, {
// 	//   'id': 3,
// 	//   'text': 'Option3',
// 	//   'active': false
// 	//  }
// 	values: ImmutablePropTypes.list.isRequired,
// 	save: React.PropTypes.func.isRequired
// };

export default DropdownSelect;
