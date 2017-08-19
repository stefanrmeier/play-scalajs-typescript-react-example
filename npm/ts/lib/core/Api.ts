import * as request from 'superagent';

export enum RequestType {
    GET,
    POST,
    PUT,
    DELETE,
    ATTACH
};

export interface Request {
    authToken?: string;
    url: string;
    contentType?: string;
    requestType: RequestType;
    payload?: any;
    csrfToken?: string;
};

export class Api {
    static get = (req:Request) => {
        return new Promise((resolve, reject) => {
            request
                .get(req.url)
                .set('Accept', 'application/json')
                .set('X-auth-token', req.authToken)
                .query(req.payload)
                .end((error:any, res:any) => {
                    error ? resolve(error) : resolve(res);
                });
        });
    }
    static attach = (req:Request) => {
        return new Promise((resolve, reject) => {
            const areq = request
                .post(req.url)
                .set('Accept', 'application/json')
                .set('Csrf-Token', req.csrfToken)
                .set('X-auth-token', req.authToken)
            req.payload.forEach((file: any) => {
                    areq.attach(file.name, file);
            });
            areq.end((error:any, res:any) => {
                    error ? resolve(error) : resolve(res);
            });
        });
    }
    static post = (req:Request) => {
        return new Promise((resolve, reject) => {
            request
                .post(req.url)
                .send(req.payload)
                .set('Accept', 'application/json')
                .set('Csrf-Token', req.csrfToken)
                .set('X-auth-token', req.authToken)
                .end((error:any, res:any) => {
                    error ? resolve(error) : resolve(res);
                });
        });
    }
    static delete = (req:Request) => {
        return new Promise((resolve, reject) => {
            request
                .delete(req.url)
                .send(req.payload)
                .set('Accept', 'application/json')
                .set('X-auth-token', req.authToken)
                .end((error:any, res:any) => {
                    error ? resolve(error) : resolve(res);
                });
        });
    }
    static put = (req:Request) => {
        return new Promise((resolve, reject) => {
            request
                .put(req.url)
                .send(req.payload)
                .set('Accept', 'application/json')
                .set('X-auth-token', req.authToken)
                .end((error:any, res:any) => {
                    error ? resolve(error) : resolve(res);
                });
        });
    }
}

