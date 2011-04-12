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
package org.switchyard.internal.io;

/**
 * All the different kinds of Serializers, so you can access a singleton for each type.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public enum SerializerType {

    /** OBJECT_STREAM. */
    OBJECT_STREAM(new ObjectStreamSerializer()),
    /** ZIP_OBJECT_STREAM. */
    ZIP_OBJECT_STREAM(new ZIPSerializer(OBJECT_STREAM.instance())),
    /** GZIP_OBJECT_STREAM. */
    GZIP_OBJECT_STREAM(new GZIPSerializer(OBJECT_STREAM.instance())),
    /** GRAPH_OBJECT_STREAM. */
    GRAPH_OBJECT_STREAM(new GraphSerializer(OBJECT_STREAM.instance())),
    /** ZIP_GRAPH_OBJECT_STREAM. */
    ZIP_GRAPH_OBJECT_STREAM(new ZIPSerializer(GRAPH_OBJECT_STREAM.instance())),
    /** GZIP_GRAPH_OBJECT_STREAM. */
    GZIP_GRAPH_OBJECT_STREAM(new GZIPSerializer(GRAPH_OBJECT_STREAM.instance())),

    /** BEAN_XML. */
    BEAN_XML(new BeanXMLSerializer()),
    /** ZIP_BEAN_XML. */
    ZIP_BEAN_XML(new ZIPSerializer(BEAN_XML.instance())),
    /** GZIP_BEAN_XML. */
    GZIP_BEAN_XML(new GZIPSerializer(BEAN_XML.instance())),
    /** GRAPH_BEAN_XML. */
    GRAPH_BEAN_XML(new GraphSerializer(BEAN_XML.instance())),
    /** ZIP_GRAPH_BEAN_XML. */
    ZIP_GRAPH_BEAN_XML(new ZIPSerializer(GRAPH_BEAN_XML.instance())),
    /** GZIP_GRAPH_BEAN_XML. */
    GZIP_GRAPH_BEAN_XML(new GZIPSerializer(GRAPH_BEAN_XML.instance())),

    /** GRAPH_PROTOSTUFF. */
    GRAPH_PROTOSTUFF(new GraphProtostuffSerializer()),
    /** ZIP_GRAPH_PROTOSTUFF. */
    ZIP_GRAPH_PROTOSTUFF(new ZIPSerializer(GRAPH_PROTOSTUFF.instance())),
    /** GZIP_GRAPH_PROTOSTUFF. */
    GZIP_GRAPH_PROTOSTUFF(new GZIPSerializer(GRAPH_PROTOSTUFF.instance())),
    /** GRAPH_GRAPH_PROTOSTUFF. */
    GRAPH_GRAPH_PROTOSTUFF(new GraphSerializer(GRAPH_PROTOSTUFF.instance())),
    /** ZIP_GRAPH_GRAPH_PROTOSTUFF. */
    ZIP_GRAPH_GRAPH_PROTOSTUFF(new ZIPSerializer(GRAPH_GRAPH_PROTOSTUFF.instance())),
    /** GZIP_GRAPH_GRAPH_PROTOSTUFF. */
    GZIP_GRAPH_GRAPH_PROTOSTUFF(new GZIPSerializer(GRAPH_GRAPH_PROTOSTUFF.instance())),

    /** NATIVE_PROTOSTUFF. */
    NATIVE_PROTOSTUFF(new NativeProtostuffSerializer()),
    /** ZIP_NATIVE_PROTOSTUFF. */
    ZIP_NATIVE_PROTOSTUFF(new ZIPSerializer(NATIVE_PROTOSTUFF.instance())),
    /** GZIP_NATIVE_PROTOSTUFF. */
    GZIP_NATIVE_PROTOSTUFF(new GZIPSerializer(NATIVE_PROTOSTUFF.instance())),
    /** GRAPH_NATIVE_PROTOSTUFF. */
    GRAPH_NATIVE_PROTOSTUFF(new GraphSerializer(NATIVE_PROTOSTUFF.instance())),
    /** ZIP_GRAPH_NATIVE_PROTOSTUFF. */
    ZIP_GRAPH_NATIVE_PROTOSTUFF(new ZIPSerializer(GRAPH_NATIVE_PROTOSTUFF.instance())),
    /** GZIP_GRAPH_NATIVE_PROTOSTUFF. */
    GZIP_GRAPH_NATIVE_PROTOSTUFF(new GZIPSerializer(GRAPH_NATIVE_PROTOSTUFF.instance())),

    /** PROTOBUF_PROTOSTUFF. */
    PROTOBUF_PROTOSTUFF(new ProtobufProtostuffSerializer()),
    /** ZIP_PROTOBUF_PROTOSTUFF. */
    ZIP_PROTOBUF_PROTOSTUFF(new ZIPSerializer(PROTOBUF_PROTOSTUFF.instance())),
    /** GZIP_PROTOBUF_PROTOSTUFF. */
    GZIP_PROTOBUF_PROTOSTUFF(new GZIPSerializer(PROTOBUF_PROTOSTUFF.instance())),
    /** GRAPH_PROTOBUF_PROTOSTUFF. */
    GRAPH_PROTOBUF_PROTOSTUFF(new GraphSerializer(PROTOBUF_PROTOSTUFF.instance())),
    /** ZIP_GRAPH_PROTOBUF_PROTOSTUFF. */
    ZIP_GRAPH_PROTOBUF_PROTOSTUFF(new ZIPSerializer(GRAPH_PROTOBUF_PROTOSTUFF.instance())),
    /** GZIP_GRAPH_PROTOBUF_PROTOSTUFF. */
    GZIP_GRAPH_PROTOBUF_PROTOSTUFF(new GZIPSerializer(GRAPH_PROTOBUF_PROTOSTUFF.instance())),

    /** JSON_PROTOSTUFF. */
    JSON_PROTOSTUFF(new JSONProtostuffSerializer()),
    /** ZIP_JSON_PROTOSTUFF. */
    ZIP_JSON_PROTOSTUFF(new ZIPSerializer(JSON_PROTOSTUFF.instance())),
    /** GZIP_JSON_PROTOSTUFF. */
    GZIP_JSON_PROTOSTUFF(new GZIPSerializer(JSON_PROTOSTUFF.instance())),
    /** GRAPH_JSON_PROTOSTUFF. */
    GRAPH_JSON_PROTOSTUFF(new GraphSerializer(JSON_PROTOSTUFF.instance())),
    /** ZIP_GRAPH_JSON_PROTOSTUFF. */
    ZIP_GRAPH_JSON_PROTOSTUFF(new ZIPSerializer(GRAPH_JSON_PROTOSTUFF.instance())),
    /** GZIP_GRAPH_JSON_PROTOSTUFF. */
    GZIP_GRAPH_JSON_PROTOSTUFF(new GZIPSerializer(GRAPH_JSON_PROTOSTUFF.instance())),

    /** NUMERIC_JSON_PROTOSTUFF. */
    NUMERIC_JSON_PROTOSTUFF(new NumericJSONProtostuffSerializer()),
    /** ZIP_NUMERIC_JSON_PROTOSTUFF. */
    ZIP_NUMERIC_JSON_PROTOSTUFF(new ZIPSerializer(NUMERIC_JSON_PROTOSTUFF.instance())),
    /** GZIP_NUMERIC_JSON_PROTOSTUFF. */
    GZIP_NUMERIC_JSON_PROTOSTUFF(new GZIPSerializer(NUMERIC_JSON_PROTOSTUFF.instance())),
    /** GRAPH_NUMERIC_JSON_PROTOSTUFF. */
    GRAPH_NUMERIC_JSON_PROTOSTUFF(new GraphSerializer(NUMERIC_JSON_PROTOSTUFF.instance())),
    /** ZIP_GRAPH_NUMERIC_JSON_PROTOSTUFF. */
    ZIP_GRAPH_NUMERIC_JSON_PROTOSTUFF(new ZIPSerializer(GRAPH_NUMERIC_JSON_PROTOSTUFF.instance())),
    /** GZIP_GRAPH_NUMERIC_JSON_PROTOSTUFF. */
    GZIP_GRAPH_NUMERIC_JSON_PROTOSTUFF(new GZIPSerializer(GRAPH_NUMERIC_JSON_PROTOSTUFF.instance())),

    /** XML_PROTOSTUFF. */
    XML_PROTOSTUFF(new XMLProtostuffSerializer()),
    /** ZIP_XML_PROTOSTUFF. */
    ZIP_XML_PROTOSTUFF(new ZIPSerializer(XML_PROTOSTUFF.instance())),
    /** GZIP_XML_PROTOSTUFF. */
    GZIP_XML_PROTOSTUFF(new GZIPSerializer(XML_PROTOSTUFF.instance())),
    /** GRAPH_XML_PROTOSTUFF. */
    GRAPH_XML_PROTOSTUFF(new GraphSerializer(XML_PROTOSTUFF.instance())),
    /** ZIP_GRAPH_XML_PROTOSTUFF. */
    ZIP_GRAPH_XML_PROTOSTUFF(new ZIPSerializer(GRAPH_XML_PROTOSTUFF.instance())),
    /** GZIP_GRAPH_XML_PROTOSTUFF. */
    GZIP_GRAPH_XML_PROTOSTUFF(new GZIPSerializer(GRAPH_XML_PROTOSTUFF.instance())),

    /** DEFAULT. */
    DEFAULT(GZIP_GRAPH_GRAPH_PROTOSTUFF.instance());

    private final Serializer _instance;

    /**
     * Constructs a new Serializers enum with the specified Serializer instance to use as a singleton.
     * @param instance the Serializer to use as a singleton
     */
    SerializerType(Serializer instance) {
        _instance = instance;
    }

    /**
     * Returns the Serializer singleton instance.
     * @return the Serializer singleton instance
     */
    public final Serializer instance() {
        return _instance;
    }

    /**
     * Returns the Serializer singleton instance associated with the specified Serializer's type name.
     * Useful for possible future configuration by SerializerType name.
     * @param name the name of the Serializers enum
     * @return the Serializer instance
     */
    public static final Serializer instanceOf(String name) {
        if (name != null) {
            name = name.trim().toUpperCase();
            SerializerType type = valueOf(name);
            if (type != null) {
                return type.instance();
            }
        }
        return null;
    }

}
