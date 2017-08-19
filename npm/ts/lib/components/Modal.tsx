import * as React from 'react';

interface Props {
    isOpen: boolean;
    onClose: VoidFunction;
    onSave?: VoidFunction;
    title: string; 
    saveText?: string;
    hideFooter?: boolean;
}

export default class Modal extends React.Component<Props, {}> {
    constructor(props: any) {
        super(props);
    }

    render() {
        const props = this.props;

        const showFooter = this.props.hideFooter ? false : true;

        return (
            <div className="modal-open">
            <div className="modal fade in" style={{ display: this.props.isOpen ? 'block' : 'none' }}>
                <div className="modal-dialog modal-md" role="document" style={{zIndex: 1500}}>
                    <div className="modal-content">
                        <div className="modal-header">
                            <button type="button" className="close" data-dismiss="modal" onClick={this.props.onClose} aria-label="Close"><span aria-hidden="true">×</span></button>
                            <h4 className="modal-title">{this.props.title}</h4>
                        </div>
                        <div className="modal-body">
                        {this.props.children}
                        </div>
                        {showFooter && 
                        <div className="modal-footer">
                        <button type="button" className="btn btn-default" data-dismiss="modal" onClick={this.props.onClose}>Close</button>
                        <button type="button" className="btn btn-primary" onClick={this.props.onSave}>{this.props.saveText ? this.props.saveText : 'Save'}</button>
                        </div>
                        }
                    </div>
                </div>

                <div className="modal-backdrop fade in"></div>
            </div>
            </div>
        );
    }
}
