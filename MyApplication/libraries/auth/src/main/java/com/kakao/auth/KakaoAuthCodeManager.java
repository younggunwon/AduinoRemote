/*
  Copyright 2015-2017 Kakao Corp.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */
package com.kakao.auth;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.SparseArray;

import com.kakao.auth.authorization.authcode.AuthCodeRequest;
import com.kakao.auth.authorization.authcode.AuthorizationCode;
import com.kakao.auth.authorization.authcode.KakaoWebViewActivity;
import com.kakao.auth.helper.StartActivityWrapper;
import com.kakao.auth.authorization.AuthorizationResult;
import com.kakao.auth.authorization.authcode.AuthCodeRequest.Command;
import com.kakao.network.ErrorResult;
import com.kakao.network.ServerProtocol;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.KakaoServiceProtocol;
import com.kakao.util.helper.TalkProtocol;
import com.kakao.util.helper.Utility;
import com.kakao.util.helper.log.Logger;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * @author leo.shin
 */
class KakaoAuthCodeManager implements AuthCodeManager {
    public static Integer authRequestCode = 0;

    private Context context;
    private final Handler handler;
    final private Queue<Command> commandQueue = new LinkedList<Command>();
    private SparseArray<AuthCodeRequest> requestMap;
    private StartActivityWrapper startActivityWrapper;
    private String appKey;
    private ISessionConfig sessionConfig;
    private KakaoCookieManager cookieManager;
    private transient boolean hasInternetPermission;

    @Override
    public void requestAuthCode(AuthType authType, Activity activity, AuthCodeCallback authCodeCallback) {
        requestAuthCode(authType, new StartActivityWrapper(activity), authCodeCallback);
    }

    @Override
    public void requestAuthCode(AuthType authType, Fragment fragment, AuthCodeCallback authCodeCallback) {
        requestAuthCode(authType, new StartActivityWrapper(fragment), authCodeCallback);
    }

    @Override
    public void requestAuthCode(AuthType authType, android.support.v4.app.Fragment fragment, AuthCodeCallback authCodeCallback) {
        requestAuthCode(authType, new StartActivityWrapper(fragment), authCodeCallback);
    }

    @Override
    public void requestAuthCodeWithScopes(AuthType authType, StartActivityWrapper wrapper, List<String> scopes, AuthCodeCallback authCodeCallback) {
        String refreshToken = getRefreshToken();
        if (refreshToken == null) {
            if (authCodeCallback != null) {
                authCodeCallback.onAuthCodeFailure(new ErrorResult(new KakaoException(KakaoException.ErrorType.ILLEGAL_STATE, "User should be logged in in order to use dynamic scope update.")));
            }
        } else {
            requestAuthCodeWithScopes(authType, wrapper, refreshToken, scopes, authCodeCallback);
        }
    }

    KakaoAuthCodeManager(final Context context, final Handler handler, final String appKey, final ISessionConfig sessionConfig, final KakaoCookieManager cookieManager) {
        this.context = context;
        this.handler = handler;
        this.appKey = appKey;
        this.sessionConfig = sessionConfig;
        this.requestMap = new SparseArray<AuthCodeRequest>();
        this.cookieManager = cookieManager;
    }


    void requestAuthCode(final AuthType authType, final StartActivityWrapper wrapper, AuthCodeCallback callback) {
        AuthCodeRequest authCodeRequest = createAuthCodeRequest(context, appKey, callback);
        start(authType, authCodeRequest, wrapper);
    }

    void requestAuthCodeWithScopes(final AuthType authType, final StartActivityWrapper wrapper, final String refreshToken, final List<String> scopes, final AuthCodeCallback authCodeCallback) {
        AuthCodeRequest request = createAuthCodeRequest(context, appKey, refreshToken, scopes, authCodeCallback);
        start(authType, request, wrapper);
    }

    void onAuthCodeReceived(final AuthCodeRequest authCodeRequest, final AuthorizationResult result) {
        AuthorizationCode authCode = null;
        KakaoException exception = null;
        final String resultRedirectURL = result.getRedirectURL();

        // 기대 했던 redirect uri 일치
        if (resultRedirectURL != null && resultRedirectURL.startsWith(authCodeRequest.getRedirectURI())) {
            authCode = AuthorizationCode.createFromRedirectedUri(result.getRedirectUri());
            // authorization code가 포함되지 않음
            if (!authCode.hasAuthorizationCode()) {
                authCode = null;
                exception = new KakaoException(KakaoException.ErrorType.AUTHORIZATION_FAILED, "the result of authorization code request does not have authorization code.");
            }
        } else { // 기대 했던 redirect uri 불일치
            exception = new KakaoException(KakaoException.ErrorType.AUTHORIZATION_FAILED, "the result of authorization code request mismatched the registered redirect uri. msg = " + result.getResultMessage());
        }
        AuthCodeCallback callback = authCodeRequest.getCallback();
        if (callback == null) {
            return;
        }
        if (exception == null) {
            callback.onAuthCodeReceived(authCode.getAuthorizationCode());
        } else {
            callback.onAuthCodeFailure(new ErrorResult(exception));
        }
    }

