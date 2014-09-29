/**
 * Copyright 2014 Unicon (R)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ltistarter.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LtiProxyConfig {

    private String userId;
    private String contextId;

    private String url;
    private String consumerKey;
    private String consumerSecret;

    public LtiProxyConfig(
            final String userId,
            final String contextId) {
        this.userId = userId;
        this.contextId = contextId;
   }

    public LtiProxyConfig() {
    }

    @JsonCreator
    public LtiProxyConfig(
            @JsonProperty(value="url") final String url,
            @JsonProperty(value="consumerKey") final String consumerKey,
            @JsonProperty(value="consumerSecret") final String consumerSecret) {
        this.url = url;
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
    }

    public LtiProxyConfig(
            final String userId,
            final String contextId,
            final String url,
            final String consumerKey,
            final String consumerSecret) {
        this.userId = userId;
        this.contextId = contextId;
        this.url = url;
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
    }

    public String getUserId() {
        return this.userId;
    }

    public String getContextId() {
        return contextId;
    }

    public String getUrl() {
        return url;
    }

    public String getConsumerKey() {
        return consumerKey;
    }

    public String getConsumerSecret() {
        return consumerSecret;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setConsumerKey(String consumerKey) {
        this.consumerKey = consumerKey;
    }

    public void setConsumerSecret(String consumerSecret) {
        this.consumerSecret = consumerSecret;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
