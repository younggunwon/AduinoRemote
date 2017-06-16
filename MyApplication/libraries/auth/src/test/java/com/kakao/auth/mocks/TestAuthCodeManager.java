package com.kakao.auth.mocks;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;

import com.kakao.auth.AuthCodeCallback;
import com.kakao.auth.AuthCodeManager;
import com.kakao.auth.AuthType;
import com.kakao.auth.helper.StartActivityWrapper;
import com.kakao.network.ErrorResult;
import com.kakao.util.exception.KakaoException;

import java.util.List;

/**
 * @author kevin.kang. Created on 2017. 5. 25..
 */

public class TestAuthCodeManager implements AuthCodeManager {
    private String authCode = "auth_code";
    @Override
    public void requestAuthCode(AuthType authType, Activity activity, AuthCodeCallback authCodeCallback) {
        authCodeCallback.onAuthCodeReceived(authCode);
    }

    @Override
    public void requestAuthCode(AuthType authType, Fragment fragment, AuthCodeCallback authCodeCallback) {
        authCodeCallback.onAuthCodeReceived(authCode);
    }

    @Override
    public void requestAuthCode(AuthType authType, android.support.v4.app.Fragment fragment, AuthCodeCallback authCodeCallback) {
        authCodeCallback.onAuthCodeReceived(authCode);
    }

    @Override
    public void requestAuthCodeWithScopes(AuthType authType, StartActivityWrapper wrapper, List<String> scopes, AuthCodeCallback authCodeCallback) {
        authCodeCallback.onAuthCodeReceived(authCode);
    }

    @Override
    public boolean handleActivityResult(int requestCode, int resultCode, Intent data) {
        return true;
    }
}
