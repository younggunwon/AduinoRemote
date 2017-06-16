package com.kakao.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.kakao.auth.authorization.AuthorizationResult;
import com.kakao.test.common.KakaoTestCase;
import com.kakao.util.helper.TalkProtocol;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowCookieManager;
import org.robolectric.shadows.ShadowLooper;

/**
 * This test class tests if KakaoAuthCodeManager correctly parses intent outputs from
 * KakaoTalk app or KaKaoWebViewActivity.
 *
 * @author kevin.kang. Created on 2017. 5. 23..
 */

public class AuthCodeParseIntentTest extends KakaoTestCase {
    private Context context;
    private KakaoCookieManager cookieManager;
    private String appKey = "sample_app_key";
    private String redirectUri = "kakao" + appKey + "://oauth";
    private String expectedAuthCode = "12345";
    private String authCodePostfix = "?code=" + expectedAuthCode;

    private String errorDescription = "error_description";

    private KakaoAuthCodeManager authCodeManager;

    @Before
    public void setup() {
        context = RuntimeEnvironment.application;
        cookieManager = new KakaoCookieManager(ShadowCookieManager.getInstance());

        ISessionConfig sessionConfig = new ISessionConfig() {
            @Override
            public AuthType[] getAuthTypes() {
                return new AuthType[]{AuthType.KAKAO_LOGIN_ALL};
            }

            @Override
            public boolean isUsingWebviewTimer() {
                return false;
            }

            @Override
            public boolean isSecureMode() {
                return false;
            }

            @Override
            public ApprovalType getApprovalType() {
                return ApprovalType.INDIVIDUAL;
            }

            @Override
            public boolean isSaveFormData() {
                return false;
            }
        };
        authCodeManager = Mockito.spy(new KakaoAuthCodeManager(context, new Handler(ShadowLooper.getMainLooper()), appKey, sessionConfig, cookieManager));
    }

    @Test
    public void testParseCancelIntent() {
        Intent intent = new Intent();
        AuthorizationResult result = authCodeManager.parseAuthCodeIntent(1, Activity.RESULT_CANCELED, intent);
        Assert.assertTrue(result.isCanceled());
    }

    @Test
    public void testParseSuccessIntent() {
        Intent intent = new Intent();
        intent.putExtra(TalkProtocol.EXTRA_REDIRECT_URL, redirectUri + authCodePostfix);

        AuthorizationResult result = authCodeManager.parseAuthCodeIntent(1, Activity.RESULT_OK, intent);

        Assert.assertTrue(result.isSuccess());
        Assert.assertEquals(redirectUri + authCodePostfix, result.getRedirectURL());
        Assert.assertEquals(redirectUri + authCodePostfix, result.getRedirectUri().toString());
        Assert.assertNull(result.getAccessToken());
    }

    @Test
    public void testNotSupprtErrorIntent() {
        Intent intent = createErrorIntent(TalkProtocol.NOT_SUPPORT_ERROR, null);
        AuthorizationResult result = authCodeManager.parseAuthCodeIntent(1, Activity.RESULT_OK, intent);

        Assert.assertTrue(result.isPass());
    }

    @Test
    public void testUnknownErrorIntent() {
        Intent intent = createErrorIntent(TalkProtocol.UNKNOWN_ERROR, errorDescription);

        AuthorizationResult result = authCodeManager.parseAuthCodeIntent(1, Activity.RESULT_OK, intent);

        Assert.assertTrue(result.isAuthError());
        Assert.assertTrue(result.getResultMessage().contains(errorDescription));
    }

    @Test
    public void testProtocolErrorIntent() {
        Intent intent = createErrorIntent(TalkProtocol.PROTOCOL_ERROR, errorDescription);
        AuthorizationResult result = authCodeManager.parseAuthCodeIntent(1, Activity.RESULT_OK, intent);

        Assert.assertTrue(result.isAuthError());
        Assert.assertTrue(result.getResultMessage().contains(errorDescription));
    }

    @Test
    public void testApplicationErrorIntent() {
        Intent intent = createErrorIntent(TalkProtocol.APPLICATION_ERROR, errorDescription);
        AuthorizationResult result = authCodeManager.parseAuthCodeIntent(1, Activity.RESULT_OK, intent);

        Assert.assertTrue(result.isAuthError());
        Assert.assertTrue(result.getResultMessage().contains(errorDescription));
    }

    @Test
    public void testAuthCodeErrorIntent() {
        Intent intent = createErrorIntent(TalkProtocol.AUTH_CODE_ERROR, errorDescription);
        AuthorizationResult result = authCodeManager.parseAuthCodeIntent(1, Activity.RESULT_OK, intent);

        Assert.assertTrue(result.isAuthError());
        Assert.assertTrue(result.getResultMessage().contains(errorDescription));
    }

    @Test
    public void testClientInfoError() {
        Intent intent = createErrorIntent(TalkProtocol.CLIENT_INFO_ERROR, errorDescription);
        AuthorizationResult result = authCodeManager.parseAuthCodeIntent(1, Activity.RESULT_OK, intent);

        Assert.assertTrue(result.isAuthError());
        Assert.assertTrue(result.getResultMessage().contains(errorDescription));
    }


    private Intent createErrorIntent(final String errorType, final String errorDescription) {
        Intent intent = new Intent();
        intent.putExtra(TalkProtocol.EXTRA_ERROR_TYPE, errorType);
        intent.putExtra(TalkProtocol.EXTRA_ERROR_DESCRIPTION, errorDescription);
        return intent;
    }
}
