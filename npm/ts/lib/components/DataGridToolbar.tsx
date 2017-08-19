import * as React from 'react';

interface Props {
    export?: Function;
    enableFilter: boolean;
    children?: any; //Use this to add other toolbar functionality

    //Injected from DataGrid via React.cloneElement
    allRows?: any;
    selectedRows?: any;

    //Injected from react-data-grid via React.cloneElement
    onToggleFilter?: VoidFunction;
    columns?: any;
    numberOfRows?: number;
}

interface State {

}

class DataGridToolbar extends React.Component<Props, State> {
    constructor(props: any) {
        super(props);
    }

    exportSelected = () => {
        this.props.export(this.props.selectedRows);
    }

    exportAll = () => {
        this.props.export(this.props.allRows);
    }

    renderToggleFilterButton = () => {
            return (<button type="button" className="btn" onClick={this.props.onToggleFilter}>
                Show filters
            </button>);
    }

    renderExportAllButton = (exportAll: VoidFunction) => {
            return (<button type="button" className="btn" onClick={exportAll}>
                Download all
            </button>);
    }

    renderExportSelectedButton = (exportSelected: VoidFunction) => {
            return (<button type="button" disabled={this.props.selectedRows.length === 0} className="btn" onClick={exportSelected}>
                Download selected {this.props.selectedRows.length > 0 &&
                 ( "(" + this.props.selectedRows.length + ")" )}
            </button>);
    }

    render() {
        return (
            <div className="react-grid-Toolbar">
                <div className="tools" style={{float: 'left'}}>
                    {this.props.enableFilter && this.renderToggleFilterButton()}
                    {this.props.export && this.renderExportAllButton(this.exportAll)}
                    {this.props.export && this.renderExportSelectedButton(this.exportSelected)}
                    {this.props.children}
                </div>
            </div>
        );
    }
}

export default DataGridToolbar;
