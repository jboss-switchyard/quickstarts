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

import java.io.StringReader;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.switchyard.HandlerException;
import org.switchyard.common.io.pull.ElementPuller;
import org.switchyard.common.xml.XMLHelper;
import org.switchyard.serial.FormatType;
import org.switchyard.serial.Serializer;
import org.switchyard.serial.SerializerFactory;
import org.switchyard.serial.protostuff.ProtostuffSerializationData.Antennae;
import org.switchyard.serial.protostuff.ProtostuffSerializationData.Car;
import org.switchyard.serial.protostuff.ProtostuffSerializationData.CustomPart;
import org.switchyard.serial.protostuff.ProtostuffSerializationData.ExpiredPart;
import org.switchyard.serial.protostuff.ProtostuffSerializationData.FlatTireException;
import org.switchyard.serial.protostuff.ProtostuffSerializationData.FluentTitle;
import org.switchyard.serial.protostuff.ProtostuffSerializationData.Name;
import org.switchyard.serial.protostuff.ProtostuffSerializationData.OutOfGasException;
import org.switchyard.serial.protostuff.ProtostuffSerializationData.Part;
import org.switchyard.serial.protostuff.ProtostuffSerializationData.Person;
import org.switchyard.serial.protostuff.ProtostuffSerializationData.StrictTitle;
import org.switchyard.serial.protostuff.ProtostuffSerializationData.Title;
import org.switchyard.serial.protostuff.ProtostuffSerializationData.Wheel;
import org.w3c.dom.Element;

