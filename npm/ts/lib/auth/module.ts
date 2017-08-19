import { fork } from 'redux-saga/effects';

import SignupCtrl from './controllers/SignupCtrl';
import LoginCtrl from './controllers/LoginCtrl'
import PasswordResetCtrl from './controllers/PasswordResetCtrl';

import PasswordEmailSentView from './views/PasswordEmailSentView';
import PasswordResetDoneView from './views/PasswordResetDoneView';
import PasswordResetFailView from './views/PasswordResetFailView';
import AccountActivateView from './views/AccountActivateView';
import SignupDoneView from './views/SignupDoneView';

import { LoginSaga } from './data/LoginDucks'
import { LogoutSaga } from './data/LogoutDucks'
import { PasswordResetSaga } from './data/PasswordResetDucks'
import { SignupSaga } from './data/SignupDucks'

export function* AuthSaga(): any {
    yield fork(LoginSaga);
    yield fork(LogoutSaga);
    yield fork(PasswordResetSaga);
    yield fork(SignupSaga);
}

export {
    SignupCtrl,
    LoginCtrl,
    PasswordResetCtrl,
    PasswordEmailSentView,
    PasswordResetDoneView,
    PasswordResetFailView,
    AccountActivateView,
    SignupDoneView
};

export const routes  = [
      { path: '/signup', component: SignupCtrl},
      { path: '/login', component: LoginCtrl, redirectURL: '/'},
      { path: '/forgot', component: PasswordResetCtrl },
      { path: 'account/activate', component: AccountActivateView },
      { path: 'signup/done', component: SignupDoneView },
      { path: 'forgot/done', component: PasswordEmailSentView },
      { path: 'reset/done', component: PasswordResetDoneView },
      { path: 'reset/fail', component: PasswordResetFailView },
]




