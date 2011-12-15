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
package org.switchyard.test.mixins;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.drools.SystemEventListenerFactory;
import org.jbpm.task.Status;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.mina.MinaTaskClientConnector;
import org.jbpm.task.service.mina.MinaTaskClientHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskOperationResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskSummaryResponseHandler;
import org.switchyard.common.io.pull.PropertiesPuller;
import org.switchyard.common.lang.Strings;
import org.switchyard.common.type.Classes;
import org.switchyard.common.type.reflect.Construction;
import org.switchyard.exception.SwitchYardException;

/**
 * BPM Test Mix In.
 * Helps with <a href="http://docs.jboss.org/jbpm/v5.2/userguide/ch.Human_Tasks.html">jBPM 5 Human Tasks</a>.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class BPMMixIn extends AbstractTestMixIn {

    private static final Logger LOGGER = Logger.getLogger(BPMMixIn.class);

    private final boolean _managedLifecycle;
    private Object _serverController = null;
    private TaskClient _client = null;
    private boolean _connected = false;
    private String _host = "127.0.0.1";
    private int _port = 9123;
    private String _rolesPath = "/roles.properties";

    /**
     * Public default constructor.
     * <p/>
     * Manages the TaskServer and TaskClient connection lifecycle.
     */
    public BPMMixIn() {
        this(true);
    }

    /**
     * Public constructor.
     * @param managedLifecycle Do you want the SwitchYard test kit to manage the start/stop lifecycle of the BPM TaskServer and TaskClient.
     */
    public BPMMixIn(boolean managedLifecycle) {
        _managedLifecycle = managedLifecycle;
    }

    /**
     * Gets the host.
     * @return the host
     */
    public String getHost() {
        return _host;
    }

    /**
     * Sets the host.
     * @param host the host
     * @return this instance
     */
    public BPMMixIn setHost(String host) {
        _host = host;
        return this;
    }

    /**
     * Gets the port.
     * @return the port
     */
    public int getPort() {
        return _port;
    }

    /**
     * Sets the port.
     * @param port the port
     * @return this instance
     */
    public BPMMixIn setPort(int port) {
        _port = port;
        return this;
    }

    /**
     * The connected status.
     * @return true if connected
     */
    public boolean isConnected() {
        return _connected;
    }

    /**
     * Gets the roles path.
     * @return the roles path
     */
    public String getRolesPath() {
        return _rolesPath;
    }

    /**
     * Sets the roles path.
     * @param rolesPath the roles path
     * @return this instance
     */
    public BPMMixIn setRolesPath(String rolesPath) {
        _rolesPath = rolesPath;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize() {
        if (_managedLifecycle) {
            startTaskServer();
            startTaskClient();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void uninitialize() {
        if (_managedLifecycle) {
            stopTaskClient();
            stopTaskServer();
        }
    }

    /**
     * Starts the task server.
     */
    public void startTaskServer() {
        Class<?> type = Classes.forName("org.switchyard.component.bpm.task.jbpm.TaskServerController", BPMMixIn.class);
        _serverController = Construction.construct(type, new Class<?>[] {String.class, int.class}, new Object[] {getHost(), getPort()});
        try {
            type.getMethod("start").invoke(_serverController);
        } catch (Throwable t) {
            throw new SwitchYardException(t);
        }
    }

    /**
     * Stops the task server.
     */
    public void stopTaskServer() {
        if (_serverController != null) {
            try {
                _serverController.getClass().getMethod("stop").invoke(_serverController);
            } catch (Throwable t) {
                throw new SwitchYardException(t);
            }
        }
    }

    /**
     * Starts the task client.
     */
    public void startTaskClient() {
        if (_client == null) {
            _client = new TaskClient(new MinaTaskClientConnector(BPMMixIn.class.getSimpleName(), new MinaTaskClientHandler(SystemEventListenerFactory.getSystemEventListener())));
        }
        if (!_connected) {
            LOGGER.info(String.format("Connecting jBPM TaskClient to %s:%s...", getHost(), getPort()));
            _connected = _client.connect(getHost(), getPort());
            if (_connected) {
                LOGGER.info(String.format("jBPM TaskClient connected to %s:%s.", getHost(), getPort()));
            } else {
                LOGGER.error(String.format("jBPM TaskClient could not connect to %s:%s!", getHost(), getPort()));
            }
        }
    }

    /**
     * Stops the task client.
     */
    public void stopTaskClient() {
        if (_client != null) {
            if (_connected) {
                LOGGER.info(String.format("Disconnecting jBPM TaskClient from %s:%s...", getHost(), getPort()));
                try {
                    _client.disconnect();
                } catch (Throwable t) {
                    // just to keep checkstyle happy ("Must have at least one statement.")
                    t.getMessage();
                }
                _connected = false;
                LOGGER.info(String.format("jBPM TaskClient disconnected from %s:%s.", getHost(), getPort()));
            }
            _client = null;
        }
    }

    /**
     * Gets the users to roles mapping.
     * @return the users to roles mapping.
     */
    public Map<String,List<String>> getUsersRoles() {
        return getUsersRoles(getRolesPath());
    }

    /**
     * Gets the users to roles mapping.
     * @param rolesPath the roles path
     * @return the users to roles mapping
     */
    public Map<String,List<String>> getUsersRoles(String rolesPath) {
        Map<String,List<String>> usersRolesMap = new HashMap<String,List<String>>();
        Properties usersRolesProps;
        try {
            usersRolesProps = new PropertiesPuller().pull(rolesPath, BPMMixIn.class);
        } catch (IOException ioe) {
            throw new SwitchYardException(ioe);
        }
        for (Object userKey : usersRolesProps.keySet()) {
            String user = (String)userKey;
            if (!"Administrator".equals(user)) {
                List<String> roles = Strings.splitTrimToNull(usersRolesProps.getProperty(user), ",");
                usersRolesMap.put(user, roles);
            }
        }
        return usersRolesMap;
    }

    /**
     * Complete all human tasks.
     */
    public void completeHumanTasks() {
        completeHumanTasks(getUsersRoles());
    }

    /**
     * Complete all human tasks.
     * @param rolesPath the roles path
     */
    public void completeHumanTasks(String rolesPath) {
        completeHumanTasks(getUsersRoles(rolesPath));
    }

    /**
     * Complete all human tasks.
     * @param usersRoles the roles mapping
     */
    public void completeHumanTasks(Map<String,List<String>> usersRoles) {
        boolean keepWorking;
        do {
            keepWorking = doCompleteHumanTasks(usersRoles);
        } while (keepWorking);
    }

    private boolean doCompleteHumanTasks(Map<String,List<String>> usersRoles) {
        boolean keepWorking = false;
        for (String userId : usersRoles.keySet()) {
            BlockingTaskSummaryResponseHandler btsrh =  new BlockingTaskSummaryResponseHandler();
            List<String> groupIds = usersRoles.get(userId);
            _client.getTasksAssignedAsPotentialOwner(userId, groupIds, "en-UK", btsrh);
            List<TaskSummary> tasks = btsrh.getResults();
            int size = tasks.size();
            LOGGER.info("Found " + size + " task" + (size == 1 ? "" : "s") + " for " + userId + ".");
            for (TaskSummary task : tasks) {
                if (Status.Completed.equals(task.getStatus())) {
                    continue;
                }
                BlockingTaskOperationResponseHandler btorh = new BlockingTaskOperationResponseHandler();
                _client.claim(task.getId(), userId, groupIds, btorh);
                btorh.waitTillDone(10000);
                LOGGER.info("Task \"" + task.getName() + "\" claimed by " + userId + ".");
                btorh = new BlockingTaskOperationResponseHandler();
                _client.start(task.getId(), userId, btorh);
                btorh.waitTillDone(10000);
                LOGGER.info("Task \"" + task.getName() + "\" started by " + userId + ".");
                btorh = new BlockingTaskOperationResponseHandler();
                _client.complete(task.getId(), userId, null, btorh);
                btorh.waitTillDone(10000);
                LOGGER.info("Task \"" + task.getName() + "\" completed by " + userId + ".");
                keepWorking = true;
            }
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ie) {
            throw new SwitchYardException(ie);
        }
        return keepWorking;
    }

}
