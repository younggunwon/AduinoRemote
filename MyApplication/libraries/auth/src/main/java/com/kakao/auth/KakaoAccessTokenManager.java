package com.kakao.auth;

import android.content.Context;

import com.kakao.auth.api.AuthApi;
import com.kakao.auth.authorization.accesstoken.AccessToken;
import com.kakao.auth.network.response.AccessTokenInfoResponse;
import com.kakao.network.tasks.KakaoResultTask;
import com.kakao.network.tasks.KakaoTaskQueue;
import com.kakao.util.helper.CommonProtocol;
import com.kakao.util.helper.Utility;

import java.util.concurrent.Future;

/**
 * Class for getting access token by either authroization code or refresh token.
 *
 * Comments for testing:
 *
 * Most of the interface methods do not have to be unit-tested because the logic heavily depends on
 * integrity of KakaoTaskQueue, KakaoResultTask, and AuthApi. Things that have to be tested
 * are whether resulting KakaoResultTask correctly invoke methods in AuthApi.
 *
 *
 * @author kevin.kang. Created on 2017. 5. 11..
 */

class KakaoAccessTokenManager implements AccessTokenManager {
    private Context context;
    private AuthApi authApi;
    private KakaoTaskQueue taskQueue;
    private String appKey;
    private String clientSecret;
    private ApprovalType approvalType;

    private static KakaoAccessTokenManager accessTokenManager;

    static KakaoAccessTokenManager initialize(final Context context, final AuthApi authApi, final String appKey, final ApprovalType approvalType) {
        if (accessTokenManager == null) {
            String clientSecret = Utility.getMetadata(context, CommonProtocol.CLIENT_SECRET_PROPERTY);
            accessTokenManager = new KakaoAccessTokenManager(context, authApi, KakaoTaskQueue.getInstance(), appKey, clientSecret, approvalType);
        }
        return accessTokenManager;
    }

//    public static KakaoAccessTokenManager getInstance() {
//        if (accessTokenManager == null) {
//            throw new IllegalStateException("Session is not initialized. Call KakaoSDK#init first.");
//        }
//        return accessTokenManager;
//    }

    public KakaoAccessTokenManager(final Context context, final AuthApi authApi, final KakaoTaskQueue taskQueue, final String appKey, final String clientSecret, final ApprovalType approvalType) {
        this.context = context;
        this.authApi = authApi;
        this.taskQueue = taskQueue;
        this.appKey = appKey;
        this.clientSecret = clientSecret;
        this.approvalType = approvalType;
    }

    /**
     * Returns Future instance containing AccessToken with authorization code.
     *
     * @param authCode Authorization code
     * @param accessTokenCallback Success/callback failure for access token
     * @return Futurre instance containing AccessToken
     */
    @Override
    public Future<AccessToken> requestAccessTokenByAuthCode(final String authCode, final AccessTokenCallback accessTokenCallback) {
        return taskQueue.addTask(getAccessTokenTask(authCode, accessTokenCallback));
    }

    /**
     * Returns Future instance containing AccessToken with refresh token.
     *
     * @param refreshToken Refresh token for refreshing access token
     * @param accessTokenCallback Success/callback failure for access token
     * @return Future instance containing AccessToken
     */
    @Override
    public synchronized Future<AccessToken> refreshAccessToken(final String refreshToken, final AccessTokenCallback accessTokenCallback) {
        return taskQueue.addTask(getRefreshTokenTask(refreshToken, accessTokenCallback));
    }

    @Override
    public Future<AccessTokenInfoResponse> requestAccessTokenInfo(final ApiResponseCallback<AccessTokenInfoResponse> responseCallback) {
        return taskQueue.addTask(getAccessTokenInfoResponseTask(responseCallback));
    }

    /*
        Below are package-private methods used for testing, mainly used for mocking KakaoResultTask object.
     */

    KakaoResultTask<AccessToken> getAccessTokenTask(final String authCode, final AccessTokenCallback callback) {
        return new KakaoResultTask<AccessToken>(callback) {
            @Override
            public AccessToken call() throws Exception {
                return authApi.requestAccessToken(context, appKey, authCode, null, clientSecret, approvalType.toString());
            }
        };
    }

    KakaoResultTask<AccessToken> getRefreshTokenTask(final String refreshToken, final AccessTokenCallback callback) {
        return new KakaoResultTask<AccessToken>(callback) {
            @Override
            public AccessToken call() throws Exception {
                return authApi.requestAccessToken(context, appKey, null, refreshToken, clientSecret, approvalType.toString());
            }
        };
    }

    KakaoResultTask<AccessTokenInfoResponse> getAccessTokenInfoResponseTask(final ApiResponseCallback<AccessTokenInfoResponse> responseCallback) {
        return new KakaoResultTask<AccessTokenInfoResponse>(responseCallback) {
            @Override
            public AccessTokenInfoResponse call() throws Exception {
                return authApi.requestAccessTokenInfo();
            }
        };
    }
}
