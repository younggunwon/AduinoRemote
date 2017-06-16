package com.kakao.auth.authorization.authcode;

import com.kakao.network.ErrorResult;

/**
 * @author kevin.kang. Created on 2017. 4. 28..
 */

public interface AuthCodeListener {
    void onAuthCodeReceived(final String authCode);
    void onAuthCodeFailure(final ErrorResult errorResult);
}
