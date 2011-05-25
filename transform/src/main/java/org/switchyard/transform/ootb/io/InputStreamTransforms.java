/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *  *
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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

/**
 * {@link java.io.InputStream} Transforms.
 * <p/>
 * All transforms close the supplied {@link java.io.InputStream} before returning.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class InputStreamTransforms {

    /**
     * Singleton Instance.
     */
    public static final InputStreamTransforms TRANSFORMER = new InputStreamTransforms();
    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(ReaderTransforms.class);

    /**
     * Transform to String.
     * @param inStream Input Stream.
     * @return String.
     */
    @Transformer
    public String toString(InputStream inStream) {
        return ReaderTransforms.TRANSFORMER.toString(new InputStreamReader(inStream));
    }

    /**
     * Transform to Reader.
     * @param inStream Input Stream.
     * @return Reader.
     */
    @Transformer
    public Reader toReader(InputStream inStream) {
        return new StringReader(toString(inStream));
    }

    /**
     * Transform to InputSource.
     * @param inStream Input Stream.
     * @return InputSource.
     */
    @Transformer
    public InputSource toInputSource(InputStream inStream) {
        byte[] bytes = toBytes(inStream);
        InputSource inputSource = new InputSource();

        inputSource.setByteStream(new ByteArrayInputStream(bytes));
        // Purposely not setting the character stream because we cannot guarantee decoding of the bytes.

        return inputSource;
    }

    /**
     * Transform to Integer.
     * @param inStream Input Stream.
     * @return Integer.
     */
    @Transformer
    public Integer toInteger(InputStream inStream) {
        return Integer.parseInt(toString(inStream));
    }

    /**
     * Transform to Long.
     * @param inStream Input Stream.
     * @return Long.
     */
    @Transformer
    public Long toLong(InputStream inStream) {
        return Long.parseLong(toString(inStream));
    }

    /**
     * Transform to Short.
     * @param inStream Input Stream.
     * @return Short.
     */
    @Transformer
    public Short toShort(InputStream inStream) {
        return Short.parseShort(toString(inStream));
    }

    /**
     * Transform to char[].
     * @param inStream Input Stream.
     * @return char[].
     */
    @Transformer
    public char[] toChars(InputStream inStream) {
        return toString(inStream).toCharArray();
    }

    /**
     * Transform to Character.
     * @param inStream Input Stream.
     * @return Character.
     */
    @Transformer
    public Character toCharacter(InputStream inStream) {
        return toString(inStream).charAt(0);
    }

    /**
     * Transform to byte[].
     * @param inStream Input Stream.
     * @return byte[].
     */
    @Transformer
    public byte[] toBytes(InputStream inStream) {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        try {
            byte[] readBuffer = new byte[56];
            int readCount;

            while ((readCount = inStream.read(readBuffer)) != -1) {
                byteBuffer.write(readBuffer, 0, readCount);
            }
        } catch (IOException e) {
            LOGGER.debug("Error reading from InputStream.", e);
        } finally {
            try {
                inStream.close();
            } catch (IOException e) {
                LOGGER.debug("Failed to close InputStream instance.", e);
            }
        }

        return byteBuffer.toByteArray();
    }

    /**
     * Transform to Double.
     * @param inStream Input Stream.
     * @return Double.
     */
    @Transformer
    public Double toDouble(InputStream inStream) {
        return Double.parseDouble(toString(inStream));
    }

    /**
     * Transform to Float.
     * @param inStream Input Stream.
     * @return Float.
     */
    @Transformer
    public Float toFloat(InputStream inStream) {
        return Float.parseFloat(toString(inStream));
    }
}
