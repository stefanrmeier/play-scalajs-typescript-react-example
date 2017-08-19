import {Store, createStore, applyMiddleware} from 'redux';
import createSagaMiddleware from 'redux-saga'
import {fork} from 'redux-saga/effects';
import { combineReducers } from 'redux'
import { reducer as formReducer } from 'redux-form'
import {SecurityReducer} from 'lib/core/security/security-reducers'
import {CoreReducer} from 'lib/core/CoreDucks'
import { routerReducer } from 'react-router-redux'
import {TrackingSaga} from 'lib/core/tracking/TrackingDucks';

const reducer = combineReducers({
  security: SecurityReducer,
  core: CoreReducer,
  form: formReducer,
  routing: routerReducer
})

export function configureStore() : Store<any> {
    const sagaMiddleware = createSagaMiddleware();
    const createStoreWithMiddleware = applyMiddleware(sagaMiddleware)(createStore);
    const store = createStoreWithMiddleware(reducer, (<any>window).__REDUX_DEVTOOLS_EXTENSION__ && (<any>window).__REDUX_DEVTOOLS_EXTENSION__());

    function* rootSaga(): any {
        yield fork(TrackingSaga);
    }

    sagaMiddleware.run(rootSaga);

    return store;
}
