import { createAction, handleActions, Action } from 'redux-actions';
import { takeLatest, call, put, fork } from 'redux-saga/effects';
import * as Backend from 'lib/core/Backend';
import {Request, RequestType} from 'lib/core/Api';

export interface ActionData<T> {
    onFail?: Function;
    onSuccess?: Function;
    data?: T;
};


export interface PasswordResetForm  {
    email: string;
}

export const Actions = {
    requestPassword : createAction<ActionData<PasswordResetForm>>(
        'PASSWORD_REQUEST'
    ),
}

//Saga
declare var jsRoutes: any;

export function* PasswordResetSaga(): any {
    yield takeLatest(Actions.requestPassword.toString(), handlePasswordForgetSubmit);
}

function* handlePasswordForgetSubmit(action: Action<ActionData<PasswordResetForm>>) {
    const onSuccess = action.payload.onSuccess;
    const onFail = action.payload.onFail;

    try {
       const request:Request = {
            url: jsRoutes.controllers.security.ForgotPasswordController.submit().url, 
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
        onFail ? onFail(error) : console.log(error);
    }
}
