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
package org.switchyard.component.bpm.task.service;

import java.util.List;
import java.util.Locale;

/**
 * Represents a task client.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public interface TaskClient {

    /**
     * Gets the host.
     * @return the host
     */
    public String getHost();

    /**
     * Sets the host.
     * @param host the host
     * @return this instance
     */
    public TaskClient setHost(String host);

    /**
     * Gets the port.
     * @return the port
     */
    public int getPort();

    /**
     * Sets the port.
     * @param port the port
     * @return this instance
     */
    public TaskClient setPort(int port);

    /**
     * Connects to the task server.
     */
    public void connect();

    /**
     * Gets the connected status.
     * @return true if connect
     */
    public boolean isConnected();

    /**
     * Disconnects from the task server.
     */
    public void disconnect();

    /**
     * Gets the tasks assigned as a potential owner.
     * @param userId the potential owner
     * @param groupIds the groups of the potential owner
     * @return the lists of tasks
     */
    public List<Task> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds);

    /**
     * Gets the tasks assigned as a potential owner.
     * @param userId the potential owner
     * @param groupIds the groups of the potential owner
     * @param locale the locale of the potential owner
     * @return the lists of tasks
     */
    public List<Task> getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, Locale locale);

    /**
     * Claims a task.
     * @param taskId the task id
     * @param userId the user id
     * @param groupIds the user's group ids
     */
    public void claim(Long taskId, String userId, List<String> groupIds);

    /**
     * Starts a task.
     * @param taskId the task id
     * @param userId the user id
     */
    public void start(Long taskId, String userId);

    /**
     * Completes a task.
     * @param taskId the task id
     * @param userId the user id
     */
    public void complete(Long taskId, String userId);

}
