import * as React from 'react';
import * as ReactMarkdown from 'react-markdown';

interface Props {
}

class Markdown extends React.Component<Props, any> {
	constructor(props: any) {
		super(props);
	}

	render() {
        const input = '# This is a header\n\nAnd this is a paragraph';
		return (
			<div>
                 <ReactMarkdown source={input} />
            </div>
		);
	}
}

export default Markdown;


