/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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

import java.io.File;
import java.io.FileWriter;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.common.io.pull.StringPuller;
import org.switchyard.component.bean.config.model.BeanSwitchYardScanner;
import org.switchyard.component.bpm.config.model.BPMSwitchYardScanner;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.mixins.BPMMixIn;
import org.switchyard.test.mixins.CDIMixIn;
import org.switchyard.test.mixins.HTTPMixIn;
import org.switchyard.transform.config.model.TransformSwitchYardScanner;

/**
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(
    config = SwitchYardTestCaseConfig.SWITCHYARD_XML,
    scanners = {BeanSwitchYardScanner.class, BPMSwitchYardScanner.class, TransformSwitchYardScanner.class},
    mixins = {CDIMixIn.class, HTTPMixIn.class},
    exclude = {"soap"}
)
public class HelpDeskTests {

    private static final Logger LOGGER = Logger.getLogger(HelpDeskTests.class);

    private static final String TASKSERVER = "taskserver";
    private static final String PROCESS = "process";
    private static final String START = "start";
    private static final String STOP = "stop";
    private static final File FILE = new File(TASKSERVER + ".rm2" + STOP);
    private static final String MSG = String.format(
            "********** IMPORTANT: To CLEANLY stop the TaskServer, in another window either run 'mvn test -D%s=%s' or simply delete the %s file. Do NOT use '<Ctrl>-C'! **********",
            TASKSERVER, STOP, FILE.getAbsolutePath() );

    private HTTPMixIn http;

    @Test
    public void startTaskServer() throws Exception {
        if (START.equals(System.getProperty(TASKSERVER))) {
            final BPMMixIn bpm = new BPMMixIn(false);
            bpm.startTaskServer();
            try {
                LOGGER.info(MSG);
                if (!FILE.exists()) {
                    FileWriter writer = new FileWriter(FILE);
                    writer.write(MSG + "\n");
                    writer.close();
                }
                while (FILE.exists()) {
                    Thread.sleep(1000);
                }
            } finally {
                bpm.stopTaskServer();
            }
        }
    }

    @Test
    public void startProcess() throws Exception {
        if (START.equals(System.getProperty(PROCESS))) {
            String ticketId = "TCKT-" + System.currentTimeMillis();
            String soapRequest = new StringPuller().pull("/xml/soap-request.xml").replaceAll("TICKET_ID", ticketId);
            http.postString("http://localhost:18001/HelpDeskService", soapRequest);
            LOGGER.info("Started helpdesk process with ticket id: " + ticketId);
        }
    }

    @Test
    public void stopTaskServer() throws Exception {
        if (STOP.equals(System.getProperty(TASKSERVER))) {
            if (FILE.exists()) {
                FILE.delete();
            }
        }
    }

}
