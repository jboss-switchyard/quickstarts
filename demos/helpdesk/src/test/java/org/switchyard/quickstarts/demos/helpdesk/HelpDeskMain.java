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
package org.switchyard.quickstarts.demos.helpdesk;

import org.apache.log4j.Logger;
import org.switchyard.common.io.pull.StringPuller;
import org.switchyard.component.test.mixins.http.HTTPMixIn;

/**
 * HelpDeskMain.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public final class HelpDeskMain {

    private static final Logger LOGGER = Logger.getLogger(HelpDeskMain.class);

    public static void main(String... args) throws Exception {
        final String ticketId = "TCKT-" + System.currentTimeMillis();
        final String soapRequest = new StringPuller().pull("/xml/soap-request.xml").replaceAll("TICKET_ID", ticketId);
        final HTTPMixIn http = new HTTPMixIn();
        http.initialize();
        try {
            http.postString("http://localhost:8080/HelpDeskService/HelpDeskService", soapRequest);
            LOGGER.info("Started helpdesk process with ticket id: " + ticketId);
        } finally {
            http.uninitialize();
        }
    }

}
