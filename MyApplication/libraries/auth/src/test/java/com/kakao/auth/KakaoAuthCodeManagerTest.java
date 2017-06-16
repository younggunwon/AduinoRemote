package com.kakao.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

import com.kakao.auth.authorization.AuthorizationResult;
import com.kakao.auth.authorization.authcode.AuthCodeRequest;
import com.kakao.network.ErrorResult;
import com.kakao.test.common.KakaoTestCase;
import com.kakao.util.helper.log.Logger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowCookieManager;
import org.robolectric.shadows.ShadowLooper;

import java.util.ArrayList;
import java.util.List;


/**
 * @author kevin.kang. Created on 2017. 5. 19..
 */

public class KakaoAuthCodeManagerTest extends KakaoTestCase {

    // below fields are values used by KakaoAuthCodeManager
    private String appKey = "sample_app_key";
    private String redirectUri = "kakao" + appKey + "://oauth";
    private String wrongRedirectUri = "kakao" + appKey + "2" + "://oauth";
    private String expectedAuthCode = "12345";
    private String authCodePostfix = "?code=" + expectedAuthCode;

    private Context context;
    private Activity activity;

    private KakaoCookieManager cookieManager;

    private KakaoAuthCodeManager authCodeManager;
    private AuthCodeCallback authCodeCallback;

    // below fields are for testing whether callbacks are correctly called
    private List<String> events;
    private final String SUCCESS = "success";
    private final String FAILURE = "failure";

    @Before
    public void setup() {
        super.setup();
        context = RuntimeEnvironment.application;
        cookieManager = new KakaoCookieManager(ShadowCookieManager.getInstance());
        activity = Mockito.spy(Robolectric.setupActivity(Activity.class));
        activity = Robolectric.setupActivity(Activity.class);

        events = new ArrayList<String>();

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
        Mockito.doReturn(true).when(authCodeManager).checkInternetPermission();
    }

    @Test
    public void testOnReceivedResult() {

    }

    @Test
    public void testLoginAllWithTalk() {
        mockNativeLogin(AuthCodeRequest.Command.LOGGED_IN_TALK);
        Assert.assertTrue(events.isEmpty());
        authCodeManager.requestAuthCode(AuthType.KAKAO_LOGIN_ALL, activity, authCodeCallback);
        Assert.assertTrue(events.contains(SUCCESS));
    }

    @Test
    public void testLoginAllWithKakakoTalkCancel() {
        mockNativeLoginCancel(AuthCodeRequest.Command.LOGGED_IN_TALK);
        Assert.assertTrue(events.isEmpty());
        authCodeManager.requestAuthCode(AuthType.KAKAO_LOGIN_ALL, activity, authCodeCallback);
        Assert.assertTrue(events.contains(FAILURE));
    }

    @Test
    public void testLoginAllWithStory() {
        mockNativeLogin(AuthCodeRequest.Command.LOGGED_IN_STORY);
        Assert.assertTrue(events.isEmpty());
        authCodeManager.requestAuthCode(AuthType.KAKAO_LOGIN_ALL, activity, authCodeCallback);
        Assert.assertTrue(events.contains(SUCCESS));
    }

    @Test
    public void testLoginAllWithAccount() {
        mockAccountLogin();
        Assert.assertTrue(events.isEmpty());
        authCodeManager.requestAuthCode(AuthType.KAKAO_LOGIN_ALL, activity, authCodeCallback);
        Assert.assertTrue(events.contains(SUCCESS));
    }


    @Test
    public void testKakaoTalk() {
        mockNativeLogin(AuthCodeRequest.Command.LOGGED_IN_TALK);
        Assert.assertTrue(events.isEmpty());
        authCodeManager.requestAuthCode(AuthType.KAKAO_TALK, activity, authCodeCallback);
        Assert.assertTrue(events.contains(SUCCESS));
    }

    @Test
    public void testKakaoStory() {
        mockNativeLogin(AuthCodeRequest.Command.LOGGED_IN_STORY);
        Assert.assertTrue(events.isEmpty());
        authCodeManager.requestAuthCode(AuthType.KAKAO_STORY, activity, authCodeCallback);
        Assert.assertTrue(events.contains(SUCCESS));
    }

