/*
 * 2012 Red Hat Inc. and/or its affiliates and other contributors.
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
