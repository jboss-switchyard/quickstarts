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

package org.switchyard.quickstarts.soap.attachment;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import javax.activation.DataHandler;
import javax.activation.URLDataSource;
import javax.xml.namespace.QName;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;

import org.switchyard.common.type.Classes;
import org.switchyard.component.soap.util.SOAPUtil;

/**
 * Client for SOAP with Attachments.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2013 Red Hat Inc.
 */
public class SoapAttachmentClient {

    private static final String SWITCHYARD_WEB_SERVICE = "http://localhost:8080/soap-attachment/ImageServiceService";

    public static void main(String[] args) throws Exception {
        String command =  null;
        SOAPMessage response = sendMessage();
        SOAPUtil.prettyPrint(response, System.out);
        Iterator<AttachmentPart> iterator = response.getAttachments();
        AttachmentPart ap = iterator.next();
        System.out.println("Response attachment: " + ap.getContentId() + " with content type " + ap.getContentType());
    }

    public static SOAPMessage sendMessage() throws Exception {
        SOAPConnectionFactory conFactory = SOAPConnectionFactory.newInstance();

        SOAPConnection connection = conFactory.createConnection();
        MessageFactory msgFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
        SOAPMessage msg = msgFactory.createMessage();
        SOAPBodyElement bodyElement = msg.getSOAPBody().addBodyElement(new QName("urn:switchyard-quickstart:soap-attachment:1.0", "echoImage"));
        bodyElement.addTextNode("cid:switchyard.png");

        AttachmentPart ap = msg.createAttachmentPart();
        URL imageUrl = Classes.getResource("switchyard.png");
        ap.setDataHandler(new DataHandler(new URLDataSource(imageUrl)));
        ap.setContentId("switchyard.png");
        msg.addAttachmentPart(ap);
        return connection.call(msg, new URL(SWITCHYARD_WEB_SERVICE));
    }
}