    @Test
    public void testKakaoAccount() {
        mockAccountLogin();
        Assert.assertTrue(events.isEmpty());
        authCodeManager.requestAuthCode(AuthType.KAKAO_ACCOUNT, activity, authCodeCallback);
        Assert.assertTrue(events.contains(SUCCESS));
    }

    @Test
    public void testKakaoTalkMultipleTimes() {
        mockNativeLogin(AuthCodeRequest.Command.LOGGED_IN_TALK);
        Assert.assertTrue(events.isEmpty());
        authCodeManager.requestAuthCode(AuthType.KAKAO_TALK, activity, authCodeCallback);
        Assert.assertTrue(events.contains(SUCCESS));

        events.clear();

        mockNativeLogin(AuthCodeRequest.Command.LOGGED_IN_TALK);
        Assert.assertTrue(events.isEmpty());
        authCodeManager.requestAuthCode(AuthType.KAKAO_TALK, activity, authCodeCallback);
        Assert.assertTrue(events.contains(SUCCESS));
    }

    @Test
    public void testKakaoStoryMultipleTimes() {
        mockNativeLogin(AuthCodeRequest.Command.LOGGED_IN_STORY);
        Assert.assertTrue(events.isEmpty());
        authCodeManager.requestAuthCode(AuthType.KAKAO_STORY, activity, authCodeCallback);
        Assert.assertTrue(events.contains(SUCCESS));

        events.clear();

        mockNativeLogin(AuthCodeRequest.Command.LOGGED_IN_STORY);
        Assert.assertTrue(events.isEmpty());
        authCodeManager.requestAuthCode(AuthType.KAKAO_STORY, activity, authCodeCallback);
        Assert.assertTrue(events.contains(SUCCESS));
    }

    @Test
    public void testKakaoAccountMultipleTimes() {
        mockAccountLogin();
        Assert.assertTrue(events.isEmpty());
        authCodeManager.requestAuthCode(AuthType.KAKAO_ACCOUNT, activity, authCodeCallback);
        Assert.assertTrue(events.contains(SUCCESS));

        events.clear();

        mockAccountLogin();
        Assert.assertTrue(events.isEmpty());
        authCodeManager.requestAuthCode(AuthType.KAKAO_ACCOUNT, activity, authCodeCallback);
        Assert.assertTrue(events.contains(SUCCESS));
    }

    @Test
    public void testKakaoAccountWithWrongRedirectUri() {
        mockAccountLoginWithWrongRedirectUri();
        Assert.assertTrue(events.isEmpty());
        authCodeManager.requestAuthCode(AuthType.KAKAO_ACCOUNT, activity, authCodeCallback);
        Assert.assertTrue(events.contains(FAILURE));
    }

    @Test
    public void testKakaoAccountWithEmptyAuthCode() {
        mockAccountLoginWithEmptyAuthCode();
        Assert.assertTrue(events.isEmpty());
        authCodeManager.requestAuthCode(AuthType.KAKAO_ACCOUNT, activity, authCodeCallback);
        Assert.assertTrue(events.contains(FAILURE));
    }

    @Test
    public void testKakaoAccountWithEmptyRedirectUriAndAuthCode() {
        mockEmptyRedirectUriAndAuthCode();
        Assert.assertTrue(events.isEmpty());
        authCodeManager.requestAuthCode(AuthType.KAKAO_ACCOUNT, activity, authCodeCallback);
        Assert.assertTrue(events.contains(FAILURE));
    }

    /**
     * Test NPE does not occur even if callback is not provided.
     */
    @Test
    public void testKakaoTalkWithoutCallback() {
        mockNativeLogin(AuthCodeRequest.Command.LOGGED_IN_TALK);
        Assert.assertTrue(events.isEmpty());
        authCodeManager.requestAuthCode(AuthType.KAKAO_TALK, activity, authCodeCallback);
    }

    AuthCodeCallback getAuthCodeCallback() {
        AuthCodeCallback authCodeCallback = new AuthCodeCallback() {
            @Override
            public void onAuthCodeReceived(String authCode) {
                Assert.assertEquals(expectedAuthCode, authCode);
                events.add(SUCCESS);
            }

            @Override
            public void onAuthCodeFailure(ErrorResult errorResult) {
                events.add(FAILURE);
            }
        };
        return authCodeCallback;
    }

