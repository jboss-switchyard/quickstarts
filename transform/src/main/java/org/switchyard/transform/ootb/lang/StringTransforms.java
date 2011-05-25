/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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
