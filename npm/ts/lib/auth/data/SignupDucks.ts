import { createAction, handleActions, Action } from 'redux-actions';
import { takeLatest, call, put, fork } from 'redux-saga/effects';
import * as Backend from 'lib/core/Backend';
import {Request, RequestType} from 'lib/core/Api';

export interface ActionData<T> {
    onFail?: Function;
    onSuccess?: Function;
    data?: T;
};

export interface SignupForm {
    email: string;
    password: string;
}

export const Actions = {
    signup : createAction<ActionData<SignupForm>>(
        'SIGNUP_REQUEST'
    ),
}

//Saga
declare var jsRoutes: any;

export function* SignupSaga(): any {
    yield takeLatest(Actions.signup.toString(), handleSignupSubmit);
}

function* handleSignupSubmit(action: Action<ActionData<SignupForm>>) {
    const onSuccess = action.payload.onSuccess;
    const onFail = action.payload.onFail;
    
    try {
        const request:Request = {
            url: jsRoutes.controllers.security.SignUpController.submit().url, 
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
        onFail ? onFail('Please fill in all form fields') : console.log(error);
    }
}
