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

    @Transformer(from="java:org.switchyard.quickstarts.soap.addressing.ItemNotAvailable", to="{urn:switchyard-quickstart:soap-addressing:1.0}ItemNotAvailable")
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