    void onAuthCodeFailure(final AuthCodeRequest authCodeRequest, AuthorizationResult result) {
        KakaoException exception = null;
        if (result == null) {
            exception = new KakaoException(KakaoException.ErrorType.AUTHORIZATION_FAILED, "the result of authorization code request is null.");
        } else if (result.isCanceled()) {
            exception = new KakaoException(KakaoException.ErrorType.CANCELED_OPERATION, result.getResultMessage());
        } else {
            exception = new KakaoException(KakaoException.ErrorType.AUTHORIZATION_FAILED, result.getResultMessage());
        }
        AuthCodeCallback callback = authCodeRequest.getCallback();
        if (callback == null) {
            return;
        }
        callback.onAuthCodeFailure(new ErrorResult(exception));
    }

    private void addToCommandQueue(final AuthType authType) {
        commandQueue.clear();
        AuthType type = authType == null ? AuthType.KAKAO_TALK : authType;
        switch (type) {
            case KAKAO_TALK:
                commandQueue.add(Command.LOGGED_IN_TALK);
                break;
            case KAKAO_STORY:
                commandQueue.add(Command.LOGGED_IN_STORY);
                break;
            case KAKAO_TALK_EXCLUDE_NATIVE_LOGIN:
                commandQueue.add(Command.LOGGED_IN_TALK);
                break;
            case KAKAO_LOGIN_ALL:
                commandQueue.add(Command.LOGGED_IN_TALK);
                commandQueue.add(Command.LOGGED_IN_STORY);
                break;
        }
        commandQueue.add(Command.WEBVIEW_AUTH);
    }

    void start(final AuthCodeRequest authCodeRequest) {
        Command command;
        while ((command = commandQueue.poll()) != null) {
            if (authCodeRequest.needsInternetPermission() && !checkInternetPermission()) {
                continue;
            }

            if (request(authCodeRequest, command)) {
                return;
            }
        }

        // handler를 끝까지 돌았는데도 authorization code를 얻지 못했으면 error
        onAuthCodeFailure(authCodeRequest, AuthorizationResult.createAuthCodeOAuthErrorResult("Failed to get Authorization Code."));
}

    void start(final AuthType authType, final AuthCodeRequest authCodeRequest, final StartActivityWrapper startActivityWrapper) {
        this.startActivityWrapper = startActivityWrapper;
        requestMap.put(authCodeRequest.getRequestCode(), authCodeRequest);
        addToCommandQueue(authType);
        start(authCodeRequest);
    }

    void startActivityForResult(final Intent intent, final int requestCode) {
        if (startActivityWrapper != null) {
            startActivityWrapper.startActivityForResult(intent, requestCode);
        }
    }

    void startActivity(final Intent intent) {
        if (startActivityWrapper != null) {
            startActivityWrapper.startActivity(intent);
        }
    }

    public boolean handleActivityResult(final int requestCode, final int resultCode, final Intent data) {
        AuthCodeRequest authCodeRequest = requestMap.get(requestCode);
        if (authCodeRequest == null) {
            Logger.w("KakaoAuthCodeManager lost track of current login attempt. Please retry.");
            return false;
        }
        AuthorizationResult result = parseAuthCodeIntent(requestCode, resultCode, data);
        if(result == null || result.isAuthError() || result.isError() || result.isCanceled()) {
            onAuthCodeFailure(authCodeRequest, result);
        } else if (result.isPass()) {
            start(authCodeRequest);
        } else if (result.isSuccess()) {
            onAuthCodeReceived(authCodeRequest, result);
        }
        return true;
    }

