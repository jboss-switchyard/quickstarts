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

import org.switchyard.annotations.Transformer;
import org.xml.sax.InputSource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

/**
 * {@link InputSource} Transforms.
 * <p/>
 * All transforms close the supplied {@link InputSource} stream/reader before returning.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class InputSourceTransforms {

    /**
     * Singleton Instance.
     */
    public static final InputSourceTransforms TRANSFORMER = new InputSourceTransforms();

    /**
     * Transform to String.
     * @param inSource Input Source.
     * @return String.
     */
    @Transformer
    public String toString(InputSource inSource) {
        Reader reader = inSource.getCharacterStream();

        if (reader != null) {
            return ReaderTransforms.TRANSFORMER.toString(reader);
        }

        InputStream stream = inSource.getByteStream();
        if (stream != null) {
            return InputStreamTransforms.TRANSFORMER.toString(stream);
        }

        return null;
    }

    /**
     * Transform to Reader.
     * @param inSource Input Source.
     * @return Reader.
     */
    @Transformer
    public Reader toReader(InputSource inSource) {
        Reader reader = inSource.getCharacterStream();

        if (reader != null) {
            return new StringReader(ReaderTransforms.TRANSFORMER.toString(reader));
        }

        return null;
    }

    /**
     * Transform to InputStream.
     * @param inSource Input Source.
     * @return InputStream.
     */
    @Transformer
    public InputStream toInputStream(InputSource inSource) {
        InputStream stream = inSource.getByteStream();

        if (stream != null) {
            return new ByteArrayInputStream(InputStreamTransforms.TRANSFORMER.toBytes(stream));
        }

        return null;
    }

    /**
     * Transform to Integer.
     * @param inSource Input Source.
     * @return Integer.
     */
    @Transformer
    public Integer toInteger(InputSource inSource) {
        return Integer.parseInt(toString(inSource));
    }

    /**
     * Transform to Long.
     * @param inSource Input Source.
     * @return Long.
     */
    @Transformer
    public Long toLong(InputSource inSource) {
        return Long.parseLong(toString(inSource));
    }

    /**
     * Transform to Short.
     * @param inSource Input Source.
     * @return Short.
     */
    @Transformer
    public Short toShort(InputSource inSource) {
        return Short.parseShort(toString(inSource));
    }

    /**
     * Transform to char[].
     * @param inSource Input Source.
     * @return char[].
     */
    @Transformer
    public char[] toChars(InputSource inSource) {
        return toString(inSource).toCharArray();
    }

    /**
     * Transform to Character.
     * @param inSource Input Source.
     * @return Character.
     */
    @Transformer
    public Character toCharacter(InputSource inSource) {
        return toString(inSource).charAt(0);
    }

    /**
     * Transform to byte[].
     * @param inSource Input Source.
     * @return byte[].
     */
    @Transformer
    public byte[] toBytes(InputSource inSource) {
        InputStream stream = inSource.getByteStream();

        if (stream != null) {
            return InputStreamTransforms.TRANSFORMER.toBytes(stream);
        }

        Reader reader = inSource.getCharacterStream();

        if (reader != null) {
            return ReaderTransforms.TRANSFORMER.toBytes(reader);
        }

        return null;
    }

    /**
     * Transform to Float.
     * @param inSource Input Source.
     * @return Float.
     */
    @Transformer
    public Double toDouble(InputSource inSource) {
        return Double.parseDouble(toString(inSource));
    }

    /**
     * Transform to Float.
     * @param inSource Input Source.
     * @return Float.
     */
    @Transformer
    public Float toFloat(InputSource inSource) {
        return Float.parseFloat(toString(inSource));
    }
}
