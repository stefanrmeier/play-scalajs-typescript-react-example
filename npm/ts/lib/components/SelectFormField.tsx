import * as React from 'react';
import * as Select from 'react-select';

export class SelectFormField extends React.Component<any, any> {
    constructor(props: any) {
        super(props);
    }

    render() {
        const props = this.props;
        return (
            <Select
                {...props}
                valueKey='id' labelKey='label'
                value={props.input.value}
                onChange={(value: any) => props.input.onChange(value)}
                onBlur={() => props.input.onBlur(props.input.value)}
                options={this.props.options}
            />

        );
    }
}