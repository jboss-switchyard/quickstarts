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
package org.switchyard.component.bpm.task.service.jbpm;

import java.util.Set;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.log4j.Logger;
import org.drools.SystemEventListenerFactory;
import org.jbpm.task.Group;
import org.jbpm.task.User;
import org.jbpm.task.service.TaskService;
import org.jbpm.task.service.TaskServiceSession;
import org.jbpm.task.service.mina.MinaTaskServer;
import org.switchyard.component.bpm.task.service.BaseTaskServer;
import org.switchyard.exception.SwitchYardException;

/**
 * A jBPM task server.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class JBPMTaskServer extends BaseTaskServer {

    private static final Logger LOGGER = Logger.getLogger(JBPMTaskServer.class);

    private org.jbpm.task.service.TaskServer _wrapped = null;

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
        taskServiceSession.dispose();
        _wrapped = new MinaTaskServer(taskService, getPort(), getHost());
        new Thread(_wrapped).start();
        setStarted(waitForPort(false));
        if (isStarted()) {
         LOGGER.info(String.format("jBPM TaskServer started on %s:%s.", getHost(), getPort()));
        } else {
            LOGGER.warn(String.format("jBPM TaskServer started on %s, but port %s is not yet ready.", getHost(), getPort()));
        }
    }

    private Set<String> addUsers(TaskServiceSession taskServiceSession) {
        Set<String> usersSet = getUsersSet("Administrator");
        for (String user : usersSet) {
            try {
                taskServiceSession.addUser(new User(user));
            } catch (Throwable t) {
                LOGGER.warn(t.getMessage());
            }
        }
        return usersSet;
    }

    private void addGroups(TaskServiceSession taskServiceSession, Set<String> usersSet) {
        Set<String> groupsSet = getGroupsSet();
        for (String group : groupsSet) {
            if (!usersSet.contains(group)) { // jBPM complains otherwise
                try {
                    taskServiceSession.addGroup(new Group(group));
                } catch (Throwable t) {
                    LOGGER.warn(t.getMessage());
                }
            }
        }
    }

    /**
     * Stops the TaskServer.
     */
    public void stop() {
        LOGGER.info(String.format("Stopping jBPM TaskServer on %s:%s...", getHost(), getPort()));
        if (_wrapped != null) {
            try {
                _wrapped.stop();
            } catch (Exception e) {
                throw new SwitchYardException(e);
            } finally {
                _wrapped = null;
            }
        }
        waitForPort(true);
        setStarted(false);
        LOGGER.info(String.format("jBPM TaskServer on %s:%s stopped.", getHost(), getPort()));
    }

}
