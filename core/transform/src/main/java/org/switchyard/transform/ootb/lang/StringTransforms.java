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
package org.switchyard.transform.ootb.lang;

import org.switchyard.annotations.Transformer;
import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

/**
 * String Transformations.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class StringTransforms {

    /**
     * Singleton Instance.
     */
    public static final StringTransforms TRANSFORMER = new StringTransforms();

    /**
     * Transform to Reader.
     * @param string Input String.
     * @return Reader.
     */
    @Transformer
    public Reader toReader(String string) {
        return new StringReader(string);
    }

    /**
     * Transform to InputStream.
     * @param string Input String.
     * @return InputStream.
     */
    @Transformer
    public InputStream toInputStream(String string) {
        return new ByteArrayInputStream(string.getBytes());
    }

    /**
     * Transform to InputSource.
     * @param string Input String.
     * @return InputSource.
     */
    @Transformer
    public InputSource toInputSource(String string) {
        InputSource inputSource = new InputSource();
        inputSource.setByteStream(new ByteArrayInputStream(string.getBytes()));
        inputSource.setCharacterStream(new StringReader(string));
        return inputSource;
    }

    /**
     * Transform to Integer.
     * @param string Input String.
     * @return Integer.
     */
    @Transformer
    public Integer toInteger(String string) {
        return Integer.parseInt(string);
    }

    /**
     * Transform to Long.
     * @param string Input String.
     * @return Long.
     */
    @Transformer
    public Long toLong(String string) {
        return Long.parseLong(string);
    }

    /**
     * Transform to Short.
     * @param string Input String.
     * @return Short.
     */
    @Transformer
    public Short toShort(String string) {
        return Short.parseShort(string);
    }

    /**
     * Transform to char[].
     * @param string Input String.
     * @return char[].
     */
    @Transformer
    public char[] toChars(String string) {
        return string.toCharArray();
    }

    /**
     * Transform to Character.
     * @param string Input String.
     * @return Character.
     */
    @Transformer
    public Character toCharacter(String string) {
        if (string.length() > 0) {
            return string.charAt(0);
        } else {
            return null;
        }
    }

    /**
     * Transform to byte[].
     * @param string Input String.
     * @return byte[].
     */
    @Transformer
    public byte[] toBytes(String string) {
        return string.getBytes();
    }

    /**
     * Transform to Double.
     * @param string Input String.
     * @return Double.
     */
    @Transformer
    public Double toDouble(String string) {
        return Double.parseDouble(string);
    }

    /**
     * Transform to Float.
     * @param string Input String.
     * @return Float.
     */
    @Transformer
    public Float toFloat(String string) {
        return Float.parseFloat(string);
    }
}
