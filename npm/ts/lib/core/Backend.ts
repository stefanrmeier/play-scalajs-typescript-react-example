import { takeLatest, call, put, fork, select } from 'redux-saga/effects';
import { Action } from 'redux-actions';
import { hashHistory } from 'react-router';

import { Api, Request, RequestType } from './Api';
import { IStateSecurity } from './security/StateSecurity';
import { CoreActionCreators } from './CoreDucks';
import * as SecureActions from './security/security-actions';

export enum JSendStatus {
    SUCCESS,
    FAIL,
    ERROR
}

export interface JSend {
    status: JSendStatus;
    data?: any;
    message?: string;
    code?: string;
}

export function* execute(request: Request) {
    switch (request.requestType) {
        case RequestType.POST: {
            try {
                const req = yield call(goodHandBefore, request);
                const response = yield call(Api.post, req);
                const res: JSend = yield call(goodHandAfter, response);
                return res;
            }
            finally {
                yield put(CoreActionCreators.remote_fetching_done());
            }
        }
        case RequestType.ATTACH: {
            try {
                const req = yield call(goodHandBefore, request);
                const response = yield call(Api.attach, req);
                const res: JSend = yield call(goodHandAfter, response);
                return res;
            }
            finally {
                yield put(CoreActionCreators.remote_fetching_done());
            }
        }
        case RequestType.DELETE: {
            try {
                const req = yield call(goodHandBefore, request);
                const response = yield call(Api.delete, req);
                const res: JSend = yield call(goodHandAfter, response);
                return res;
            }
            finally {
                yield put(CoreActionCreators.remote_fetching_done());
            }
        }
        case RequestType.PUT: {
            try {
                const req = yield call(goodHandBefore, request);
                const response = yield call(Api.put, req);
                const res: JSend = yield call(goodHandAfter, response);
                return res;
            }
            finally {
                yield put(CoreActionCreators.remote_fetching_done());
            }
        }
        default: {
            try {
                const req = yield call(goodHandBefore, request);
                const response = yield call(Api.get, req);
                const res: JSend = yield call(goodHandAfter, response);
                return res;
            }
            finally {
                yield put(CoreActionCreators.remote_fetching_done());
            }
        }
    }
}

function* goodHandBefore<Request>(request: Request) {
    yield put(CoreActionCreators.remote_fetching_init());

    const authToken = yield call(getAuthToken)

    const securedRequest: Request = Object.assign(request, {
        authToken: authToken,
        csrfToken: 'this-is-a-great-nocheck-token',
    });

    return securedRequest;
}

function* goodHandAfter(response: any) {

    try {
        //if request is not authorized, auth cookie needs to be deletdd
        //in order to load public.js instead of secure.js
        switch (response.status) {
            case 401:
                deleteCookie('auth');
                window.location.href = '/';
                break;
            case 403:
                hashHistory.push('/unauthorized');
                break;
            case 404:
                hashHistory.push('/notfound');
                break;
            default:
                if (response.status !== 200) {
                    return <JSend>{
                        status: JSendStatus.ERROR,
                        message: response.body
                    };
                }

                break;
        }

        //if this header is provided, it means that the token has been updated
        const authToken: string = response.headers["x-auth-token"];

        if (authToken) {
            yield call(setAuthToken, authToken);
        }
        const res = response.text ? JSON.parse(response.text) : null;

        switch (res.status) {
            case 'success':
                return <JSend>{
                    status: JSendStatus.SUCCESS,
                    data: res.data ? res.data : null
                };
            case 'fail':
                return <JSend>{
                    status: JSendStatus.FAIL,
                    data: res.data ? res.data : null
                };
            case 'error':
                return <JSend>{
                    status: JSendStatus.ERROR,
                    message: res.message
                };
            default:
                return <JSend>{
                    status: JSendStatus.ERROR,
                    message: "Unexcepted response format."
                };
        }
    } catch (e) {
        return <JSend>{
            status: JSendStatus.ERROR,
            message: e
        };
    }

}

function* getAuthToken() {
    const authToken: string = yield select(getAuthtokenFromState);

    if (authToken) {
        return authToken;
    } else {
        const token = getCookie('auth');

        if (token) {
            setAuthToken(token);
        }
        return token;
    }
}
const getAuthtokenFromState = (state: any) => state.security.authToken;

function* setAuthToken(token: string) {
    setCookie('auth', token, 121);
    yield put(SecureActions.updateAuthToken(token));
}

function getCookie(name: string) {
    var v = document.cookie.match('(^|;) ?' + name + '=([^;]*)(;|$)');
    return v ? v[2] : null;
}

function setCookie(name: string, value: any, validityInMinutes?: number) {
    const d: Date = new Date();

    const expiryTime: number = validityInMinutes ? validityInMinutes : 60;

    d.setTime(d.getTime() + 60 * 1000 * expiryTime);

    document.cookie = name + "=" + value + ";path=/;expires=" + d.toUTCString();
}

function deleteCookie(name: string) { setCookie(name, '', -1); }