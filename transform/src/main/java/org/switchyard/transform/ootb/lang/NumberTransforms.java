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
