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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.switchyard.serial.FormatType;
import org.switchyard.serial.Serializer;
import org.switchyard.serial.SerializerFactory;
import org.switchyard.serial.jackson.JacksonSerializationData.Car;
import org.switchyard.serial.jackson.JacksonSerializationData.CustomPart;
import org.switchyard.serial.jackson.JacksonSerializationData.Part;
import org.switchyard.serial.jackson.JacksonSerializationData.Person;
import org.switchyard.serial.jackson.JacksonSerializationData.Wheel;

/**
 * Tests more complex de/serialization scenarios.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public final class JacksonComplexSerializationTest {

    private <T> T serDeser(T object, Class<T> clazz) throws Exception {
        Serializer ser = SerializerFactory.create(FormatType.JSON, null, true);
        byte[] bytes = ser.serialize(object, clazz);
        return ser.deserialize(bytes, clazz);
    }

    @Test
    public void testSpecificArray() throws Exception {
        Car car = new Car();
        car.setPassengers(new Person[] {new Person("passengerA"), new Person("passengerB")});
        car = serDeser(car, Car.class);
        Assert.assertEquals(2, car.getPassengers().length);
        Assert.assertEquals("passengerB", car.getPassengers()[1].getName());
    }

    @Test
    public void testPolymorphicArray() throws Exception {
        Car car = new Car();
        car.setCheapParts(new Part[] {new Wheel(), new CustomPart(true)});
        car = serDeser(car, Car.class);
        Assert.assertEquals(2, car.getCheapParts().length);
        Assert.assertEquals(true, car.getCheapParts()[1].isReplaceable());
    }

    @Test
    public void testPolymorphicCollection() throws Exception {
        Car car = new Car();
        Collection<Part> ep = new ArrayList<Part>();
        for (int i=0; i < 4; i++) {
            ep.add(new Wheel());
        }
        ep.add(new CustomPart(false));
        car.setExpensiveParts(ep);
        car = serDeser(car, Car.class);
        Assert.assertEquals(5, car.getExpensiveParts().size());
        List<Part> list = new ArrayList<Part>(car.getExpensiveParts());
        Assert.assertEquals(true, list.get(3).isReplaceable());
        Assert.assertEquals(false, list.get(4).isReplaceable());
    }

    @Test
    public void testCircularReferences() throws Exception {
        Person me = new Person("me");
        Person bff = new Person("bff");
        me.setBestFriend(bff);
        bff.setBestFriend(me);
        Person mom = new Person("mom");
        Person dad = new Person("dad");
        mom.setBestFriend(dad);
        dad.setBestFriend(mom);
        me.getRelatives().add(mom);
        me.getRelatives().add(dad);
        mom.getRelatives().add(me);
        dad.getRelatives().add(me);
        me = serDeser(me, Person.class);
        bff = me.getBestFriend();
        Iterator<Person> parents = me.getRelatives().iterator();
        mom = parents.next();
        dad = parents.next();
        Assert.assertEquals("me", me.getName());
        Assert.assertEquals("bff", bff.getName());
        Assert.assertEquals("mom", mom.getName());
        Assert.assertEquals("dad", dad.getName());
        Assert.assertSame(me, me.getBestFriend().getBestFriend());
        Assert.assertSame(bff, bff.getBestFriend().getBestFriend());
        Assert.assertSame(mom, mom.getBestFriend().getBestFriend());
        Assert.assertSame(dad, dad.getBestFriend().getBestFriend());
        Assert.assertSame(me, mom.getRelatives().iterator().next());
        Assert.assertSame(me, dad.getRelatives().iterator().next());
    }

}
