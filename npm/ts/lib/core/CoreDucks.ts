import { createAction, handleActions, Action } from 'redux-actions';

export interface ICoreState {
    fetchingCount: number;
    isFetching: boolean;
}


//Actions
export const CoreActionCreators = {
    remote_fetching_init : createAction<boolean>(
        'REMOTE_FETCHING_INIT'
    ),
    remote_fetching_done : createAction<boolean>(
        'REMOTE_FETCHING_DONE'
    )
}

//Reducer
export const CoreReducer = handleActions<any, any>({
	REMOTE_FETCHING_INIT: (state:ICoreState, action:Action<{}>) => {
        
        return { isFetching: true, fetchingCount: state.fetchingCount + 1 };
	},
    REMOTE_FETCHING_DONE: (state:ICoreState, action:Action<{}>) => {
        const fetchingCountNew = state.fetchingCount - 1;

        return { 
            isFetching: fetchingCountNew > 0, fetchingCount: fetchingCountNew
        };
	}
},
{isFetching: false, fetchingCount: 0}
);