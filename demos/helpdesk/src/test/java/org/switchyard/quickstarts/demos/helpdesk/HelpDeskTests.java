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

import java.net.Socket;
import java.net.SocketException;
import java.util.List;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.drools.agent.impl.DoNothingSystemEventListener;
import org.jbpm.task.Status;
import org.jbpm.task.User;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.TaskServer;
import org.jbpm.task.service.TaskService;
import org.jbpm.task.service.TaskServiceSession;
import org.jbpm.task.service.mina.MinaTaskClientConnector;
import org.jbpm.task.service.mina.MinaTaskClientHandler;
import org.jbpm.task.service.mina.MinaTaskServer;
import org.jbpm.task.service.responsehandlers.BlockingTaskOperationResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskSummaryResponseHandler;
import org.junit.Test;
import org.switchyard.component.bean.config.model.BeanSwitchYardScanner;
import org.switchyard.component.bpm.config.model.BpmSwitchYardScanner;
import org.switchyard.test.SwitchYardTestCase;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.mixins.CDIMixIn;
import org.switchyard.test.mixins.HTTPMixIn;
import org.switchyard.transform.config.model.TransformSwitchYardScanner;

/**
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
@SwitchYardTestCaseConfig(
    config = SwitchYardTestCaseConfig.SWITCHYARD_XML,
    scanners = {BeanSwitchYardScanner.class, BpmSwitchYardScanner.class, TransformSwitchYardScanner.class},
    mixins = {CDIMixIn.class, HTTPMixIn.class})
public class HelpDeskTests extends SwitchYardTestCase {

    private static final String HOST = "127.0.0.1";
    private static final int PORT = 9123;
    private static final String ADMINISTRATOR = "Administrator";
    private static final String DEVELOPER = "Developer";
    private static final String USER = "User";

    // http://docs.jboss.org/jbpm/v5.0/userguide/ch.Human_Tasks.html#d0e2349
    private TaskServer _server = null;
    private TaskClient _client = null;
    private boolean _clientConnected = false;

    @Test
    public void testHelpDesk() throws Exception {
        if (startTaskServer()) {
            try {
                getMixIn(HTTPMixIn.class).postResourceAndTestXML("http://localhost:18001/HelpDeskService", "/xml/soap-request.xml", "/xml/soap-response.xml");
                boolean keepWorking = true;
                while (keepWorking) {
                    keepWorking = completeTasksForUsers(DEVELOPER, USER);
                }
            } finally {
                stopTaskServer();
            }
        }
    }

    private boolean startTaskServer() {
        try {
            boolean ready = false;
            while (!ready) {
                Socket socket = null;
                try {
                    socket = new Socket(HOST, PORT);
                    if (socket.isConnected()) {
                        Thread.sleep(1000);
                    } else {
                        ready = true;
                    }
                } catch (SocketException se) {
                    ready = true;
                } finally {
                    if (socket != null) {
                        socket.close();
                        socket = null;
                    }
                }
            }
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.jbpm.task");
            TaskService taskService = new TaskService(emf, new DoNothingSystemEventListener());
            TaskServiceSession taskServiceSession = taskService.createSession();
            for (String id : new String[]{ADMINISTRATOR, DEVELOPER, USER}) {
                taskServiceSession.addUser(new User(id));
            }
            taskServiceSession.dispose();
            _server = new MinaTaskServer(taskService, PORT, HOST);
            new Thread(_server).start();
            //_server.start();
            return true;
        } catch (Throwable t) {
            System.err.println("********** Could not start task server: " + t.getMessage() + " **********");
            return false;
        }
    }

    private boolean completeTasksForUsers(String... userIds) throws Exception {
        boolean keepWorking = false;
        if (_client == null) {
            _client = new TaskClient(new MinaTaskClientConnector("client", new MinaTaskClientHandler(new DoNothingSystemEventListener())));
        }
        _client.connect(HOST, PORT);
        _clientConnected = true;
        for (String userId : userIds) {
            BlockingTaskSummaryResponseHandler btsrh =  new BlockingTaskSummaryResponseHandler();
            _client.getTasksOwned(userId, "en-UK", btsrh);
            List<TaskSummary> tasks = btsrh.getResults();
            for (TaskSummary task : tasks) {
                if (Status.Completed.equals(task.getStatus())) {
                    continue;
                }
                User user = task.getActualOwner();
                BlockingTaskOperationResponseHandler btorh = new BlockingTaskOperationResponseHandler();
                _client.start(task.getId(), user.getId(), btorh);
                btorh.waitTillDone(10000);
                btorh = new BlockingTaskOperationResponseHandler();
                _client.complete(task.getId(), user.getId(), null, btorh);
                btorh.waitTillDone(10000);
                keepWorking = true;
            }
        }
        _client.disconnect();
        _clientConnected = false;
        Thread.sleep(1000);
        return keepWorking;
    }

    private void stopTaskServer() {
        if (_client != null) {
            if (_clientConnected) {
                try {
                    _client.disconnect();
                } catch (Throwable t) {}
                _clientConnected = false;
            }
            _client = null;
        }
        if (_server != null) {
            try {
                _server.stop();
            } catch (Throwable t) {}
            _server = null;
        }
    }

}
