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
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.log4j.Logger;
import org.drools.agent.impl.DoNothingSystemEventListener;
import org.jbpm.task.Group;
import org.jbpm.task.User;
import org.jbpm.task.service.TaskServer;
import org.jbpm.task.service.TaskService;
import org.jbpm.task.service.TaskServiceSession;
import org.jbpm.task.service.mina.MinaTaskServer;
import org.switchyard.common.io.pull.PropertiesPuller;
import org.switchyard.common.lang.Strings;

/**
 * TaskServerServletContextListener.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class TaskServerServletContextListener implements ServletContextListener {

    private static final Logger LOGGER = Logger.getLogger(TaskServerServletContextListener.class);

    /** The default host. */
    public static final String DEFAULT_HOST = "127.0.0.1";
    /** The default port. */
    public static final int DEFAULT_PORT = 9123;
    /** The Administrator userId. */
    public static final String ADMINISTRATOR = "Administrator";

    // TODO: Allow overriding of host and port via configuration in web.xml.
    private String _host = DEFAULT_HOST;
    private int _port = DEFAULT_PORT;
    private TaskServer _server = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        LOGGER.info("Starting jBPM TaskServer...");
        startTaskServer();
        LOGGER.info("jBPM TaskServer started.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        LOGGER.info("Stopping jBPM TaskServer...");
        stopTaskServer();
        LOGGER.info("jBPM TaskServer stopped.");
    }

    private void startTaskServer() {
        boolean ready = false;
        while (!ready) {
            Socket socket = null;
            try {
                socket = new Socket(_host, _port);
                if (socket.isConnected()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        LOGGER.warn(ie.getMessage());
                    }
                } else {
                    ready = true;
                }
            } catch (IOException ioe) {
                ready = true;
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
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.jbpm.task");
        TaskService taskService = new TaskService(emf, new DoNothingSystemEventListener());
        TaskServiceSession taskServiceSession = taskService.createSession();
        Properties userProps;
        try {
            userProps = new PropertiesPuller().pull("/users.properties", getClass());
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        Set<String> userIds = new LinkedHashSet<String>();
        if (userProps != null) {
            userIds.add(ADMINISTRATOR);
            for (Object userKey : userProps.keySet()) {
                String userId = Strings.trimToNull((String)userKey);
                if (userId != null) {
                    userIds.add(userId);
                }
            }
            for (String userId : userIds) {
                addUser(taskServiceSession, userId);
            }
        }
        Properties groupProps;
        try {
            groupProps = new PropertiesPuller().pull("/roles.properties", getClass());
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
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
                    addGroup(taskServiceSession, groupId);
                }
            }
        }
        taskServiceSession.dispose();
        _server = new MinaTaskServer(taskService, _port, _host);
        new Thread(_server).start();
        //_server.start();
    }

    private void addUser(TaskServiceSession taskServiceSession, String userId) {
        try {
            taskServiceSession.addUser(new User(userId));
        } catch (Throwable t) {
            LOGGER.warn(t.getMessage());
        }
    }

    private void addGroup(TaskServiceSession taskServiceSession, String groupId) {
        try {
            taskServiceSession.addGroup(new Group(groupId));
        } catch (Throwable t) {
            LOGGER.warn(t.getMessage());
        }
    }

    private void stopTaskServer() {
        if (_server != null) {
            try {
                _server.stop();
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                _server = null;
            }
        }
    }

}
