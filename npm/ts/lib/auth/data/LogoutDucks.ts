import {takeLatest, call, put, fork} from 'redux-saga/effects';
import {Action} from 'redux-actions';
import { createAction } from 'redux-actions';
import * as Backend from 'lib/core/Backend';
import {Request, RequestType} from 'lib/core/Api';

export interface ActionData<T> {
    onFail?: Function;
    onSuccess?: Function;
    data?: T;
};

export const Actions = {
    logoutRequest : createAction<ActionData<null>>(
        'LOGOUT_REQUEST'
    ),
}

export function* LogoutSaga(): any {
    yield takeLatest(Actions.logoutRequest.toString(), logoutRequest);
}

declare var jsRoutes: any;

function* logoutRequest(action: Action<ActionData<null>>) {
    const onSuccess = action.payload.onSuccess;
    const onFail = action.payload.onFail;
    
    try {
        const request:Request = {
            url: jsRoutes.controllers.ApplicationController.signOut().url,
            requestType: RequestType.GET
        };
        
        const res:Backend.JSend = yield call(Backend.execute, request) as any;
        if (res.status === Backend.JSendStatus.SUCCESS) {
            onSuccess ? onSuccess() : () => {};
        }
        else {
            throw res.message;
        }
    } catch(error) {
        onFail ? onFail(error) : console.log(error);
    }
}