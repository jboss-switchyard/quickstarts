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

import junit.framework.Assert;

import org.junit.Test;
import org.switchyard.io.Serializer;

/**
 * Tests various serializers, using both a passed in object, and a graphed object.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class SimpleSerializationTests {

    @Test
    public void testObjectStreamSerializer() throws Exception {
        doTestSerializer(new ObjectStreamSerializer());
    }

    @Test
    public void testBeanXMLSerializer() throws Exception {
        doTestSerializer(new BeanXMLSerializer());
    }

    @Test
    public void testNativeProtostuffSerializer() throws Exception {
        doTestSerializer(new NativeProtostuffSerializer());
    }

    @Test
    public void testProtobufProtostuffSerializer() throws Exception {
        doTestSerializer(new ProtobufProtostuffSerializer());
    }

    @Test
    public void testJSONProtostuffSerializer() throws Exception {
        doTestSerializer(new JSONProtostuffSerializer());
    }

    @Test
    public void testNumericJSONProtostuffSerializer() throws Exception {
        doTestSerializer(new NumericJSONProtostuffSerializer());
    }

    @Test
    public void testXMLProtostuffSerializer() throws Exception {
        doTestSerializer(new XMLProtostuffSerializer());
    }

    private void doTestSerializer(Serializer ser) throws Exception {
        doRunSerializer(ser);
        doRunSerializer(new GraphSerializer(ser));
    }

    private void doRunSerializer(Serializer ser) throws Exception {
        Person david = new Person();
        david.setName("David");
        Car mustang = new Car();
        mustang.setDriver(david);
        byte[] bytes = ser.serialize(mustang, Car.class);
        /*
        System.out.println(ser.getClass().getSimpleName() + ": " + bytes.length + " bytes");
        System.out.println(new String(bytes));
        System.out.println();
        */
        Car car = ser.deserialize(bytes, Car.class);
        Assert.assertEquals("David", car.getDriver().getName());
    }

}