    private void mockNativeLogin(final AuthCodeRequest.Command command) {
        final Integer requestCode = authCodeManager.getCurrentRequestCode();
        final Intent intent = new Intent();

        // This mocks handleActivityResult() from third-party app.
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                authCodeManager.handleActivityResult(requestCode, Activity.RESULT_OK, intent);
                return null;
            }
        }).when(authCodeManager).startActivityForResult(Matchers.any(Intent.class), Matchers.any(Integer.class));

        // This mocks intent from KakaoTalk. Intents are to be tested inside parseAuthCodeIntent
        Mockito.doReturn(AuthorizationResult.createSuccessAuthCodeResult(redirectUri + "?code=12345")).when(authCodeManager).parseAuthCodeIntent(requestCode, Activity.RESULT_OK, intent);

        authCodeCallback = getAuthCodeCallback();

        // Below three lines mock getIntent() methods of AuthCodeRequest to return kakaotalk intent
        AuthCodeRequest authCodeRequest = Mockito.spy(new AuthCodeRequest(context, appKey, redirectUri, requestCode, authCodeCallback));
        Mockito.doReturn(new Intent()).when(authCodeRequest).getIntent(command);
        Mockito.doReturn(authCodeRequest).when(authCodeManager).createAuthCodeRequest(context, appKey, authCodeCallback);
    }

    private void mockNativeLoginCancel(final AuthCodeRequest.Command command) {
        final Integer requestCode = authCodeManager.getCurrentRequestCode();
        final Intent intent = new Intent();

        // This mocks handleActivityResult() from third-party app.
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                authCodeManager.handleActivityResult(requestCode, Activity.RESULT_OK, intent);
                return null;
            }
        }).when(authCodeManager).startActivityForResult(Matchers.any(Intent.class), Matchers.any(Integer.class));

        // This mocks intent from KakaoTalk. Intents are to be tested inside parseAuthCodeIntent
        Mockito.doReturn(AuthorizationResult.createAuthCodeCancelResult("Pressed back button during requesting auth code.")).when(authCodeManager).parseAuthCodeIntent(requestCode, Activity.RESULT_OK, intent);

        authCodeCallback = getAuthCodeCallback();

        // Below three lines mock getIntent() methods of AuthCodeRequest to return kakaotalk intent
        AuthCodeRequest authCodeRequest = Mockito.spy(new AuthCodeRequest(context, appKey, redirectUri, requestCode, authCodeCallback));
        Mockito.doReturn(new Intent()).when(authCodeRequest).getIntent(command);
        Mockito.doReturn(authCodeRequest).when(authCodeManager).createAuthCodeRequest(context, appKey, authCodeCallback);
    }

    private void mockAccountLogin() {
        final Integer requestCode = authCodeManager.getCurrentRequestCode();
        authCodeCallback = getAuthCodeCallback();
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                authCodeManager.onWebViewCompleted(authCodeManager.getAuthCodeRequest(requestCode), redirectUri + authCodePostfix, null);
                return null;
            }
        }).when(authCodeManager).startActivity(Matchers.any(Intent.class));
    }

    private void mockAccountLoginWithWrongRedirectUri() {
        final Integer requestCode = authCodeManager.getCurrentRequestCode();
        authCodeCallback = getAuthCodeCallback();
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                authCodeManager.onWebViewCompleted(authCodeManager.getAuthCodeRequest(requestCode), wrongRedirectUri + authCodePostfix, null);
                return null;
            }
        }).when(authCodeManager).startActivity(Matchers.any(Intent.class));
    }

    private void mockAccountLoginWithEmptyAuthCode() {
        final Integer requestCode = authCodeManager.getCurrentRequestCode();
        authCodeCallback = getAuthCodeCallback();
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                authCodeManager.onWebViewCompleted(authCodeManager.getAuthCodeRequest(requestCode), redirectUri, null);
                return null;
            }
        }).when(authCodeManager).startActivity(Matchers.any(Intent.class));
    }

    private void mockEmptyRedirectUriAndAuthCode() {
        final Integer requestCode = authCodeManager.getCurrentRequestCode();
        authCodeCallback = getAuthCodeCallback();
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                authCodeManager.onWebViewCompleted(authCodeManager.getAuthCodeRequest(requestCode), null, null);
                return null;
            }
        }).when(authCodeManager).startActivity(Matchers.any(Intent.class));
    }
}
