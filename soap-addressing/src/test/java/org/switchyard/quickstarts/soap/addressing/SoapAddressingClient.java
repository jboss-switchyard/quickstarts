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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.switchyard.component.test.mixins.http.HTTPMixIn;
import org.switchyard.component.soap.util.SOAPUtil;

/**
 * Client for SOAP with WS-Addressing.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2013 Red Hat Inc.
 */
public class SoapAddressingClient {

    private static final String SWITCHYARD_WEB_SERVICE = "http://localhost:8080/soap-addressing/order/OrderService";

    private static String SOAP_TEMPLATE =
         "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\""
        + "    xmlns:urn=\"urn:switchyard-quickstart:soap-addressing:1.0\""
        + "    xmlns:wsa=\"http://www.w3.org/2005/08/addressing\">"
        + "    <soapenv:Header>"
        + "        <wsa:MessageID>uuid:3d3fcbbb-fd43-4118-b40e-62577894f39a</wsa:MessageID>"
        + "        <wsa:Action>urn:switchyard-quickstart:soap-addressing:1.0:OrderService:orderRequest</wsa:Action>"
        + "    </soapenv:Header>"
        + "    <soapenv:Body>"
        + "        <urn:order>"
        + "            <item>%s</item>"
        + "            <quantity>%s</quantity>"
        + "        </urn:order>"
        + "    </soapenv:Body>"
        + "</soapenv:Envelope>";

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: SoapAddressingClient <item> <quantity>");
            return;
        } else {
            SOAPUtil.prettyPrint(sendMessage(args[0], args[1]), System.out);
            System.out.println();
            System.out.println(getFileMessage());
        }
    }

    public static String sendMessage(String item, String quantity) throws Exception {
        File testFile = getFile();
        if (testFile.exists()) {
            testFile.delete();
        }
        String command =  null;
        HTTPMixIn http = new HTTPMixIn();
        http.initialize();
        return http.postString(SWITCHYARD_WEB_SERVICE, String.format(SOAP_TEMPLATE, item, quantity));
    }

    public static String getFileMessage() throws Exception {
        File testFile = getFile();
        // Wait for the actual response from WarehouseService
        int timeout = 0;
        while (!testFile.exists()) {
            Thread.sleep(100);
            timeout += 100;
            if (timeout == 5000) {
                break;
            }
        }
        BufferedReader stream = new BufferedReader(new FileReader(testFile));
        String text = stream.readLine();
        stream.close();
        return text;
    }

    private static File getFile() {
        String outputFile = "/tmp" + File.separator + "test.txt";
        return new File(outputFile);
    }
}
