/*
  Copyright 2017 Kakao Corp.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */
package com.kakao.message.template;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kevin.kang. Created on 2017. 3. 10..
 */

public class TemplateParams {
    public String getObjectType() {
        return objectType;
    }

    public SocialObject getSocial() {
        return social;
    }

    public List<ButtonObject> getButtons() {
        return buttons;
    }

    protected String objectType;

    private final SocialObject social;
    private final List<ButtonObject> buttons;


    TemplateParams(final Builder builder) {
        this.social = builder.social;
        this.buttons = builder.buttons;
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject json = new JSONObject();
        json.put(MessageTemplateProtocol.OBJ_TYPE, objectType);
        if (social != null)
            json.put(MessageTemplateProtocol.SOCIAL, social.toJSONObject());
        if (buttons != null) {
            JSONArray buttonArray = new JSONArray();
            for (ButtonObject button : buttons) {
                buttonArray.put(button.toJSONObject());
            }
            json.put(MessageTemplateProtocol.BUTTONS, buttonArray);
        }
        return json;
    }

    @Override
    public String toString() {
        try {
            return toJSONObject().toString();
        } catch (JSONException e) {
            return null;
        }
    }

    public static class Builder extends AbstractTemplateParamsBuilder {
        SocialObject social;
        List<ButtonObject> buttons = new ArrayList<ButtonObject>();
        public Builder setSocial(final SocialObject socialObject) {
            this.social = socialObject;
            return this;
        }

        public Builder addButton(final ButtonObject buttonObject) {
            buttons.add(buttonObject);
            return this;
        }
    }
}
