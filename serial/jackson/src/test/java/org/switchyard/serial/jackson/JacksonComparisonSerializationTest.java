/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.serial.jackson;

import junit.framework.Assert;

import org.junit.Test;
import org.switchyard.serial.CompressionType;
import org.switchyard.serial.FormatType;
import org.switchyard.serial.Serializer;
import org.switchyard.serial.SerializerFactory;
import org.switchyard.serial.jackson.JacksonSerializationData.Car;
import org.switchyard.serial.jackson.JacksonSerializationData.Person;

/**
 * Tests various serializers, using both a passed in object, and a mapped object, uncompressed and compressed.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public final class JacksonComparisonSerializationTest {

    @Test
    public void testSerialization() throws Exception {
        for (FormatType format : new FormatType[]{FormatType.JSON, FormatType.SER_OBJECT, FormatType.XML_BEAN}) {
            doTest(format);
        }
    }

    private void doTest(FormatType format) throws Exception {
        doRun(SerializerFactory.create(format, null, false), 0, false);
        doRun(SerializerFactory.create(format, CompressionType.ZIP, false), 4, false);
        doRun(SerializerFactory.create(format, CompressionType.GZIP, false), 4, false);
        doRun(SerializerFactory.create(format, null, true), 4, false);
        doRun(SerializerFactory.create(format, CompressionType.ZIP, true), 8, false);
        doRun(SerializerFactory.create(format, CompressionType.GZIP, true), 8, true);
    }

    private void doRun(Serializer serializer, int spaces, boolean newline) throws Exception {
        //serializer.setPrettyPrint(true);
        Car car = new Car(new Person("David"));
        long start = System.currentTimeMillis();
        byte[] bytes = serializer.serialize(car, Car.class);
        //System.out.println(new String(bytes));
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
        Assert.assertEquals("David", car.getDriver().getName());
    }

}
