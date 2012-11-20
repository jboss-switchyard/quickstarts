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
package org.switchyard.component.test.mixins.bpm;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.switchyard.common.io.pull.PropertiesPuller;
import org.switchyard.common.lang.Strings;
import org.switchyard.component.bpm.task.Task;
import org.switchyard.component.bpm.task.TaskClient;
import org.switchyard.component.bpm.task.TaskServer;
import org.switchyard.component.bpm.task.TaskService;
import org.switchyard.component.bpm.task.TaskStatus;
import org.switchyard.exception.SwitchYardException;
import org.switchyard.test.mixins.AbstractTestMixIn;

/**
 * BPM Test Mix In.
 * Helps with <a href="http://docs.jboss.org/jbpm/v5.2/userguide/ch.Human_Tasks.html">jBPM 5 Human Tasks</a>.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class BPMMixIn extends AbstractTestMixIn {

    private final boolean _managedLifecycle;
    private final TaskService _service;
    private TaskServer _server = null;
    private TaskClient _client = null;
    private String _host = "127.0.0.1";
    private int _port = 9123;
    private String _groupsPath = "/roles.properties";

    /**
     * Public default constructor.
     * <p/>
     * Manages the TaskServer connection lifecycle.
     */
    public BPMMixIn() {
        this(true);
    }

    /**
     * Public constructor.
     * @param managedLifecycle Do you want the SwitchYard test kit to manage the start/stop lifecycle of the BPM TaskServer.
     */
    public BPMMixIn(boolean managedLifecycle) {
        _managedLifecycle = managedLifecycle;
        _service = TaskService.instance();
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
     * Gets the groups path.
     * @return the groups path
     */
    public String getGroupsPath() {
        return _groupsPath;
    }

    /**
     * Sets the groups path.
     * @param groupsPath the groups path
     * @return this instance
     */
    public BPMMixIn setGroupsPath(String groupsPath) {
        _groupsPath = groupsPath;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize() {
        if (_managedLifecycle) {
            startTaskServer();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void uninitialize() {
        if (_managedLifecycle) {
            stopTaskServer();
        }
    }

    /**
     * Starts the task server.
     */
    public void startTaskServer() {
        if (_server == null) {
            _server = _service.newTaskServer();
            _server.setHost(getHost());
            _server.setPort(getPort());
            _server.start();
        }
    }

    /**
     * Stops the task server.
     */
    public void stopTaskServer() {
        if (_server != null) {
            try {
                _server.stop();
            } finally {
                _server = null;
            }
        }
    }

    /**
     * Connects the task client.
     */
    public void connectTaskClient() {
        if (_client == null) {
            _client = _service.newTaskClient();
            _client.setHost(getHost());
            _client.setPort(getPort());
            _client.connect();
        }
    }

    /**
     * Disconnects the task client.
     */
    public void disconnectTaskClient() {
        if (_client != null) {
            try {
                _client.disconnect();
            } finally {
                _client = null;
            }
        }
    }

    /**
     * Gets the users to groups mapping.
     * @return the users to groups mapping.
     */
    public Map<String,List<String>> getUsersGroups() {
        return getUsersGroups(getGroupsPath());
    }

    /**
     * Gets the users to groups mapping.
     * @param groupsPath the groups path
     * @return the users to groups mapping
     */
    public Map<String,List<String>> getUsersGroups(String groupsPath) {
        Map<String,List<String>> usersGroupsMap = new HashMap<String,List<String>>();
        Properties usersGroupsProps;
        try {
            usersGroupsProps = new PropertiesPuller().pull(groupsPath, BPMMixIn.class);
        } catch (IOException ioe) {
            throw new SwitchYardException(ioe);
        }
        for (Object userKey : usersGroupsProps.keySet()) {
            String user = (String)userKey;
            if (!"Administrator".equals(user)) {
                List<String> groups = Strings.splitTrimToNull(usersGroupsProps.getProperty(user), ",");
                usersGroupsMap.put(user, groups);
            }
        }
        return usersGroupsMap;
    }

    /**
     * Complete all human tasks.
     */
    public void completeHumanTasks() {
        completeHumanTasks(getUsersGroups());
    }

    /**
     * Complete all human tasks.
     * @param groupsPath the groups path
     */
    public void completeHumanTasks(String groupsPath) {
        completeHumanTasks(getUsersGroups(groupsPath));
    }

    /**
     * Complete all human tasks.
     * @param usersGroups the groups mapping
     */
    public void completeHumanTasks(Map<String,List<String>> usersGroups) {
        boolean keepWorking;
        do {
            keepWorking = doCompleteHumanTasks(usersGroups);
        } while (keepWorking);
    }

    private boolean doCompleteHumanTasks(Map<String,List<String>> usersGroups) {
        boolean keepWorking = false;
        for (String userId : usersGroups.keySet()) {
            List<String> groupIds = usersGroups.get(userId);
            List<Task> tasks = _client.getTasksAssignedAsPotentialOwner(userId, groupIds);
            for (Task task : tasks) {
                if (TaskStatus.COMPLETED.equals(task.getStatus())) {
                    continue;
                }
                Long taskId = task.getId();
                _client.claim(taskId, userId, groupIds);
                _client.start(taskId, userId);
                _client.complete(taskId, userId);
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