    /**
     * This method parses intent directly delivered from KakaoTalk.
     * @param requestCode requestCode used to start KakaoTalkActivity with startActivityForResult()
     * @param resultCode resultCode received from KakaoTalk
     * @param data Intent containing authorization resulit (Authorization code)
     * @return AuthorizationResult describing success or failure of getting authorization code
     */
    AuthorizationResult parseAuthCodeIntent(final int requestCode, final int resultCode, final Intent data) {
        AuthorizationResult outcome;

        if (data == null || resultCode == Activity.RESULT_CANCELED) {
            // This happens if the user presses 'Back'.
            outcome = AuthorizationResult.createAuthCodeCancelResult("pressed back button or cancel button during requesting auth code.");
        } else if (KakaoServiceProtocol.isCapriProtocolMatched(data)) {
            outcome = AuthorizationResult.createAuthCodeOAuthErrorResult("TalkProtocol is mismatched during requesting auth code through KakaoTalk.");
        } else if (resultCode != Activity.RESULT_OK) {
            outcome = AuthorizationResult.createAuthCodeOAuthErrorResult("got unexpected resultCode during requesting auth code. code=" + requestCode);
        } else {
            Bundle extras = data.getExtras();
            String errorType = extras.getString(TalkProtocol.EXTRA_ERROR_TYPE);
            String rediretURL = extras.getString(TalkProtocol.EXTRA_REDIRECT_URL);
            if (errorType == null && rediretURL != null) {
                return AuthorizationResult.createSuccessAuthCodeResult(rediretURL);
            } else {
                if(errorType != null && errorType.equals(TalkProtocol.NOT_SUPPORT_ERROR)) {
                    return AuthorizationResult.createAuthCodePassResult();
                }
                String errorDes = extras.getString(TalkProtocol.EXTRA_ERROR_DESCRIPTION);
                return AuthorizationResult.createAuthCodeOAuthErrorResult("redirectURL=" + rediretURL + ", " + errorType + " : " + errorDes);
            }
        }
        return outcome;
    }

    private boolean request(AuthCodeRequest authCodeRequest, Command command) {
        boolean result = true;
        if (command != Command.WEBVIEW_AUTH) {
            Intent intent = authCodeRequest.getIntent(command);
            if (intent == null) {
                return false;
            }

            try {
                startActivityForResult(intent, authCodeRequest.getRequestCode());
            } catch (ActivityNotFoundException e) {
                Logger.e(e);
                return false;
            }
        } else {
            result = requestWebviewAuth(authCodeRequest);
        }

        return result;
    }

    private boolean requestWebviewAuth(final AuthCodeRequest authCodeRequest) {
        try {
            final Bundle parameters = new Bundle();
            parameters.putString(StringSet.client_id, authCodeRequest.getAppKey());
            parameters.putString(StringSet.redirect_uri, authCodeRequest.getRedirectURI());
            parameters.putString(StringSet.response_type, StringSet.code);

            final Bundle extraParams = authCodeRequest.getExtraParams();
            if(extraParams != null && !extraParams.isEmpty()){
                for(String key : extraParams.keySet()){
                    String value = extraParams.getString(key);
                    if(value != null){
                        parameters.putString(key, value);
                    }
                }
            }

            cookieManager.flush();

            Uri uri = Utility.buildUri(ServerProtocol.AUTH_AUTHORITY, ServerProtocol.AUTHORIZE_CODE_PATH, parameters);

            Intent intent = KakaoWebViewActivity.newIntent(startActivityWrapper.getContext());
            intent.putExtra(KakaoWebViewActivity.KEY_URL, uri.toString());
            intent.putExtra(KakaoWebViewActivity.KEY_EXTRA_HEADERS, authCodeRequest.getExtraHeaders());
            intent.putExtra(KakaoWebViewActivity.KEY_USE_WEBVIEW_TIMERS, sessionConfig.isUsingWebviewTimer());
            intent.putExtra(KakaoWebViewActivity.KEY_RESULT_RECEIVER, getResultReceiver(authCodeRequest));
            startActivity(intent);

        } catch (Throwable t) {
            Logger.e("WebViewAuthHandler is failed", t);
            return false;
        }
        return true;
    }

    void onWebViewCompleted(final AuthCodeRequest authCodeRequest, final String redirectURL, KakaoException exception) {
        commandQueue.clear();
        requestMap.delete(authCodeRequest.getRequestCode());
        AuthorizationResult result;
        if (redirectURL != null) {
            Uri redirectedUri = Uri.parse(redirectURL);
            final String code = redirectedUri.getQueryParameter(StringSet.code);
            if (!TextUtils.isEmpty(code)) {
                result = AuthorizationResult.createSuccessAuthCodeResult(redirectURL);
                onAuthCodeReceived(authCodeRequest, result);
                return;
            } else {
                String error = redirectedUri.getQueryParameter(StringSet.error);
                String errorDescription = redirectedUri.getQueryParameter(StringSet.error_description);
                if (error != null && error.equalsIgnoreCase(StringSet.access_denied)) {
                    result = AuthorizationResult.createAuthCodeCancelResult("pressed back button or cancel button during requesting auth code.");
                } else {
                    result = AuthorizationResult.createAuthCodeOAuthErrorResult(errorDescription);
                }
            }
        } else {
            if (exception == null) {
                result = AuthorizationResult.createAuthCodeOAuthErrorResult("Failed to get Authorization Code.");
            } else if (exception.isCancledOperation()) {
                result = AuthorizationResult.createAuthCodeCancelResult(exception.getMessage());
            } else {
                result = AuthorizationResult.createAuthCodeOAuthErrorResult(exception.getMessage());
            }
        }
        onAuthCodeFailure(authCodeRequest, result);
    }

