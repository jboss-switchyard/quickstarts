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

package org.switchyard.quickstarts.soap.binding.rpc;

import java.io.IOException;

import org.switchyard.component.test.mixins.http.HTTPMixIn;
import org.switchyard.component.soap.util.SOAPUtil;

/**
 * Client for SOAP binding.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2013 Red Hat Inc.
 */
public class SoapBindingClient {

    private static final String SWITCHYARD_WEB_SERVICE = "http://localhost:8080/soap-binding-rpc/HelloWorldWSService";
    private static String SOAP_TEMPLATE =
              "<soap:Envelope xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns2=\"urn:switchyard-quickstart:soap-binding-rpc:1.0\">"
            + "    <soap:Body>"
            + "        <ns2:sayHello>"
            + "            <toWhom>%s</toWhom>"
            + "            <language>%s</language>"
            + "        </ns2:sayHello>"
            + "    </soap:Body>"
            + "</soap:Envelope>";

    public static void main(String[] args) throws Exception {
        String command =  null;
        if (args.length == 0) {
            System.out.println("Usage: SoapBindingClient <some_name>");
            return;
        } else {
            HTTPMixIn http = new HTTPMixIn();
            http.initialize();
            SOAPUtil.prettyPrint(http.postString(SWITCHYARD_WEB_SERVICE, String.format(SOAP_TEMPLATE, args[0], "English")), System.out);
        }
    }
}
