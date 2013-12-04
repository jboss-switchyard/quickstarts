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
package org.switchyard.quickstarts.soap.addressing;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.soap.Detail;
import javax.xml.soap.SOAPFault;

import org.switchyard.annotations.Transformer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.switchyard.common.xml.XMLHelper;
import org.switchyard.component.soap.util.SOAPUtil;

public class ServiceTransformers {

    @Transformer(from = "java:org.switchyard.quickstarts.soap.addressing.ItemNotAvailable", to = "{urn:switchyard-quickstart:soap-addressing:1.0}ItemNotAvailable")
    public Element transform(ItemNotAvailable exception) throws Exception {
        System.out.println("................................. " + exception);
        ItemNotAvailableBean exceptionBean = new ItemNotAvailableBean();
        exceptionBean.setMessage(exception.getMessage());
        Document detailDoc = XMLHelper.getNewDocument();
        JAXBContext jaxbContext = JAXBContext.newInstance("org.switchyard.quickstarts.soap.addressing");
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.marshal(exceptionBean, detailDoc);
        SOAPFault fault = SOAPUtil.generateSOAP11Fault(exception).getSOAPBody().getFault();
        Detail detail = fault.addDetail();
        detail.appendChild(detailDoc.getDocumentElement());
        fault.detachNode();
        return fault;
    }
}
