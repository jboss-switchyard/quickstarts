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
package org.switchyard.quickstarts.demo.policy.security.wss.signencrypt;

import java.util.HashSet;
import java.util.Set;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
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
                SSLContext sslcontext = SSLContext.getInstance("TLS");
                sslcontext.init(null, null, null);
                SSLSocketFactory sf = new SSLSocketFactory(sslcontext, SSLSocketFactory.STRICT_HOSTNAME_VERIFIER);
                Scheme https = new Scheme(scheme, port, sf);
                SchemeRegistry sr = new SchemeRegistry();
                sr.register(https);
            } else {
                scheme = "http";
                port = 8080;
            }
            boolean signencrypt = policies.contains(SIGNENCRYPT);
            invokeWorkService(scheme, port, signencrypt);
        }
    }

}
