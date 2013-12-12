/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
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
package org.switchyard.quickstarts.jca.inflow;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the quickstart.switchyard.transform_jaxb._1 package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName PERSON_QNAME = new QName("urn:switchyard-quickstart:jca-inflow-activemq:0.1.0", "person");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: quickstart.switchyard.jca_inflow_activemq._1.
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Person }.
     * 
     * @return Person
     */
    public Person createPerson() {
        return new Person();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Person }{@code >}}.
     * 
     * @param value Person
     * @return Person JAXBElement
     */
    @XmlElementDecl(namespace = "urn:switchyard-quickstart:jca-inflow-activemq:0.1.0", name = "person")
    public JAXBElement<Person> createPerson(Person value) {
        return new JAXBElement<Person>(PERSON_QNAME, Person.class, null, value);
    }

}
