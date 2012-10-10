package org.switchyard.serial.jackson;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.switchyard.serial.jackson.JacksonSerializationData.Car.CarMapper;
import org.switchyard.serial.jackson.JacksonSerializationData.Person.PersonMapper;
import org.switchyard.serial.map.Mapper;

public final class JacksonSerializationData {

    @SuppressWarnings("serial")
    @org.switchyard.serial.map.Mappable(PersonMapper.class)
    public static final class Person implements Serializable {
        private String _name;
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
        public static final class PersonMapper implements Mapper<Person> {
            @Override
            public Map<String, Object> toMap(Person obj) {
                Map<String, Object> map = new LinkedHashMap<String, Object>();
                map.put("name", obj._name);
                return map;
            }
            @Override
            public Person toObject(Map<String, Object> map) {
                Person obj = new Person();
                obj._name = (String)map.get("name");
                return obj;
            }
        }
    }

    @SuppressWarnings("serial")
    @org.switchyard.serial.map.Mappable(CarMapper.class)
    public static final class Car implements Serializable {
        private Person _driver;
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
        public static final class CarMapper implements Mapper<Car> {
            private final PersonMapper _pm = new PersonMapper();
            @Override
            public Map<String, Object> toMap(Car obj) {
                Map<String, Object> map = new LinkedHashMap<String, Object>();
                map.put("driver", _pm.toMap(obj._driver));
                return map;
            }
            @Override
            public Car toObject(Map<String, Object> map) {
                Car obj = new Car();
                @SuppressWarnings("unchecked")
                Map<String, Object> dm = (Map<String, Object>)map.get("driver");
                if (dm != null) {
                    obj._driver = _pm.toObject(dm);
                }
                return obj;
            }
        }
    }

}
