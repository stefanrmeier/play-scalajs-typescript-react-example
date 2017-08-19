import * as React from 'react';
import Dropzone = require('react-dropzone')

interface UploadProps {
	fileUpload(files:any): void
}

class Upload extends React.Component<UploadProps, any> {
	constructor(props: any) {
		super(props);
	}
	onDrop = (files: any) => {
		this.props.fileUpload(files);
	}
	render() {
		return (
			<div>
				<Dropzone onDrop={this.onDrop}>
					<div>Try dropping some files here, or click to select files to upload.</div>
				</Dropzone>
			</div>
		);
	}
}

export default Upload;
