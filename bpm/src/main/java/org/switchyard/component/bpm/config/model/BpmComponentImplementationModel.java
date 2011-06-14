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

import org.switchyard.component.bpm.process.ProcessResourceType;
import org.switchyard.config.model.composite.ComponentImplementationModel;

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
     * Gets the "processDefinition" attribute.
     *
     * @return the "processDefinition" attribute
     */
    public String getProcessDefinition();

    /**
     * Sets the "processDefinition" attribute.
     *
     * @param processDefinition the "processDefinition" attribute
     * @return this instance (useful for chaining)
     */
    public BpmComponentImplementationModel setProcessDefinition(String processDefinition);

    /**
     * Gets the "processDefinitionType" attribute.
     *
     * @return the "processDefinitionType" attribute
     */
    public ProcessResourceType getProcessDefinitionType();

    /**
     * Sets the "processDefinitionType" attribute.
     *
     * @param processDefinitionType the "processDefinitionType" attribute
     * @return this instance (useful for chaining)
     */
    public BpmComponentImplementationModel setProcessDefinitionType(ProcessResourceType processDefinitionType);

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
     * Gets the child process resource models.
     * @return the child process resource models
     */
    public List<ProcessResourceModel> getProcessResources();

    /**
     * Adds a child process resource model.
     * @param processResource the child process process resource model
     * @return this BpmComponentImplementationModel (useful for chaining)
     */
    public BpmComponentImplementationModel addProcessResource(ProcessResourceModel processResource);

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
