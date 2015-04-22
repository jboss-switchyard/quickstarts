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
package org.switchyard.quickstarts.soap.attachment;

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
import javax.xml.soap.SOAPMessage;

import org.switchyard.common.type.Classes;
import org.switchyard.component.soap.util.SOAPUtil;

/**
 * Client for SOAP with Attachments.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2013 Red Hat Inc.
 */
public class SoapAttachmentClient {

    public static void main(String[] args) throws Exception {
        String port = System.getProperty("org.switchyard.component.soap.client.port", "8080");
        SOAPMessage response = sendMessage("http://localhost:" + port + "/soap-attachment/ImageServiceService");
        SOAPUtil.prettyPrint(response, System.out);
        @SuppressWarnings("unchecked")
		Iterator<AttachmentPart> iterator = response.getAttachments();
        AttachmentPart ap = iterator.next();
        System.out.println("Response attachment: " + ap.getContentId() + " with content type " + ap.getContentType());
    }

    public static SOAPMessage sendMessage(String switchyard_web_service) throws Exception {
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
        return connection.call(msg, new URL(switchyard_web_service));
    }
}
