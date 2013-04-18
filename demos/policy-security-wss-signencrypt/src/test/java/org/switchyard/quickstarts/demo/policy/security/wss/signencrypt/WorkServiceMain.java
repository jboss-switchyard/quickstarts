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
package org.switchyard.quickstarts.demo.policy.security.wss.signencrypt;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.httpclient.contrib.ssl.EasySSLProtocolSocketFactory;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.log4j.Logger;
import org.switchyard.common.io.pull.StringPuller;
import org.switchyard.common.lang.Strings;
import org.switchyard.component.test.mixins.http.HTTPMixIn;
import org.switchyard.policy.SecurityPolicy;

public final class WorkServiceMain {

    private static final Logger LOGGER = Logger.getLogger(WorkServiceMain.class);

    private static final String CONFIDENTIALITY = SecurityPolicy.CONFIDENTIALITY.getName();
    private static final String SIGNENCRYPT = "signencrypt";
    private static final String HELP = "help";

    private static final String MAVEN_USAGE = String.format("Maven Usage: mvn exec:java -Dexec.args=\"%s %s %s\"", CONFIDENTIALITY, SIGNENCRYPT, HELP);

    private static void invokeWorkService(String scheme, int port, boolean signencrypt) throws Exception {
        String soapRequest;
        if (signencrypt) {
            soapRequest = new StringPuller().pull("/xml/secure-request.xml");
        } else {
            soapRequest = new StringPuller().pull("/xml/insecure-request.xml").replaceAll("WORK_CMD", "CMD-" + System.currentTimeMillis());
        }
        HTTPMixIn http = new HTTPMixIn();
        http.initialize();
        try {
            String endpoint = String.format("%s://localhost:%s/policy-security-wss-signencrypt/WorkService", scheme, port);
            //LOGGER.info(String.format("Invoking work service at endpoint: %s with request: %s", endpoint, soapRequest));
            LOGGER.info(String.format("Invoking work service at endpoint: %s", endpoint));
            String soapResponse = http.postString(endpoint, soapRequest);
            //LOGGER.info(String.format("Received work service response: %s", soapResponse));
            if (soapResponse.toLowerCase().contains("fault")) {
                throw new Exception("Error invoking work service (check server log)");
            }
        } finally {
            http.uninitialize();
        }
    }

    public static void main(String... args) throws Exception {
        Set<String> policies = new HashSet<String>();
        for (String arg : args) {
            arg = Strings.trimToNull(arg);
            if (arg != null) {
                if (arg.equals(CONFIDENTIALITY) || arg.equals(SIGNENCRYPT) || arg.equals(HELP)) {
                    policies.add(arg);
                } else {
                    LOGGER.error(MAVEN_USAGE);
                    throw new Exception(MAVEN_USAGE);
                }
            }
        }
        if (policies.contains(HELP)) {
            LOGGER.info(MAVEN_USAGE);
        } else {
            final String scheme;
            final int port;
            if (policies.contains(CONFIDENTIALITY)) {
                scheme = "https";
                port = 8443;
                Protocol.registerProtocol("https", new Protocol("https", (ProtocolSocketFactory)(new EasySSLProtocolSocketFactory()), port));
            } else {
                scheme = "http";
                port = 8080;
            }
            boolean signencrypt = policies.contains(SIGNENCRYPT);
            invokeWorkService(scheme, port, signencrypt);
        }
    }

}
