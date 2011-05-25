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
package org.switchyard.transform.ootb.io;

import org.apache.log4j.Logger;
import org.switchyard.annotations.Transformer;
import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

/**
 * {@link Reader} Transforms.
 * <p/>
 * All transforms close the supplied {@link Reader} before returning.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class ReaderTransforms {

    /**
     * Singleton Instance.
     */
    public static final ReaderTransforms TRANSFORMER = new ReaderTransforms();
    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(ReaderTransforms.class);

    /**
     * Transform to String.
     * @param reader Input Reader.
     * @return String.
     */
    @Transformer
    public String toString(Reader reader) {
        StringBuilder builder = new StringBuilder();

        try {
            char[] readBuffer = new char[56];
            int readCount;

            while ((readCount = reader.read(readBuffer)) != -1) {
                builder.append(readBuffer, 0, readCount);
            }
        } catch (IOException e) {
            LOGGER.debug("Error reading from reader.", e);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                LOGGER.debug("Failed to close reader instance.", e);
            }
        }

        return builder.toString();
    }

    /**
     * Transform to InputStream.
     * @param reader Input Reader.
     * @return InputStream.
     */
    @Transformer
    public InputStream toInputStream(Reader reader) {
        return new ByteArrayInputStream(toString(reader).getBytes());
    }

    /**
     * Transform to InputSource.
     * @param reader Input Reader.
     * @return InputSource.
     */
    @Transformer
    public InputSource toInputSource(Reader reader) {
        String string = toString(reader);
        InputSource inputSource = new InputSource();

        inputSource.setByteStream(new ByteArrayInputStream(string.getBytes()));
        inputSource.setCharacterStream(new StringReader(string));

        return inputSource;
    }

    /**
     * Transform to Integer.
     * @param reader Input Reader.
     * @return Integer.
     */
    @Transformer
    public Integer toInteger(Reader reader) {
        return Integer.parseInt(toString(reader));
    }

    /**
     * Transform to Long.
     * @param reader Input Reader.
     * @return Long.
     */
    @Transformer
    public Long toLong(Reader reader) {
        return Long.parseLong(toString(reader));
    }

    /**
     * Transform to Short.
     * @param reader Input Reader.
     * @return Short.
     */
    @Transformer
    public Short toShort(Reader reader) {
        return Short.parseShort(toString(reader));
    }

    /**
     * Transform to char[].
     * @param reader Input Reader.
     * @return char[].
     */
    @Transformer
    public char[] toChars(Reader reader) {
        return toString(reader).toCharArray();
    }

    /**
     * Transform to Character.
     * @param reader Input Reader.
     * @return Character.
     */
    @Transformer
    public Character toCharacter(Reader reader) {
        return toString(reader).charAt(0);
    }

    /**
     * Transform to byte[].
     * @param reader Input Reader.
     * @return byte[].
     */
    @Transformer
    public byte[] toBytes(Reader reader) {
        return toString(reader).getBytes();
    }

    /**
     * Transform to Double.
     * @param reader Input Reader.
     * @return Double.
     */
    @Transformer
    public Double toDouble(Reader reader) {
        return Double.parseDouble(toString(reader));
    }

    /**
     * Transform to Float.
     * @param reader Input Reader.
     * @return Float.
     */
    @Transformer
    public Float toFloat(Reader reader) {
        return Float.parseFloat(toString(reader));
    }
}
