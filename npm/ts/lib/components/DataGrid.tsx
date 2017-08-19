import * as React from 'react';
import * as ReactDataGrid from 'react-data-grid';
import * as ReactDataGridAddons from 'react-data-grid-addons';
import DataGridToolbar from './DataGridToolbar';

const Selectors = ReactDataGridAddons.Data.Selectors;

interface Props {
    colNames: any[];
    rows: any[];
    minHeight?: number;
    minWidth?: number;
    minColumnWidth?: number;
    uniqueColumnKey: string;
    toolbar?: any;
    enableRowSelect?: any;
}

interface State {
    rows: any[];
    filters: any;
    selectedRows: any
}

class DataGrid extends React.Component<Props, State> {
    constructor(props: any) {
        super(props);
    }

    componentWillMount() {
        this.setState({ rows: this.props.rows, selectedRows: [], filters: {} })
    }

    componentWillReceiveProps (nextProps: Props) {
        this.setState({ rows: nextProps.rows });
    }

    handleFilterChange = (filter: any) => {
        let newFilters = Object.assign({}, this.state.filters);
        if (filter.filterTerm) {
            newFilters[filter.column.key] = filter;
        } else {
            delete newFilters[filter.column.key];
        }
        this.setState({ filters: newFilters });
    }

    onClearFilters = () => {
        // all filters removed
        this.setState({ filters: {} });
    }

    handleGridSort = (sortColumn: any, sortDirection: any) => {
        const comparer = (a: any, b: any) => {
            if (sortDirection === 'ASC') {
                return (a[sortColumn] > b[sortColumn]) ? 1 : -1;
            } else if (sortDirection === 'DESC') {
                return (a[sortColumn] < b[sortColumn]) ? 1 : -1;
            } else {
                return 1;
            }
        }
        const rows = sortDirection === 'NONE' ? this.state.rows.slice(0) : this.state.rows.sort(comparer);
        this.setState({ rows: rows });
    }
    getRows = () => { return Selectors.getRows(this.state); }
    getSize = () => { return this.getRows().length; }
    rowGetter = (rowIdx: number) => {
        return this.getRows()[rowIdx];
    }

    getColumns(colNames: any[]) {
        const columns = colNames.map((x) => {
            return {
                key: x.id,
                name: x.label ? x.label : x.id,
                sortable: true,
                filterable: true,
                width: x.width ? x.width : 60,
                resizable: true
            };
        });
        return columns;
    }

    onRowSelect = (rows: any) => {
        this.setState({ selectedRows: rows });
    }

    wrapToolbar = (toolbar: any) => {
        return (React.cloneElement(toolbar, { allRows: this.state.rows, selectedRows: this.state.selectedRows}));
    }

    render() {
        return (
            <div>
            <ReactDataGrid
                onGridSort={this.handleGridSort}
                rowKey={this.props.uniqueColumnKey}
                columns={this.getColumns(this.props.colNames)}
                rowGetter={this.rowGetter}
                enableCellSelect={false}
                enableRowSelect={this.props.enableRowSelect}
                onRowSelect={this.onRowSelect}
                rowsCount={this.getSize()}
                minHeight={this.props.minHeight}
                minWidth={this.props.minWidth}
                minColumnWidth={this.props.minColumnWidth}
                toolbar={this.props.toolbar && this.wrapToolbar(this.props.toolbar)}
                onAddFilter={this.handleFilterChange}
                onClearFilters={this.onClearFilters} />
                <span>Total entries: {this.state.rows.length}</span>
            </div>
        );
    }
}

export default DataGrid;