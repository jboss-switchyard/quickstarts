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
package org.switchyard.component.bpm.task;

import org.switchyard.ServiceDomain;

/**
 * Represents a Task handler.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public interface TaskHandler {

    /**
     * Gets the name of the task handler.
     * @return the name of the task handler
     */
    public String getName();

    /**
     * Sets the name of the task handler.
     * @param name the name of the task handler
     * @return this TaskHandler (useful for chaining)
     */
    public TaskHandler setName(String name);

    /**
     * Gets the message content name.
     * @return the name of the message content
     */
    public String getMessageContentName();

    /**
     * Sets the message content name.
     * @param messageContentName the message content name
     * @return this TaskHandler (useful for chaining)
     */
    public TaskHandler setMessageContentName(String messageContentName);

    /**
     * Gets the ServiceDomain.
     * @return the ServiceDomain
     */
    public ServiceDomain getServiceDomain();

    /**
     * Sets the ServiceDomain.
     * @param serviceDomain the ServiceDomain
     * @return this TaskHandler (useful for chaining)
     */
    public TaskHandler setServiceDomain(ServiceDomain serviceDomain);

    /**
     * Executes the specified Task.
     * @param task the specified Task
     * @param taskManager the TaskManager
     */
    public void executeTask(Task task,  TaskManager taskManager);

    /**
     * Aborts the specified Task.
     * @param task the specified Task
     * @param taskManager the TaskManager
     */
    public void abortTask(Task task,  TaskManager taskManager);

}
