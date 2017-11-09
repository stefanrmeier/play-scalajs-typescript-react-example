import * as React from 'react';
import Select from 'react-select';
import {List, Map} from 'immutable';

interface Props {
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

class AutocompleteSelect extends React.Component<Props, any> {
	constructor(props:any) {
		super(props);
	}
	save = (selectedValue: any) => {
		const data = this.props.values.map((value) => {
			if (selectedValue.value === value.get('id')) {
				return value.set('active', true);
			}
			return value.set('active', false);
		});
		this.props.save(data);
	}
	render() {
		const options = this.props.values.map((a) => {
			return Map({value: a.get('id'), label: a.get('text')});
		});

		const activeElement = this.props.values.filter(d => d.get('active') === true);
		const selectedValueId = (activeElement.size > 0) ? activeElement.get(0).get('id') : '';

		return (
			<div className="form-group required">
				<label className="control-label">{this.props.label}</label>
				<Select name="form-field-name" value={selectedValueId} options={options.toJS()} onChange={this.save} clearable={false}/>
			</div>
		);
	}
}

// AutocompleteSelect.propTypes = {
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

export default AutocompleteSelect;
