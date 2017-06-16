package com.kakao.auth;

import android.os.Build;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

/**
 * @author kevin.kang. Created on 2017. 5. 22..
 */

public class KakaoCookieManager {
    private CookieSyncManager cookieSyncManager;
    private CookieManager cookieManager;

    private static KakaoCookieManager instance;

    public static KakaoCookieManager getInstance() {
        if (instance == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                instance = new KakaoCookieManager(CookieManager.getInstance());
            } else {
                CookieSyncManager.createInstance(KakaoSDK.getAdapter().getApplicationConfig().getApplicationContext());
                instance = new KakaoCookieManager(CookieSyncManager.getInstance());
            }
        }
        return instance;
    }

    public KakaoCookieManager(final CookieSyncManager manager) {
        cookieSyncManager = manager;
    }

    public KakaoCookieManager(final CookieManager manager) {
        cookieManager = manager;
    }

    public void flush() {
        if (cookieManager != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.flush();
        } else if (cookieSyncManager != null) {
            cookieSyncManager.sync();
        }
    }
}
