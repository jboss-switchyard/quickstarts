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
package org.switchyard.serial.protostuff;

import org.junit.Assert;
import org.junit.Test;
import org.switchyard.serial.CompressionType;
import org.switchyard.serial.FormatType;
import org.switchyard.serial.Serializer;
import org.switchyard.serial.SerializerFactory;
import org.switchyard.serial.protostuff.ProtostuffSerializationData.Car;
import org.switchyard.serial.protostuff.ProtostuffSerializationData.Person;

/**
 * Tests various serializers, using both a passed in object, and a mapped object, uncompressed and compressed.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public final class ProtostuffComparisonSerializationTest {

    @Test
    public void testSerialization() throws Exception {
        for (FormatType format : FormatType.values()) {
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
        Car car = new Car(new Person("Dave"));
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
        Assert.assertEquals("Dave", car.getDriver().getNickName());
    }

}
