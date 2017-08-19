import * as React from 'react';
import * as ReactDOM from 'react-dom';
import { Router, Route, hashHistory } from 'react-router';
import { syncHistoryWithStore } from 'react-router-redux'
import { Provider } from 'react-redux';
import { routes } from './core/routes'
import * as bootstrap from './core/bootstrap';

const store = bootstrap.configureStore()

const history = syncHistoryWithStore(hashHistory, store)

ReactDOM.render(
	<Provider store={store}>
		<Router history={history} routes={routes} />
	</Provider>, document.getElementById('app'));