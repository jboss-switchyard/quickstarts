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
package org.switchyard.component.bpm.task.jbpm;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.log4j.Logger;
import org.drools.SystemEventListenerFactory;
import org.jbpm.task.Group;
import org.jbpm.task.User;
import org.jbpm.task.service.TaskServer;
import org.jbpm.task.service.TaskService;
import org.jbpm.task.service.TaskServiceSession;
import org.jbpm.task.service.mina.MinaTaskServer;
import org.switchyard.common.io.pull.PropertiesPuller;
import org.switchyard.common.lang.Strings;
import org.switchyard.exception.SwitchYardException;

/**
 * TaskServerController.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public final class TaskServerController {

    private static final Logger LOGGER = Logger.getLogger(TaskServerController.class);

    private String _host = "127.0.0.1";
    private int _port = 9123;
    private boolean _started = false;
    private TaskServer _server = null;
    private String _usersPath = "/users.properties";
    private String _rolesPath = "/roles.properties";

    /**
     * Default public constructor.
     */
    public TaskServerController() {}

    /**
     * Public constructor accepting host and port.
     * @param host the host
     * @param port the port
     */
    public TaskServerController(String host, int port) {
        setHost(host);
        setPort(port);
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
    public TaskServerController setHost(String host) {
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
    public TaskServerController setPort(int port) {
        _port = port;
        return this;
    }

    /**
     * Gets the started status.
     * @return true if started
     */
    public boolean isStarted() {
        return _started;
    }

    /**
     * Gets the users path.
     * @return the users path
     */
    public String getUsersPath() {
        return _usersPath;
    }

    /**
     * Sets the users path.
     * @param usersPath the users path
     * @return this instance
     */
    public TaskServerController setUsersPath(String usersPath) {
        _usersPath = usersPath;
        return this;
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
    public TaskServerController setRolesPath(String rolesPath) {
        _rolesPath = rolesPath;
        return this;
    }

    /**
     * Starts the TaskServer.
     */
    public void start() {
        LOGGER.info(String.format("Starting jBPM TaskServer on %s:%s...", getHost(), getPort()));
        waitForPort(true);
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.jbpm.task");
        TaskService taskService = new TaskService(emf, SystemEventListenerFactory.getSystemEventListener());
        TaskServiceSession taskServiceSession = taskService.createSession();
        Set<String> userIds = addUsers(taskServiceSession);
        addGroups(taskServiceSession, userIds);
        _server = new MinaTaskServer(taskService, getPort(), getHost());
        new Thread(_server).start();
        taskServiceSession.dispose();
        _started = waitForPort(false);
        if (_started) {
         LOGGER.info(String.format("jBPM TaskServer started on %s:%s.", getHost(), getPort()));
        } else {
            LOGGER.warn(String.format("jBPM TaskServer started on %s, but port %s is not yet ready.", getHost(), getPort()));
        }
    }

    private boolean waitForPort(final boolean wantFree) {
        int attempts = 0;
        boolean ready = false;
        while (!ready && attempts < 11) {
            attempts++;
            Socket socket = null;
            try {
                socket = new Socket(getHost(), getPort());
                final boolean connected = socket.isConnected();
                if ((wantFree && connected) || (!wantFree && !connected)) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        LOGGER.trace(ie.getMessage());
                    }
                } else {
                    ready = true;
                }
            } catch (IOException ioe) {
                if (wantFree) {
                    ready = true;
                } else {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        LOGGER.trace(ie.getMessage());
                    }
                }
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException ioe) {
                        LOGGER.warn(ioe.getMessage());
                    } finally {
                        socket = null;
                    }
                }
            }
        }
        return ready;
    }

    private Set<String> addUsers(TaskServiceSession taskServiceSession) {
        Properties userProps;
        try {
            userProps = new PropertiesPuller().pull(getUsersPath(), TaskServerController.class);
        } catch (IOException ioe) {
            throw new SwitchYardException(ioe);
        }
        Set<String> userIds = new LinkedHashSet<String>();
        userIds.add("Administrator");
        if (userProps != null) {
            for (Object userKey : userProps.keySet()) {
                String userId = Strings.trimToNull((String)userKey);
                if (userId != null) {
                    userIds.add(userId);
                }
            }
        }
        for (String userId : userIds) {
            try {
                taskServiceSession.addUser(new User(userId));
            } catch (Throwable t) {
                LOGGER.warn(t.getMessage());
            }
        }
        return userIds;
    }

    private void addGroups(TaskServiceSession taskServiceSession, Set<String> userIds) {
        Properties groupProps;
        try {
            groupProps = new PropertiesPuller().pull(getRolesPath(), TaskServerController.class);
        } catch (IOException ioe) {
            throw new SwitchYardException(ioe);
        }
        if (groupProps != null) {
            Set<String> groupIds = new LinkedHashSet<String>();
            for (Object groupKey : groupProps.keySet()) {
                String groupValue = groupProps.getProperty((String)groupKey);
                StringTokenizer st = new StringTokenizer(groupValue, ",");
                while (st.hasMoreTokens()) {
                    String groupId = Strings.trimToNull(st.nextToken());
                    if (groupId != null) {
                        groupIds.add(groupId);
                    }
                }
            }
            for (String groupId : groupIds) {
                if (!userIds.contains(groupId)) { // jBPM complains otherwise
                    try {
                        taskServiceSession.addGroup(new Group(groupId));
                    } catch (Throwable t) {
                        LOGGER.warn(t.getMessage());
                    }
                }
            }
        }
    }

    /**
     * Stops the TaskServer.
     */
    public void stop() {
        LOGGER.info(String.format("Stopping jBPM TaskServer on %s:%s...", getHost(), getPort()));
        if (_server != null) {
            try {
                _server.stop();
            } catch (Exception e) {
                throw new SwitchYardException(e);
            } finally {
                _server = null;
            }
        }
        _started = false;
        LOGGER.info(String.format("jBPM TaskServer on %s:%s stopped.", getHost(), getPort()));
    }

    /**
     * CLI.
     * @param args none required
     */
    public static void main(String... args) {
        final TaskServerController controller = new TaskServerController();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                controller.stop();
            }
        });
        controller.start();
    }

}
