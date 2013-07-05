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