/**
 * Tests more complex de/serialization scenarios.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public final class ProtostuffComplexSerializationTest {

    private <T> T serDeser(T object, Class<T> clazz) throws Exception {
        Serializer ser = SerializerFactory.create(FormatType.JSON, null, true);
        //ser.setPrettyPrint(true);
        byte[] bytes = ser.serialize(object, clazz);
        //System.out.println(new String(bytes));
        return ser.deserialize(bytes, clazz);
    }

    @Test
    public void testSpecificArray() throws Exception {
        Car car = new Car();
        car.setPassengers(new Person[] {new Person("passengerA"), new Person("passengerB")});
        car = serDeser(car, Car.class);
        Assert.assertEquals(2, car.getPassengers().length);
        Assert.assertEquals("passengerB", car.getPassengers()[1].getNickName());
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
    public void testSpecificUnsupportedType() throws Exception {
        ExpiredPart part = new ExpiredPart(new Date());
        part = serDeser(part, ExpiredPart.class);
        Assert.assertNull(part);
    }

    @Test
    public void testUnsupportedTypeArray() throws Exception {
        Car car = new Car();
        car.setCheapParts(new Part[] {new Wheel(), new ExpiredPart(new Date())});
        car = serDeser(car, Car.class);
        Assert.assertEquals(1, car.getCheapParts().length);
    }

    @Test
    public void testUnsupportedTypeCollection() throws Exception {
        Car car = new Car();
        Collection<Part> ep = new ArrayList<Part>();
        for (int i=0; i < 4; i++) {
            ep.add(new Wheel());
        }
        ep.add(new ExpiredPart(new Date()));
        car.setExpensiveParts(ep);
        car = serDeser(car, Car.class);
        Assert.assertEquals(4, car.getExpensiveParts().size());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testUnsupportedTypeMap() throws Exception {
        Map<String, Part> map = new HashMap<String, Part>();
        map.put("wheel", new Wheel());
        map.put("crank", new ExpiredPart(new Date()));
        map = serDeser(map, Map.class);
        Assert.assertEquals(1, map.size());
    }

    @Test
    public void testCircularReferences() throws Exception {
        Name bob = new Name("Bob", "Bobby", "Bobson");
        Person me = new Person("me", bob);
        Person bff = new Person("bff", bob);
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
        Assert.assertEquals("me", me.getNickName());
        Assert.assertEquals("Bob", me.getFullName().getFirst());
        Assert.assertEquals("bff", bff.getNickName());
        Assert.assertSame(me.getFullName(), bff.getFullName());
        Assert.assertEquals("mom", mom.getNickName());
        Assert.assertEquals("dad", dad.getNickName());
        Assert.assertSame(me, me.getBestFriend().getBestFriend());
        Assert.assertSame(bff, bff.getBestFriend().getBestFriend());
        Assert.assertSame(me.getFullName(), me.getBestFriend().getBestFriend().getFullName());
        Assert.assertSame(mom, mom.getBestFriend().getBestFriend());
        Assert.assertSame(dad, dad.getBestFriend().getBestFriend());
        Assert.assertSame(me, mom.getRelatives().iterator().next());
        Assert.assertSame(me, dad.getRelatives().iterator().next());
    }

    @Test
    public void testDOM() throws Exception {
        final String expectedXML = "<inspection code=\"123\"><state>NY</state></inspection>";
        final Element expectedDOM = new ElementPuller().pull(new StringReader(expectedXML));
        Car car = new Car();
        car.setInspection(expectedDOM);
        car = serDeser(car, Car.class);
        final Element actualDOM = car.getInspection();
        final String actualXML = XMLHelper.toString(actualDOM);
        Assert.assertEquals(expectedXML, actualXML);
    }

    @Test
    public void testBasicExceptions() throws Exception {
        final IllegalStateException expectedIllegalStateException = new IllegalStateException("expectedIllegalStateException");
        expectedIllegalStateException.fillInStackTrace();
        final HandlerException expectedHandlerException = new HandlerException(expectedIllegalStateException);
        expectedHandlerException.fillInStackTrace();
        final Exception expectedException = new Exception("expectedException", expectedHandlerException);
        expectedException.fillInStackTrace();
        final Exception actualException = serDeser(expectedException, Exception.class);
        final HandlerException actualHandlerException = (HandlerException)actualException.getCause();
        final IllegalStateException actualIllegalStateException = (IllegalStateException)actualHandlerException.getCause();
        Assert.assertEquals(expectedException.getMessage(), actualException.getMessage());
        Assert.assertEquals(expectedHandlerException.getMessage(), actualHandlerException.getMessage());
        Assert.assertEquals(expectedIllegalStateException.getMessage(), actualIllegalStateException.getMessage());
        Assert.assertEquals(expectedException.getStackTrace().length, actualException.getStackTrace().length);
        Assert.assertEquals(expectedHandlerException.getStackTrace().length, actualHandlerException.getStackTrace().length);
        Assert.assertEquals(expectedIllegalStateException.getStackTrace().length, actualIllegalStateException.getStackTrace().length);
        Assert.assertEquals(expectedHandlerException.isWrapper(), actualHandlerException.isWrapper());
    }

    @Test
    public void testCustomExceptions() throws Exception {
        final OutOfGasException expectedOutOfGasException = new OutOfGasException("Dagnabit!");
        final FlatTireException expectedFlatTireException = new FlatTireException(new Wheel(Wheel.Location.BACK_RIGHT));
        Car car = new Car();
        car.setProblems(Arrays.asList(new Exception[]{expectedOutOfGasException, expectedFlatTireException}));
        car = serDeser(car, Car.class);
        final List<Exception> actualExceptions = car.getProblems();
        final OutOfGasException actualOutOfGasException = (OutOfGasException)actualExceptions.get(0);
        final FlatTireException actualFlatTireException = (FlatTireException)actualExceptions.get(1);
        Assert.assertEquals(expectedOutOfGasException.getExplicitive(), actualOutOfGasException.getExplicitive());
        Assert.assertSame(expectedFlatTireException.getWheel().getLocation(), actualFlatTireException.getWheel().getLocation());
        Assert.assertEquals("Really?", actualFlatTireException.getMessage());
    }

    @Test
    public void testUndeclaredThrowableException() throws Exception {
        final UndeclaredThrowableException expectedUndeclaredThrowableException = new UndeclaredThrowableException(new Throwable("undeclared"), "message");
        Car car = new Car();
        car.setProblems(Arrays.asList(new Exception[]{expectedUndeclaredThrowableException, expectedUndeclaredThrowableException}));
        car = serDeser(car, Car.class);
        final List<Exception> actualExceptions = car.getProblems();
        final UndeclaredThrowableException actualUndeclaredThrowableException = (UndeclaredThrowableException)actualExceptions.get(0);
        final UndeclaredThrowableException sameUndeclaredThrowableException = (UndeclaredThrowableException)actualExceptions.get(1);
        Assert.assertSame(actualUndeclaredThrowableException, sameUndeclaredThrowableException);
        Assert.assertEquals("message", actualUndeclaredThrowableException.getMessage());
        Assert.assertEquals(expectedUndeclaredThrowableException.getMessage(), actualUndeclaredThrowableException.getMessage());
        Assert.assertEquals(expectedUndeclaredThrowableException.getStackTrace().length, actualUndeclaredThrowableException.getStackTrace().length);
        Assert.assertEquals("undeclared", actualUndeclaredThrowableException.getUndeclaredThrowable().getMessage());
        Assert.assertSame(actualUndeclaredThrowableException.getUndeclaredThrowable(), actualUndeclaredThrowableException.getCause());
    }

    @Test
    public void testStrictBean() throws Exception {
        final UUID id = UUID.randomUUID();
        Car car = new Car();
        StrictTitle title = new StrictTitle();
        title.setId(id);
        title.setState("NY");
        title.setLiened(true);
        car.setTitle(title);
        car = serDeser(car, Car.class);
        Assert.assertEquals(id, car.getTitle().getId());
        Assert.assertEquals("NY", car.getTitle().getState());
        Assert.assertTrue(car.getTitle().isLiened());
    }

    @Test
    public void testFluentBean() throws Exception {
        final UUID id = UUID.randomUUID();
        Car car = new Car();
        Title title = new FluentTitle().setId(id).setState("NY").setLiened(true);
        car.setTitle(title);
        car = serDeser(car, Car.class);
        Assert.assertEquals(id, car.getTitle().getId());
        Assert.assertEquals("NY", car.getTitle().getState());
        Assert.assertTrue(car.getTitle().isLiened());
    }

    @Test
    public void testEmptyBean() throws Exception {
        Car car = new Car();
        Antennae antennae = new Antennae();
        car.setAntennae(antennae);
        car = serDeser(car, Car.class);
        antennae = car.getAntennae();
        Assert.assertNotNull(antennae);
    }

    @Test
    public void testDuplicateValues() throws Exception {
        Name name = new Name("me", "me", "me");
        name = serDeser(name, Name.class);
        Assert.assertEquals("me", name.getFirst());
        Assert.assertEquals("me", name.getMiddle());
        Assert.assertEquals("me", name.getLast());
        name = new Name("you", null, "you");
        name = serDeser(name, Name.class);
        Assert.assertEquals("you", name.getFirst());
        Assert.assertNull(name.getMiddle());
        Assert.assertEquals("you", name.getLast());
        name = new Name("him", "her", null);
        name = serDeser(name, Name.class);
        Assert.assertEquals("him", name.getFirst());
        Assert.assertEquals("her", name.getMiddle());
        Assert.assertNull(name.getLast());
    }

}
