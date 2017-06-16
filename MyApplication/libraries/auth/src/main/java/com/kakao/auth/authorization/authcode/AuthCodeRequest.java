/**
 * Copyright 2014-2016 Kakao Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kakao.auth.authorization.authcode;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.kakao.auth.ApprovalType;
import com.kakao.auth.AuthCodeCallback;
import com.kakao.auth.StringSet;
import com.kakao.auth.network.request.AuthRequest;
import com.kakao.util.helper.StoryProtocol;
import com.kakao.util.helper.TalkProtocol;
import com.kakao.util.helper.log.Logger;

/**
 * @author leoshin, created at 15. 7. 13..
 */
public class AuthCodeRequest extends AuthRequest {
    public enum Command {
        /**
         * / talk에 login되어 있는 계정이 있는 경우
         */
        LOGGED_IN_TALK,

        /**
         * story에 login되어 있는 계정이 있는 경우
         */
        LOGGED_IN_STORY,

        /**
         * talk이 install되어 있지 않는 경우
         */
        WEBVIEW_AUTH
    }

    final private Context context;
    final private Integer requestCode;
    final private AuthCodeCallback callback;

    public AuthCodeRequest(Context context, String appKey, String redirectURI, final Integer requestCode, final AuthCodeCallback callback) {
        super(appKey, redirectURI);
        this.context = context;
        this.requestCode = requestCode;
        this.callback = callback;
    }

    @Override
    public int getRequestCode() {
        return requestCode;
    }

    public AuthCodeCallback getCallback() {
        return callback;
    }

    /**
     *
     * @param command Command enum describing method for login
     * @return Intent to start KakaoTalk or KakaoStory for login, null for kakao account login
     */
    public Intent getIntent(Command command) {
        Logger.d("++ Auth code request : " + command.toString());
        Intent intent = null;
        switch (command) {
            case LOGGED_IN_TALK:
                intent = TalkProtocol.createLoggedInActivityIntent(context, getAppKey(), getRedirectURI(), getExtraParams(), needProjectLogin(getExtraParams()));
                break;
            case LOGGED_IN_STORY:
                intent = StoryProtocol.createLoggedInActivityIntent(context, getAppKey(), getRedirectURI(), getExtraParams(), needProjectLogin(getExtraParams()));
                break;
            case WEBVIEW_AUTH:
                break;
        }

        return intent;
    }

    public static boolean needProjectLogin(final Bundle extras){
        return extras != null && extras.containsKey(StringSet.approval_type) && extras.getString(StringSet.approval_type).equals(ApprovalType.PROJECT.toString());
    }

    public boolean needsInternetPermission() {
        return true;
    }
}
