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

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class that defines parameters for building feed templates.
 * @author kevin.kang. Created on 2017. 3. 10..
 */

public class FeedTemplate extends TemplateParams {
    private final ContentObject contentObject;
    FeedTemplate(Builder builder) {
        super(builder.parentBuilder);
        this.objectType = MessageTemplateProtocol.TYPE_FEED;
        this.contentObject = builder.contentObject;
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject jsonObject = super.toJSONObject();
        jsonObject.put(MessageTemplateProtocol.CONTENT, contentObject.toJSONObject());
        return jsonObject;
    }

    public static Builder newBuilder(final ContentObject contentObject) {
        return new Builder(contentObject);
    }

    public ContentObject getContentObject() {
        return contentObject;
    }

    public static class Builder extends AbstractTemplateParamsBuilder {
        private TemplateParams.Builder parentBuilder;
        protected ContentObject contentObject;

        public Builder(final ContentObject contentObject) {
            this.parentBuilder = new TemplateParams.Builder();
            this.contentObject = contentObject;
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

        public FeedTemplate build() {
            return new FeedTemplate(this);
        }
    }
}
