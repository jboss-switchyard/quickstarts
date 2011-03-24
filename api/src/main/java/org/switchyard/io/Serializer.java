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
package org.switchyard.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Represents a mechanism for serializing and deserializing objects.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public interface Serializer {

    /**
     * The default buffer size.
     */
    public static final int DEFAULT_BUFFER_SIZE = 256;

    /**
     * Serializes the given object of the specified type to a byte array, using the default buffer size.
     * @param <T> the type
     * @param obj the object to serialize
     * @param type the class type used for a serialization schema
     * @return the serialized bytes
     * @throws IOException if a problem happened during serialization
     */
    public <T> byte[] serialize(T obj, Class<T> type) throws IOException;

    /**
     * Serializes the given object of the specified type to a byte array, using the specified buffer size.
     * @param <T> the type
     * @param obj the object to serialize
     * @param type the class type used for a serialization schema
     * @param bufferSize the specified buffer size
     * @return the serialized bytes
     * @throws IOException if a problem happened during serialization
     */
    public <T> byte[] serialize(T obj, Class<T> type, int bufferSize) throws IOException;

    /**
     * Serializes the given object of the specified type to an OutputStream, using the default buffer size.
     * @param <T> the type
     * @param obj the object to serialize
     * @param type the class type used for a serialization schema
     * @param out the OutputStream to write to
     * @return the number of bytes written to the OutputStream
     * @throws IOException if a problem happened during serialization
     */
    public <T> int serialize(T obj, Class<T> type, OutputStream out) throws IOException;

    /**
     * Serializes the given object of the specified type to an OutputStream, using the default buffer size.
     * @param <T> the type
     * @param obj the object to serialize
     * @param type the class type used for a serialization schema
     * @param out the OutputStream to write to
     * @param bufferSize the specified buffer size
     * @return the number of bytes written to the OutputStream
     * @throws IOException if a problem happened during serialization
     */
    public <T> int serialize(T obj, Class<T> type, OutputStream out, int bufferSize) throws IOException;

    /**
     * Deserializes the given byte array to an object of the specified type, using the default buffer size.
     * @param <T> the type
     * @param bytes the bytes to deserialize
     * @param type the class type used for a deserialization schema
     * @return the deserialized object
     * @throws IOException if a problem happened during deserialization
     */
    public <T> T deserialize(byte[] bytes, Class<T> type) throws IOException;

    /**
     * Deserializes the given byte array to an object of the specified type, using the specified buffer size.
     * @param <T> the type
     * @param bytes the bytes to deserialize
     * @param type the class type used for a deserialization schema
     * @param bufferSize the specified buffer size
     * @return the deserialized object
     * @throws IOException if a problem happened during deserialization
     */
    public <T> T deserialize(byte[] bytes, Class<T> type, int bufferSize) throws IOException;

    /**
     * Deserializes the given InputStream to an object of the specified type, using the default buffer size.
     * @param <T> the type
     * @param in the InputStream to deserialize
     * @param type the class type used for a deserialization schema
     * @return the deserialized object
     * @throws IOException if a problem happened during deserialization
     */
    public <T> T deserialize(InputStream in, Class<T> type) throws IOException;

    /**
     * Deserializes the given InputStream to an object of the specified type, using the specified buffer size.
     * @param <T> the type
     * @param in the InputStream to deserialize
     * @param type the class type used for a deserialization schema
     * @param bufferSize the specified buffer size
     * @return the deserialized object
     * @throws IOException if a problem happened during deserialization
     */
    public <T> T deserialize(InputStream in, Class<T> type, int bufferSize) throws IOException;

}
