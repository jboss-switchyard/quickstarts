/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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

package org.switchyard.component.bpm.config.model;

import java.util.List;

import org.switchyard.common.io.resource.Resource;
import org.switchyard.config.model.composite.ComponentImplementationModel;
import org.switchyard.config.model.resource.ResourceModel;

/**
 * A "bpm" ComponentImplementationModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public interface BpmComponentImplementationModel extends ComponentImplementationModel {

    /**
     * The "bpm" namespace.
     */
    public static final String DEFAULT_NAMESPACE = "urn:switchyard-component-bpm:config:1.0";

    /**
     * The "bpm" implementation type.
     */
    public static final String BPM = "bpm";

    /**
     * Gets the "processDefinition" resource.
     *
     * @return the "processDefinition" resource
     */
    public Resource getProcessDefinition();

    /**
     * Sets the "processDefinition" resource.
     *
     * @param processDefinition the "processDefinition" resource
     * @return this instance (useful for chaining)
     */
    public BpmComponentImplementationModel setProcessDefinition(Resource processDefinition);

    /**
     * Gets the "processId" attribute.
     *
     * @return the "processId" attribute
     */
    public String getProcessId();

    /**
     * Sets the "processId" attribute.
     *
     * @param processId the "processId" attribute
     * @return this instance (useful for chaining)
     */
    public BpmComponentImplementationModel setProcessId(String processId);

    /**
     * Gets the "messageContentName" attribute.
     *
     * @return the "messageContentName" attribute
     */
    public String getMessageContentName();

    /**
     * Sets the "messageContentName" attribute.
     *
     * @param messageContentName the "messageContentName" attribute
     * @return this instance (useful for chaining)
     */
    public BpmComponentImplementationModel setMessageContentName(String messageContentName);

    /**
     * Gets the child process action models.
     * @return the child process action models
     */
    public List<ProcessActionModel> getProcessActions();

    /**
     * Adds a child process action model.
     * @param processAction the child process action model
     * @return this BpmComponentImplementationModel (useful for chaining)
     */
    public BpmComponentImplementationModel addProcessAction(ProcessActionModel processAction);

    /**
     * Gets the child resource models.
     * @return the child resource models
     */
    public List<ResourceModel> getResources();

    /**
     * Adds a child resource model.
     * @param resource the child resource model
     * @return this BpmComponentImplementationModel (useful for chaining)
     */
    public BpmComponentImplementationModel addResource(ResourceModel resource);

    /**
     * Gets the child task handler models.
     * @return the child task handler models
     */
    public List<TaskHandlerModel> getTaskHandlers();

    /**
     * Adds a child task item handler model.
     * @param taskHandler the child task handler model
     * @return this BpmComponentImplementationModel (useful for chaining)
     */
    public BpmComponentImplementationModel addTaskHandler(TaskHandlerModel taskHandler);

}
