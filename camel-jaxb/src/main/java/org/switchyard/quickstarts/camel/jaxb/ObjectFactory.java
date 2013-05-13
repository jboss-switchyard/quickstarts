/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.quickstarts.camel.jaxb;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * Object factory for jaxb.
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName REQUEST_QNAME = new QName("urn:switchyard-quickstart:camel-jaxb:1.0", "request");
    private final static QName RESPONSE_QNAME = new QName("urn:switchyard-quickstart:camel-jaxb:1.0", "response");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes.
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GreetingRequest}.
     * 
     * @return Request.
     */
    public GreetingRequest createGreetingRequest() {
        return new GreetingRequest();
    }

    /**
     * Create an instance of {@link JAXBElement} wrapper for {@link GreetingRequest}.
     * 
     * @param value Request.
     * @return JAXB element.
     */
    @XmlElementDecl(namespace = "urn:switchyard-quickstart:camel-jaxb:1.0", name = "request")
    public JAXBElement<GreetingRequest> createOrder(GreetingRequest value) {
        return new JAXBElement<GreetingRequest>(REQUEST_QNAME, GreetingRequest.class, null, value);
    }

    /**
     * Create an instance of {@link GreetingResponse}.
     * 
     * @return Response.
     */
    public GreetingResponse createGreetingResponse() {
        return new GreetingResponse();
    }

    /**
     * Create an instance of {@link JAXBElement} wrapper for {@link GreetingResponse}.
     * 
     * @param value Response.
     * @return JAXB element.
     */
    @XmlElementDecl(namespace = "urn:switchyard-quickstart:camel-jaxb:1.0", name = "response")
    public JAXBElement<GreetingResponse> createOrder(GreetingResponse value) {
        return new JAXBElement<GreetingResponse>(RESPONSE_QNAME, GreetingResponse.class, null, value);
    }

}
