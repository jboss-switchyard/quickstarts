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
package org.switchyard.serial;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Serializes and deserializes objects.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public interface Serializer {

    /**
     * Serializes an object of a type to a byte array.
     * @param <T> the type of object
     * @param obj the object
     * @param type the type
     * @return the byte array
     * @throws IOException something wicked this way comes
     */
    public <T> byte[] serialize(T obj, Class<T> type) throws IOException;

    /**
     * Serializes an object of a type to an output stream.
     * @param <T> the type of object
     * @param obj the object
     * @param type the type
     * @param out the output stream
     * @return how many bytes were written
     * @throws IOException something wicked this way comes
     */
    public <T> int serialize(T obj, Class<T> type, OutputStream out) throws IOException;

    /**
     * Deserializes a byte array to an object of a type.
     * @param <T> the type of object
     * @param bytes the byte array
     * @param type the type
     * @return the object
     * @throws IOException something wicked this way comes
     */
    public <T> T deserialize(byte[] bytes, Class<T> type) throws IOException;

    /**
     * Deserializes an input stream to an object of a type.
     * @param <T> the type of object
     * @param in the input stream
     * @param type the type
     * @return the object
     * @throws IOException something wicked this way comes
     */
    public <T> T deserialize(InputStream in, Class<T> type) throws IOException;

    /**
     * Gets the type of format being used.
     * @return the format
     */
    public FormatType getFormat();

    /**
     * Gets the type of compression being used.
     * @return the compression
     */
    public CompressionType getCompression();

    /**
     * Gets the buffer size being used.
     * @return the buffer size
     */
    public int getBufferSize();

    /**
     * Sets the buffer size being used.
     * @param bufferSize the buffer size
     * @return this instance (useful for chaining)
     */
    public Serializer setBufferSize(int bufferSize);

    /**
     * Gets if streams will be closed.
     * @return if streams will be closed
     */
    public boolean isCloseEnabled();

    /**
     * Sets if streams will be closed.
     * @param closeEnabled if streams will be closed
     * @return this instance (useful for chaining)
     */
    public Serializer setCloseEnabled(boolean closeEnabled);

    /**
     * Gets if pretty-print should be attempted by supported formats.
     * @return if pretty-print should be attempted by supported formats
     */
    public boolean isPrettyPrint();

    /**
     * Sets if pretty-print should be attempted by supported formats.
     * @param prettyPrint if pretty-print should be attempted by supported formats
     * @return this instance (useful for chaining)
     */
    public Serializer setPrettyPrint(boolean prettyPrint);

}
