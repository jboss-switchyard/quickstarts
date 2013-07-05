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
 *
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class NumberTransforms {

    /**
     * Singleton Instance.
     */
    public static final NumberTransforms TRANSFORMER = new NumberTransforms();

    /**
     * Transform to String.
     * @param number Input Number.
     * @return String.
     */
    @Transformer
    public String toString(Number number) {
        return number.toString();
    }

    /**
     * Transform to Reader.
     * @param number Input Number.
     * @return Reader.
     */
    @Transformer
    public Reader toReader(Number number) {
        return new StringReader(number.toString());
    }

    /**
     * Transform to InputStream.
     * @param number Input Number.
     * @return InputStream.
     */
    @Transformer
    public InputStream toInputStream(Number number) {
        return new ByteArrayInputStream(number.toString().getBytes());
    }

    /**
     * Transform to InputSource.
     * @param number Input Number.
     * @return InputSource.
     */
    @Transformer
    public InputSource toInputSource(Number number) {
        String string = toString(number);
        InputSource inputSource = new InputSource();

        inputSource.setByteStream(new ByteArrayInputStream(string.getBytes()));
        inputSource.setCharacterStream(new StringReader(string));

        return inputSource;
    }

    /**
     * Transform to char[].
     * @param number Input Number.
     * @return char[].
     */
    @Transformer
    public char[] toChars(Number number) {
        return toString(number).toCharArray();
    }

    /**
     * Transform to Character.
     * @param number Input Number.
     * @return Character.
     */
    @Transformer
    public Character toCharacter(Number number) {
        return toString(number).charAt(0);
    }

    /**
     * Transform to byte[].
     * @param number Input Number.
     * @return byte[].
     */
    @Transformer
    public byte[] toBytes(Number number) {
        return toString(number).getBytes();
    }
}
