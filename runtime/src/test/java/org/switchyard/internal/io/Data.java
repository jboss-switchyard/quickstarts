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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * I/O test data.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public final class Data {

    private Data() {}

    @SuppressWarnings("serial")
    public static final class Person implements Serializable {
        private String _name;
        private Person _bestFriend;
        private Collection<Person> _relatives = new ArrayList<Person>();
        public Person() {}
        public Person(String name) {
            setName(name);
        }
        public String getName() {
            return _name;
        }
        public void setName(String name) {
            _name = name;
        }
        public Person getBestFriend() {
            return _bestFriend;
        }
        public void setBestFriend(Person bestFriend) {
            _bestFriend = bestFriend;
        }
        public Collection<Person> getRelatives() {
            return _relatives;
        }
        public void setRelatives(Collection<Person> relatives) {
            _relatives = relatives;
        }
        public String toString() {
            return "Person(name=" + getName() + ", bestFriend=" + getBestFriend() + ", relatives=" + getRelatives() + ")";
        }
    }

    @SuppressWarnings("serial")
    public static final class Car implements Serializable {
        private Person _driver;
        private Person[] _passengers;
        private Part[] _cheapParts;
        private Collection<Part> _expensiveParts;
        public Car() {}
        public Car(Person driver) {
            setDriver(driver);
        }
        public Person getDriver() {
            return _driver;
        }
        public void setDriver(Person driver) {
            _driver = driver;
        }
        public Person[] getPassengers() {
            return _passengers;
        }
        public void setPassengers(Person[] passengers) {
            _passengers = passengers;
        }
        public Part[] getCheapParts() {
            return _cheapParts;
        }
        public void setCheapParts(Part[] cheapParts) {
            _cheapParts = cheapParts;
        }
        public Collection<Part> getExpensiveParts() {
            return _expensiveParts;
        }
        public void setExpensiveParts(Collection<Part> expensiveParts) {
            _expensiveParts = expensiveParts;
        }
        public String toString() {
            return "Car(driver=" + getDriver() + ", passengers=" + Arrays.toString(getPassengers()) + ", cheapParts=" + Arrays.toString(getCheapParts()) + ", expensiveParts=" + getExpensiveParts() + ")";
        }
    }

    public static interface Part {
        public boolean isReplaceable();
    }

    public static final class Wheel implements Part {
        public boolean isReplaceable() {
            return true;
        }
        public String toString() {
            return "Wheel(replaceable=" + isReplaceable() + ")";
        }
    }

    public static final class CustomPart implements Part {
        private boolean _replaceable;
        public CustomPart() {}
        public CustomPart(boolean replaceable) {
            setReplaceable(replaceable);
        }
        public boolean isReplaceable() {
            return _replaceable;
        }
        public void setReplaceable(boolean replaceable) {
            _replaceable = replaceable;
        }
        public String toString() {
            return "CustomPart(replaceable=" + isReplaceable() + ")";
        }

    }

}
