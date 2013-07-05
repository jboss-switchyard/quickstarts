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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import org.w3c.dom.Element;

/**
 * Data for tests.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public final class ProtostuffSerializationData {

    @SuppressWarnings("serial")
    public static final class Name implements Serializable {
        private String _first;
        private String _middle;
        private String _last;
        public Name() {}
        public Name(String first, String middle, String last) {
            setFirst(first).setMiddle(middle).setLast(last);
        }
        public String getFirst() {
            return _first;
        }
        public Name setFirst(String first) {
            _first = first;
            return this;
        }
        public String getMiddle() {
            return _middle;
        }
        public Name setMiddle(String middle) {
            _middle = middle;
            return this;
        }
        public String getLast() {
            return _last;
        }
        public Name setLast(String last) {
            _last = last;
            return this;
        }
        public String toString() {
            return "Name@" + System.identityHashCode(this) + "(first=" + getFirst() + ", middle=" + getMiddle() + ", last=" + getLast() + ")";
        }
    }

    @SuppressWarnings("serial")
    public static final class Person implements Serializable {
        private String _nickName;
        private Name _fullName;
        private Person _bestFriend;
        private Collection<Person> _relatives = new ArrayList<Person>();
        public Person() {}
        public Person(String nickName) {
            setNickName(nickName);
        }
        public Person(Name fullName) {
            setFullName(fullName);
        }
        public Person(String nickName, Name fullName) {
            setNickName(nickName);
            setFullName(fullName);
        }
        public String getNickName() {
            return _nickName;
        }
        public void setNickName(String nickName) {
            _nickName = nickName;
        }
        public Name getFullName() {
            return _fullName;
        }
        public void setFullName(Name fullName) {
            _fullName = fullName;
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
            return "Person@" + System.identityHashCode(this) + "(nickName=" + getNickName() + ", fullName=" + getFullName() + ", bestFriend=" + (_bestFriend != null ? _bestFriend.getNickName() : "") + ", relatives=" + getRelatives().size() + ")";
        }
    }

    @SuppressWarnings("serial")
    public static final class Car implements Serializable {
        private Person _driver;
        private Person[] _passengers;
        private Title _title;
        private Part[] _cheapParts;
        private Collection<Part> _expensiveParts;
        private Element _inspection;
        private Exception _problem;
        private Antennae _antennae;
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
        public Title getTitle() {
            return _title;
        }
        public void setTitle(Title title) {
            _title = title;
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
        public Element getInspection() {
            return _inspection;
        }
        public void setInspection(Element inspection) {
            _inspection = inspection;
        }
        public Exception getProblem() {
            return _problem;
        }
        public void setProblem(Exception problem) {
            _problem = problem;
        }
        public Antennae getAntennae() {
            return _antennae;
        }
        public void setAntennae(Antennae antennae) {
            _antennae = antennae;
        }
        public String toString() {
            return "Car@" + System.identityHashCode(this) + "(driver=" + getDriver() + ", passengers=" + Arrays.toString(getPassengers()) + ", cheapParts=" + Arrays.toString(getCheapParts()) + ", expensiveParts=" + getExpensiveParts() + ", inspection=" + getInspection() + ", problem=" + getProblem() + ")";
        }
    }

    public static interface Title {
        public UUID getId();
        public String getState();
        public boolean isLiened();
    }

    public static final class StrictTitle implements Title {
        private UUID _id;
        private String _state;
        private boolean _liened;
        public StrictTitle() {}
        public UUID getId() {
            return _id;
        }
        public void setId(UUID id) {
            _id = id;
        }
        public String getState() {
            return _state;
        }
        public void setState(String state) {
            _state = state;
        }
        public boolean isLiened() {
            return _liened;
        }
        public void setLiened(boolean liened) {
            _liened = liened;
        }
        public String toString() {
            return "StrictTitle@" + System.identityHashCode(this) + "(id=" + getId() + ", state=" + getState() + ", liened" + isLiened() + ")";
        }
    }

    public static final class FluentTitle implements Title {
        private UUID _id;
        private String _state;
        private boolean _liened;
        public FluentTitle() {}
        public UUID getId() {
            return _id;
        }
        // purposely returns the implementation type, for the sake of the test
        public FluentTitle setId(UUID id) {
            _id = id;
            return this;
        }
        public String getState() {
            return _state;
        }
        // purposely returns the implementation type, for the sake of the test
        public FluentTitle setState(String state) {
            _state = state;
            return this;
        }
        public boolean isLiened() {
            return _liened;
        }
        // purposely returns the interface type, for the sake of the test
        public Title setLiened(boolean liened) {
            _liened = liened;
            return this;
        }
        public String toString() {
            return "FluentTitle@" + System.identityHashCode(this) + "(id=" + getId() + ", state=" + getState() + ", liened" + isLiened() + ")";
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
            return "CustomPart@" + System.identityHashCode(this) + "(replaceable=" + isReplaceable() + ")";
        }
    }

    public static final class ExpiredPart implements Part {
        private Date _expiredDate;
        public ExpiredPart(Date expiredDate) {
            _expiredDate = expiredDate;
        }
        public boolean isReplaceable() {
            return false;
        }
        public Date getExpiredDate() {
            return _expiredDate;
        }
        public String toString() {
            return "BadPart@" + System.identityHashCode(this) + "(replaceable=" + isReplaceable() + ", expiredDate=" + getExpiredDate() + ")";
        }
    }

    public static final class Antennae {
        public String toString() {
            return "Antennae@" + System.identityHashCode(this);
        }
    }

    private ProtostuffSerializationData() {}

}
