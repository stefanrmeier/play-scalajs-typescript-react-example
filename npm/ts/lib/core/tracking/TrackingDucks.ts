 import {takeEvery, call, put, fork} from 'redux-saga/effects';
import {Action} from 'redux-actions';

import * as Backend from '../Backend';
import { createAction } from 'redux-actions';
import {Api, Request, RequestType} from '../Api';

export interface TrackingEvent {
    eventName: string;
}

export const Actions = {
    trackEvent : createAction<TrackingEvent>(
    'TRACK_EVENT'
    )
}

//Saga
export function* TrackingSaga(): any {
    yield takeEvery(Actions.trackEvent.toString(), handleTrack);
}

declare var jsRoutes: any;

function* handleTrack(action: Action<TrackingEvent>) {
    try {
        const event:TrackingEvent = action.payload;
        const request:Request = {
            url: jsRoutes.controllers.TrackingController.track().url, 
            payload: event,
            requestType: RequestType.POST
        };

        const res:Backend.JSend = yield call(Backend.execute, request) as any;

        if (res.status === Backend.JSendStatus.SUCCESS) {
            // const jobs:Array<Job> = res.data;

            // const actionData:ActionData<Array<Job>> = {
            //     data: jobs
            // }
            // yield put (InternalActions.update_internal(actionData));
            // onSuccess ? onSuccess(jobs) : () => {};
        }
        else {
            throw res.message;
        }

    } catch(error) {
        // onFail ? onFail(error) : console.log(error);
    }
}