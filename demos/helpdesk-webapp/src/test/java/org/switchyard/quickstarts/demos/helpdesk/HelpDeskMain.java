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

import java.io.File;
import java.io.FileWriter;

import org.apache.log4j.Logger;
import org.switchyard.common.io.pull.StringPuller;
import org.switchyard.common.lang.Strings;
import org.switchyard.test.mixins.BPMMixIn;
import org.switchyard.test.mixins.HTTPMixIn;

/**
 * HelpDeskMain.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2012 Red Hat Inc.
 */
public final class HelpDeskMain {

    private static final Logger LOGGER = Logger.getLogger(HelpDeskMain.class);

    private static final String CMD_START_TASKSERVER = "start.taskserver";
    private static final String CMD_START_PROCESS = "start.process";
    private static final String CMD_STOP_TASKSERVER = "stop.taskserver";

    private static final File FILE_TARGET_DIRECTORY = new File("target");
    private static final File FILE_TASKSERVER_RM2STOP = new File(FILE_TARGET_DIRECTORY, "taskserver.rm2stop");

    private static final String MSG_TASKSERVER_RM2STOP = String.format(
            "********** IMPORTANT: To CLEANLY stop the TaskServer, in another window either run 'mvn exec:java -Dexec.args=%s' or simply delete the %s file. Do NOT use '<Ctrl>-C'! **********",
            CMD_STOP_TASKSERVER, FILE_TASKSERVER_RM2STOP.getAbsolutePath() );
    private static final String MSG_MAVEN_USAGE = String.format(
            "Maven Usage: mvn exec:java -Dexec.args=\"%s|%s|%s\"",
            CMD_START_TASKSERVER, CMD_START_PROCESS, CMD_STOP_TASKSERVER );

    private static void startTaskServer() throws Exception {
        final BPMMixIn bpm = new BPMMixIn(false);
        bpm.startTaskServer();
        try {
            LOGGER.info(MSG_TASKSERVER_RM2STOP);
            if (!FILE_TASKSERVER_RM2STOP.exists()) {
                if (!FILE_TARGET_DIRECTORY.exists()) {
                    FILE_TARGET_DIRECTORY.mkdir();
                }
                FileWriter writer = new FileWriter(FILE_TASKSERVER_RM2STOP);
                writer.write(MSG_TASKSERVER_RM2STOP + "\n");
                writer.close();
            }
            while (FILE_TASKSERVER_RM2STOP.exists()) {
                Thread.sleep(1000);
            }
        } finally {
            bpm.stopTaskServer();
        }
    }

    private static void startProcess() throws Exception {
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

    static void stopTaskServer() throws Exception {
        if (FILE_TASKSERVER_RM2STOP.exists()) {
            FILE_TASKSERVER_RM2STOP.delete();
        }
    }

    public static void main(String... args) throws Exception {
        String cmd = args.length != 0 ? Strings.trimToNull(args[0]) : null;
        if (cmd != null) {
            if (cmd.equals(CMD_START_TASKSERVER)) {
                startTaskServer();
            } else if (cmd.equals(CMD_START_PROCESS)) {
                startProcess();
            } else if (cmd.equals(CMD_STOP_TASKSERVER)) {
                stopTaskServer();
            } else {
                LOGGER.error(MSG_MAVEN_USAGE);
                throw new Exception(MSG_MAVEN_USAGE);
            }
        } else {
            LOGGER.info(MSG_MAVEN_USAGE);
        }
    }

}
