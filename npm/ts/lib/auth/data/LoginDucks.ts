import { createAction, handleActions, Action } from 'redux-actions';
import { takeLatest, call, put, fork } from 'redux-saga/effects';
import * as Backend from 'lib/core/Backend';
import {Request, RequestType} from 'lib/core/Api';

export interface ActionData<T> {
    onFail?: Function;
    onSuccess?: Function;
    data?: T;
};

export interface LoginForm {
    email: string;
    password: string;
}

export const Actions = {
    login : createAction<ActionData<LoginForm>>(
        'LOGIN_REQUEST'
    ),
}

//Saga
declare var jsRoutes: any;

export function* LoginSaga(): any {
    yield takeLatest(Actions.login.toString(), handleLoginSubmit);
}

function* handleLoginSubmit(action: Action<ActionData<LoginForm>>) {
    const onSuccess = action.payload.onSuccess;
    const onFail = action.payload.onFail;
    
    try {
        const request:Request = {
            url: jsRoutes.controllers.security.SignInController.submit().url, 
            payload: action.payload.data,
            requestType: RequestType.POST
        };
        const res:Backend.JSend  = yield call(Backend.execute, request) as any;

        if (res.status === Backend.JSendStatus.SUCCESS) {
            onSuccess ? onSuccess() : () => {};
        }
        else {
            throw res.message;
        }
    } catch(error) {
        onFail ? onFail('Username or password incorrect. Try again.') : console.log(error);
    }
}