    /**
     * This is the callback called by KakaoWebViewActivity when kakao account login succeeds.
     * @param authCodeRequest
     * @param resultCode
     * @param resultData
     */
    void onReceivedResult(final AuthCodeRequest authCodeRequest, int resultCode, Bundle resultData) {
        String redirectUrl = null;
        KakaoException kakaoException = null;
        switch (resultCode) {
            case KakaoWebViewActivity.RESULT_SUCCESS:
                redirectUrl =  resultData.getString(KakaoWebViewActivity.KEY_REDIRECT_URL);
                break;
            case KakaoWebViewActivity.RESULT_ERROR:
                kakaoException = (KakaoException) resultData.getSerializable(KakaoWebViewActivity.KEY_EXCEPTION);
                break;
        }
        onWebViewCompleted(authCodeRequest, redirectUrl, kakaoException);
    }

    boolean checkInternetPermission() {
        if (hasInternetPermission) {
            return true;
        }

        if (!Utility.isUsablePermission(context, Manifest.permission.INTERNET)) {
            onAuthCodeFailure(null, AuthorizationResult.createAuthCodeOAuthErrorResult("This Operation needs INTERNET permission."));
            return false;
        } else {
            hasInternetPermission = true;
            return true;
        }
    }

    Integer getCurrentRequestCode() {
        return authRequestCode;
    }

    AuthCodeRequest createAuthCodeRequest(final Context context, final String appKey, final AuthCodeCallback callback) {
        AuthCodeRequest request = new AuthCodeRequest(context, appKey, StringSet.REDIRECT_URL_PREFIX + appKey + StringSet.REDIRECT_URL_POSTFIX, authRequestCode++, callback);
        request.putExtraParam(StringSet.approval_type, sessionConfig.getApprovalType() == null ? ApprovalType.INDIVIDUAL.toString() : sessionConfig.getApprovalType().toString());
        return request;
    }

    AuthCodeRequest createAuthCodeRequest(final Context context, final String appKey, final String refreshToken, final List<String> scopes, final AuthCodeCallback callback) {
        AuthCodeRequest request = new AuthCodeRequest(context, appKey, StringSet.REDIRECT_URL_PREFIX + appKey + StringSet.REDIRECT_URL_POSTFIX, authRequestCode++, callback);
        request.putExtraHeader(StringSet.RT, refreshToken);
        request.putExtraParam(StringSet.scope, getScopesString(scopes));
        request.putExtraParam(StringSet.approval_type, sessionConfig.getApprovalType() == null ? ApprovalType.INDIVIDUAL.toString() : sessionConfig.getApprovalType().toString());
        return request;

    }

    AuthCodeRequest getAuthCodeRequest(final Integer requestCode) {
        return requestMap.get(requestCode);
    }

    ResultReceiver getResultReceiver(final AuthCodeRequest authCodeRequest) {
        ResultReceiver resultReceiver = new ResultReceiver(handler) {
            @Override
            protected void onReceiveResult(int resultCode, Bundle resultData) {
                KakaoAuthCodeManager.this.onReceivedResult(authCodeRequest, resultCode, resultData);
            }
        };
        return resultReceiver;
    }

    String getRefreshToken() {
        try {
            return Session.getCurrentSession().getTokenInfo().getRefreshToken();
        } catch (IllegalStateException|NullPointerException e) {
            return null;
        }
    }

    String getScopesString(final List<String> requiredScopes) {
        String scopeParam = null;
        if (requiredScopes == null) {
            return null;
        }
        StringBuilder builder = null;
        for (String scope : requiredScopes) {
            if (builder != null) {
                builder.append(",");
            } else {
                builder = new StringBuilder("");
            }

            builder.append(scope);
        }

        if (builder != null) {
            scopeParam = builder.toString();
        }

        return scopeParam;
    }
}
