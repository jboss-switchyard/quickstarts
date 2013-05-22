/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2010-2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more 
 * details. You should have received a copy of the GNU Lesser General Public 
 * License, v.2.1 along with this distribution; if not, write to the Free 
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  
 * 02110-1301, USA.
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
