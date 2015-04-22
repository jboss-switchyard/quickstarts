/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.switchyard.common.codec;

import java.nio.charset.Charset;

import javax.xml.bind.DatatypeConverter;

/**
 * Base64 utilities.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public final class Base64 {

    /**
     * Encodes a byte array to a Base64-encoded String.
     * @param bytes the byte array
     * @return the Base64-encoded String
     */
    public static final String encode(byte[] bytes) {
        return DatatypeConverter.printBase64Binary(bytes);
    }

    /**
     * Encodes a String to a Base64-encoded String, first obtaining the bytes using the platform-default charset.
     * @param value the String
     * @return the Base64-encoded String
     */
    public static final String encodeFromString(String value) {
        return encodeFromString(value, Charset.defaultCharset());
    }

    /**
     * Encodes a String to a Base64-encoded String, first obtaining the bytes using the specified charset name.
     * @param value the String
     * @param charsetName the specified charset name
     * @return the Base64-encoded String
     */
    public static final String encodeFromString(String value, String charsetName) {
        return encodeFromString(value, Charset.forName(charsetName));
    }

    /**
     * Encodes a String to a Base64-encoded String, first obtaining the bytes using the specified charset.
     * @param value the String
     * @param charset the specified charset
     * @return the Base64-encoded String
     */
    public static final String encodeFromString(String value, Charset charset) {
        return encode(value.getBytes(charset));
    }

    /**
     * Decodes a Base64-encoded String to a byte array.
     * @param encoded the Base64-encoded String
     * @return the byte array
     */
    public static final byte[] decode(String encoded) {
        return DatatypeConverter.parseBase64Binary(encoded);
    }

    /**
     * Decodes a Base64-encoded String to String, constructed with the platform-default charset.
     * @param encoded the Bas64-encoded String
     * @return the decoded String
     */
    public static final String decodeToString(String encoded) {
        return decodeToString(encoded, Charset.defaultCharset());
    }

    /**
     * Decodes a Base64-encoded String to String, constructed with the specified charset name.
     * @param encoded the Bas64-encoded String
     * @param charsetName the specified charset name
     * @return the decoded String
     */
    public static final String decodeToString(String encoded, String charsetName) {
        return decodeToString(encoded, Charset.forName(charsetName));
    }

    /**
     * Decodes a Base64-encoded String to String, constructed with the specified charset.
     * @param encoded the Bas64-encoded String
     * @param charset the specified charset
     * @return the decoded String
     */
    public static final String decodeToString(String encoded, Charset charset) {
        return new String(decode(encoded), charset);
    }

    private Base64() {}

}
