/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.switchyard.test.quickstarts;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Iterator;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.jws.WebService;
import javax.xml.namespace.QName;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPBodyElement;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPMessage;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.component.test.mixins.http.HTTPMixIn;
import org.switchyard.test.ArquillianUtil;

/*
 *  Huge attachment test without any heap space error.
 */
@RunWith(Arquillian.class)
public class SoapAttachmentQuickstartTest {

    private static final String SWITCHYARD_WEB_SERVICE = "http://localhost:8080/soap-attachment/ImageServiceService";

    static int size = 13000000;

    @Deployment(testable = false)
    public static JavaArchive createDeployment() {
        return ArquillianUtil.createJarQSDeployment("switchyard-soap-attachment");
    }

    @Test
    public void imageService() throws Exception {
        SOAPMessage response = sendMessage();
        Assert.assertTrue(response.getAttachments().hasNext());
    }

    public SOAPMessage sendMessage() throws Exception {
        SOAPConnectionFactory conFactory = SOAPConnectionFactory.newInstance();

        SOAPConnection connection = conFactory.createConnection();
        MessageFactory msgFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);
        SOAPMessage msg = msgFactory.createMessage();
        SOAPBodyElement bodyElement = msg.getSOAPBody().addBodyElement(new QName("urn:switchyard-quickstart:soap-attachment:1.0", "echoImage"));
        bodyElement.addTextNode("cid:switchyard.png");

        // CXF does not set content-type.
        msg.getMimeHeaders().addHeader("Content-Type", "multipart/related; type=\"text/xml\"; start=\"<root.message@cxf.apache.org>\"");
        msg.getSOAPPart().setContentId("<root.message@cxf.apache.org>");

        AttachmentPart ap = msg.createAttachmentPart();
        ap.setDataHandler(new DataHandler(new StreamDataSource()));
        ap.setContentId("<switchyard.png>");
        msg.addAttachmentPart(ap);

        return connection.call(msg, new URL(SWITCHYARD_WEB_SERVICE));
    }

    private static class StreamDataSource implements DataSource {

        public InputStream getInputStream() throws IOException {
            return new InputStream() {
                int count;

                @Override
                public int read() throws IOException {
                    if (count < size) {
                        count++;
                        return 'M';
                    } else {
                        return -1;
                    }
                }
            };
        }

        public OutputStream getOutputStream() throws IOException {
            return null;
        }

        public String getContentType() {
            return "image/png";
        }

        public String getName() {
            return "junk";
        }
    }
}
