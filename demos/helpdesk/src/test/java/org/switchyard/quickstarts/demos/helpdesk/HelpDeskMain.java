/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
