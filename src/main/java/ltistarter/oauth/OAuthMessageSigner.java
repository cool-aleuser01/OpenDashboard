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
package ltistarter.oauth;

import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.SortedMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OAuthMessageSigner {

    private static Logger logger = LoggerFactory.getLogger(OAuthMessageSigner.class);

    /**
     * This method double encodes the parameter keys and values. Thus, it expects the keys and values contained in the 'parameters' SortedMap NOT to be encoded.
     * 
     * @param secret
     * @param algorithm
     * @param method
     * @param url
     * @param parameters
     * @return oauth signature
     * @throws Exception
     */
    public String sign(String secret, String algorithm, String method, String url, SortedMap<String, String> parameters) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec((secret.concat(OAuthUtil.AMPERSAND)).getBytes(), algorithm);
        Mac mac = Mac.getInstance(secretKeySpec.getAlgorithm());
        mac.init(secretKeySpec);

        StringBuilder signatureBase = new StringBuilder(OAuthUtil.percentEncode(method));
        signatureBase.append(OAuthUtil.AMPERSAND);

        signatureBase.append(OAuthUtil.percentEncode(url));
        signatureBase.append(OAuthUtil.AMPERSAND);

        int count = 0;
        for (String key : parameters.keySet()) {
            count++;
            signatureBase.append(OAuthUtil.percentEncode(OAuthUtil.percentEncode(key)));
            signatureBase.append(URLEncoder.encode(OAuthUtil.EQUAL, OAuthUtil.ENCODING));
            signatureBase.append(OAuthUtil.percentEncode(OAuthUtil.percentEncode(parameters.get(key))));

            if (count < parameters.size()) {
                signatureBase.append(URLEncoder.encode(OAuthUtil.AMPERSAND, OAuthUtil.ENCODING));
            }
        }

        logger.debug("SECRET: {}", secret);
        logger.debug("SIGNATURE BASE: {}", signatureBase.toString());

        byte[] bytes = mac.doFinal(signatureBase.toString().getBytes());
        byte[] encodedMacBytes = Base64.encodeBase64(bytes);

        return new String(encodedMacBytes);
    }

    /**
     * This method double encodes the parameter keys and values. Thus, it
     * expects the keys and values contained in the 'parameters' SortedMap NOT
     * to be encoded. This method also generates oauth_body_hash parameter and
     * adds it to 'parameters' SortedMap
     * 
     * @param secret
     * @param algorithm
     * @param method
     * @param url
     * @param parameters
     * @param requestBody
     * @return oauth signature
     * @throws Exception
     */
    public String signWithBodyHash(String secret, String algorithm, String method, String url, SortedMap<String, String> parameters, String requestBody)
            throws Exception {

        byte[] bytes = requestBody.getBytes();

        MessageDigest sha = MessageDigest.getInstance("SHA-1");
        sha.reset();
        sha.update(bytes);
        byte[] encodedRequestBytes = Base64.encodeBase64(sha.digest());

        String oauthBodyHash = new String(encodedRequestBytes);

        parameters.put(OAuthUtil.OAUTH_POST_BODY_PARAMETER, oauthBodyHash);

        return sign(secret, algorithm, method, url, parameters);
    }

}
