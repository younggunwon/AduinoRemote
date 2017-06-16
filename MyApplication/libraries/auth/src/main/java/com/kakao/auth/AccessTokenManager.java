package com.kakao.auth;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.kakao.auth.api.AuthApi;
import com.kakao.auth.authorization.accesstoken.AccessToken;
import com.kakao.auth.network.response.AccessTokenInfoResponse;
import com.kakao.network.tasks.KakaoTaskQueue;
import com.kakao.util.helper.CommonProtocol;
import com.kakao.util.helper.Utility;

import java.util.concurrent.Future;

/**
 * @author kevin.kang. Created on 2017. 5. 25..
 */

public interface AccessTokenManager {
    Future<AccessToken> requestAccessTokenByAuthCode(final String authCode, final AccessTokenCallback accessTokenCallback);
    Future<AccessToken> refreshAccessToken(final String refreshToken, final AccessTokenCallback accessTokenCallback);
    Future<AccessTokenInfoResponse> requestAccessTokenInfo(final ApiResponseCallback<AccessTokenInfoResponse> responseCallback);

    class Factory {
        private static AccessTokenManager accessTokenManager;

        static AccessTokenManager initialize(final Context context, final AuthApi authApi, final String appKey, final ApprovalType approvalType) {
            if (accessTokenManager == null) {
                String clientSecret = Utility.getMetadata(context, CommonProtocol.CLIENT_SECRET_PROPERTY);
                accessTokenManager = new KakaoAccessTokenManager(context, authApi, KakaoTaskQueue.getInstance(), appKey, clientSecret, approvalType);
            }
            return accessTokenManager;
        }

        public static AccessTokenManager getInstance() {
            return accessTokenManager;
        }
    }
}
