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
package org.switchyard.quickstarts.camel.soap.proxy;

import static javax.jws.soap.SOAPBinding.Style.DOCUMENT;
import static javax.jws.soap.SOAPBinding.Use.LITERAL;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import javax.xml.namespace.QName;
import javax.xml.soap.Detail;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPFactory;
import javax.xml.soap.SOAPFault;
import javax.xml.ws.soap.SOAPFaultException;

/**
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
@Produces(MediaType.APPLICATION_XML)
@WebService(name = "ReverseService", serviceName = "ReverseService", targetNamespace = "urn:switchyard-quickstart:camel-soap-proxy:1.0")
@SOAPBinding(style = DOCUMENT, use = LITERAL)
public class ReverseService {

    @POST
    @Path("/")
    @WebMethod(action = "urn:switchyard-quickstart:camel-soap-proxy:1.0")
    @WebResult(name = "text")
    public String reverse(@WebParam(name = "text") String text) throws Exception {
        if (text.equals("fault")) {
            SOAPFactory factory = SOAPFactory.newInstance();
            SOAPFault sf = factory.createFault("myfaultstring",  new QName(SOAPConstants.URI_NS_SOAP_ENVELOPE, "Server"));
            sf.setFaultActor("myFaultActor");
            Detail d = sf.addDetail();
            QName entryName = new QName("urn:switchyard-quickstart:camel-soap-proxy:1.0", "order", "PO");
            DetailEntry entry = d.addDetailEntry(entryName);
            QName name = new QName("urn:switchyard-quickstart:camel-soap-proxy:1.0", "symbol");
            SOAPElement symbol = entry.addChildElement(name);
            symbol.addTextNode("SUNW");
            throw new SOAPFaultException(sf);
        }
        return new StringBuilder(text).reverse().toString();
    }

    @POST
    @Path("/")
    @WebMethod(action = "urn:switchyard-quickstart:camel-soap-proxy:1.0")
    @WebResult(name = "text")
    public String upper(@WebParam(name = "text") String text) {
        return text.toUpperCase();
    }

}
