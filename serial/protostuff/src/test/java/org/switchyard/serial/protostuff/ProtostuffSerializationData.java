package org.switchyard.serial.protostuff;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public final class ProtostuffSerializationData {

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
            return "Person(name=" + getName() + ", bestFriend=" + (_bestFriend != null ? _bestFriend.getName() : "") + ", relatives=" + getRelatives().size() + ")";
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
