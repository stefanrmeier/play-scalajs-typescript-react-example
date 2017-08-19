import { handleActions, Action } from 'redux-actions';
import {IStateSecurity} from './StateSecurity';

export const SecurityReducer = handleActions<any, any>({
	AUTHTOKEN_UPDATE: (state:IStateSecurity, action:Action<string>) => ({
		authToken: action.payload
	})
},
{authToken: ''}
);