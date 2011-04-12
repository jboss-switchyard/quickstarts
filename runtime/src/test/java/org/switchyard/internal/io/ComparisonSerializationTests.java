/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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

import static org.switchyard.internal.io.SerializerType.BEAN_XML;
import static org.switchyard.internal.io.SerializerType.GRAPH_PROTOSTUFF;
import static org.switchyard.internal.io.SerializerType.JSON_PROTOSTUFF;
import static org.switchyard.internal.io.SerializerType.NATIVE_PROTOSTUFF;
import static org.switchyard.internal.io.SerializerType.NUMERIC_JSON_PROTOSTUFF;
import static org.switchyard.internal.io.SerializerType.OBJECT_STREAM;
import static org.switchyard.internal.io.SerializerType.PROTOBUF_PROTOSTUFF;
import static org.switchyard.internal.io.SerializerType.XML_PROTOSTUFF;
import junit.framework.Assert;

import org.junit.Test;
import org.switchyard.internal.io.Data.Car;
import org.switchyard.internal.io.Data.Person;

/**
 * Tests various serializers, using both a passed in object, and a graphed object, uncompressed and compressed.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public final class ComparisonSerializationTests {

    @Test
    public void testObjectStreamSerializer() throws Exception {
        doTestSerializer(OBJECT_STREAM);
    }

    @Test
    public void testBeanXMLSerializer() throws Exception {
        doTestSerializer(BEAN_XML);
    }

    @Test
    public void testGraphProtostuffSerializer() throws Exception {
        doTestSerializer(GRAPH_PROTOSTUFF);
    }

    @Test
    public void testNativeProtostuffSerializer() throws Exception {
        doTestSerializer(NATIVE_PROTOSTUFF);
    }

    @Test
    public void testProtobufProtostuffSerializer() throws Exception {
        doTestSerializer(PROTOBUF_PROTOSTUFF);
    }

    @Test
    public void testJSONProtostuffSerializer() throws Exception {
        doTestSerializer(JSON_PROTOSTUFF);
    }

    @Test
    public void testNumericJSONProtostuffSerializer() throws Exception {
        doTestSerializer(NUMERIC_JSON_PROTOSTUFF);
    }

    @Test
    public void testXMLProtostuffSerializer() throws Exception {
        doTestSerializer(XML_PROTOSTUFF);
    }

    private void doTestSerializer(SerializerType type) throws Exception {
        Serializer serializer = type.instance();
        doRunSerializer(serializer, 0, false);
        doRunSerializer(new ZIPSerializer(serializer), 4, false);
        doRunSerializer(new GZIPSerializer(serializer), 4, false);
        doRunSerializer(new GraphSerializer(serializer), 4, false);
        doRunSerializer(new ZIPSerializer(new GraphSerializer(serializer)), 8, false);
        doRunSerializer(new GZIPSerializer(new GraphSerializer(serializer)), 8, true);
    }

    private void doRunSerializer(Serializer serializer, int spaces, boolean newline) throws Exception {
        Car car = new Car(new Person("driver"));
        long start = System.currentTimeMillis();
        byte[] bytes = serializer.serialize(car, Car.class);
        car = serializer.deserialize(bytes, Car.class);
        long stop = System.currentTimeMillis();
        boolean debug = false; // toggle this is you're interested in performance
        if (debug) {
            StringBuilder sb = new StringBuilder();
            for (int i=0; i < spaces; i++) {
                sb.append(' ');
            }
            sb.append(serializer.getClass().getSimpleName());
            sb.append(": ");
            sb.append(bytes.length);
            sb.append(" bytes serialized and deserialized in ");
            sb.append(stop - start);
            sb.append(" milliseconds");
            if (newline) {
                sb.append("\n");
            }
            System.out.println(sb);
        }
        Assert.assertEquals("driver", car.getDriver().getName());
    }

}
