/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
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
