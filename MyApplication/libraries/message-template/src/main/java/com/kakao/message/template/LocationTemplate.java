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
 * Class that defines parameters for building location templates.
 * @author kevin.kang. Created on 2017. 3. 13..
 */

public class LocationTemplate extends FeedTemplate {
    private final String address;
    private final String addressTitle;

    LocationTemplate(Builder builder) {
        super(builder.parentBuilder);
        this.objectType = MessageTemplateProtocol.TYPE_LOCATION;
        this.address = builder.address;
        this.addressTitle = builder.addressTitle;
    }

    public JSONObject toJSONObject() throws JSONException {
        JSONObject jsonObject = super.toJSONObject();
        jsonObject.put(MessageTemplateProtocol.ADDRESS, address);
        jsonObject.put(MessageTemplateProtocol.ADDRESS_TITLE, addressTitle);
        return jsonObject;
    }

    public static FeedTemplate.Builder newBuilder(final ContentObject contentObject) {
        throw new UnsupportedOperationException("LocationTemplate does not support this method.");
    }

    public static Builder newBuilder(final String address, final ContentObject contentObject) {
        return new Builder(address, contentObject);
    }

    public static class Builder extends AbstractTemplateParamsBuilder {
        private FeedTemplate.Builder parentBuilder;
        private String address;
        private String addressTitle;

        Builder(final String address, final ContentObject contentObject) {
            parentBuilder = new FeedTemplate.Builder(contentObject);
            this.address = address;
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

        public Builder setAddressTitle(final String addressTitle) {
            this.addressTitle = addressTitle;
            return this;
        }

        public LocationTemplate build() {
            return new LocationTemplate(this);
        }
    }
}
