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
 * Class that defines parameters for building feed templates.
 * @author kevin.kang. Created on 2017. 3. 10..
 */
public class ListTemplate extends TemplateParams {
    private final String headerTitle;
    private final LinkObject headerLink;
    private final List<ContentObject> contents;

    private ListTemplate(final Builder builder) {
        super(builder.parentBuilder);
        this.objectType = MessageTemplateProtocol.TYPE_LIST;
        this.headerTitle = builder.headerTitle;
        this.headerLink = builder.headerLink;
        this.contents = builder.contents;
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject jsonObject = super.toJSONObject();
        jsonObject.put(MessageTemplateProtocol.HEADER_TITLE, headerTitle);
        if (headerLink != null)
            jsonObject.put(MessageTemplateProtocol.HEADER_LINK, headerLink.toJSONObject());
        if (contents != null) {
            JSONArray contentsArray = new JSONArray();
            for (ContentObject contentObject : contents) {
                contentsArray.put(contentObject.toJSONObject());
            }
            jsonObject.put(MessageTemplateProtocol.CONTENTS, contentsArray);
        }
        return jsonObject;
    }

    public static Builder newBuilder(final String headerTitle, final LinkObject headerLink) {
        return new Builder(headerTitle, headerLink);
    }

    public static class Builder extends AbstractTemplateParamsBuilder {
        private String headerTitle;
        private LinkObject headerLink;
        private List<ContentObject> contents;

        private TemplateParams.Builder parentBuilder;

        Builder(final String headerTitle, final LinkObject headerLink) {
            this.parentBuilder = new TemplateParams.Builder();
            this.headerTitle = headerTitle;
            this.headerLink = headerLink;
            this.contents = new ArrayList<ContentObject>();
        }

        public Builder addContent(final ContentObject contentObject) {
            if (contents.size() < 3) {
                contents.add(contentObject);
            }
            return this;
        }

        public ListTemplate build() {
            return new ListTemplate(this);
        }

        @Override
        public Builder setSocial(SocialObject socialObject) {
            parentBuilder.setSocial(socialObject);
            return this;
        }

        @Override
        public Builder addButton(ButtonObject buttonObject) {
            parentBuilder.addButton(buttonObject);
            return this;
        }
    }
}
