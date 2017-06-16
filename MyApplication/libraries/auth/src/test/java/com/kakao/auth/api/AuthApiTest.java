package com.kakao.auth.api;

import android.content.Context;
import android.os.Handler;

import com.kakao.auth.KakaoCookieManager;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowCookieManager;
import org.robolectric.shadows.ShadowLooper;

/**
 * @author kevin.kang. Created on 2017. 5. 19..
 */

public class AuthApiTest {
    private Context context;
    private AuthApi authApi;

    @Before
    public void setup() {
        context = RuntimeEnvironment.application;
        authApi = new AuthApi(new Handler(ShadowLooper.getMainLooper()), new KakaoCookieManager(ShadowCookieManager.getInstance()));
    }

    @Test
    public void testAuthApi() {
        authApi.synchronizeCookies(context);
    }

}
