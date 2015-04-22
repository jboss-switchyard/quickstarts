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
