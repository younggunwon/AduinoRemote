package com.kakao.auth;

import com.kakao.auth.authorization.accesstoken.AccessToken;
import com.kakao.auth.authorization.accesstoken.AccessTokenListener;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.network.exception.ResponseStatusError;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import java.net.HttpURLConnection;

/**
 * @author kevin.kang. Created on 2017. 4. 28..
 */

public abstract class AccessTokenCallback extends ResponseCallback<AccessToken> implements AccessTokenListener {
    @Override
    public final void onFailure(ErrorResult errorResult) {
        Exception exception = errorResult.getException();
        if (exception != null && exception instanceof ResponseStatusError) {
            ResponseStatusError e = (ResponseStatusError) exception;
            switch (e.getHttpStatusCode()) {
                case HttpURLConnection.HTTP_BAD_REQUEST:
                case HttpURLConnection.HTTP_UNAUTHORIZED:
                    exception = new KakaoException(KakaoException.ErrorType.AUTHORIZATION_FAILED, e.getErrorMsg());
                    break;
                default:
                    exception = new KakaoException(KakaoException.ErrorType.UNSPECIFIED_ERROR, e.getErrorMsg());
                    break;
            }
        }
        onAccessTokenFailure(new ErrorResult(exception));
    }

    @Override
    public final void onSuccess(AccessToken accessToken) {
        if (accessToken.hasValidAccessToken()) {
            onAccessTokenReceived(accessToken);
        } else {
            Exception exception = new KakaoException(KakaoException.ErrorType.AUTHORIZATION_FAILED, "the result of access token request is invalid access token.");
            onAccessTokenFailure(new ErrorResult(exception));
        }
    }
}
