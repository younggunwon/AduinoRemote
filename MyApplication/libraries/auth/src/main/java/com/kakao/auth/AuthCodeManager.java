package com.kakao.auth;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.kakao.auth.helper.StartActivityWrapper;

import java.util.List;

/**
 * @author kevin.kang. Created on 2017. 5. 25..
 */

public interface AuthCodeManager {
    void requestAuthCode(final AuthType authType, final Activity activity, AuthCodeCallback authCodeCallback);
    void requestAuthCode(final AuthType authType, final Fragment fragment, AuthCodeCallback authCodeCallback);
    void requestAuthCode(final AuthType authType, final android.support.v4.app.Fragment fragment, AuthCodeCallback authCodeCallback);
    void requestAuthCodeWithScopes(final AuthType authType, final StartActivityWrapper wrapper, final List<String> scopes, AuthCodeCallback authCodeCallback);

    boolean handleActivityResult(int requestCode, int resultCode, Intent data);

    class Factory {
        private static AuthCodeManager authCodeManager;

        static AuthCodeManager initialize(final Context context, final String appKey, final ISessionConfig sessionConfig) {
            if (authCodeManager == null) {
                authCodeManager = new KakaoAuthCodeManager(context, new Handler(Looper.getMainLooper()), appKey, sessionConfig, KakaoCookieManager.getInstance());
            }
            return authCodeManager;
        }

        public static AuthCodeManager getInstance() {
            return authCodeManager;
        }
    }
}
