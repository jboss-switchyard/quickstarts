/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.switchyard.component.bpm.config.model;

import org.switchyard.component.common.knowledge.config.model.KnowledgeComponentImplementationModel;

/**
 * BPMComponentImplementationModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public interface BPMComponentImplementationModel extends KnowledgeComponentImplementationModel {

    /**
     * The "bpm" namespace.
     */
    public static final String DEFAULT_NAMESPACE = "urn:switchyard-component-bpm:config:1.0";

    /**
     * The "bpm" implementation type.
     */
    public static final String BPM = "bpm";

    /**
     * Gets the "persistent" attribute.
     * @return the "persistent" attribute
     */
    public boolean isPersistent();

    /**
     * Sets the "persistent" attribute.
     * @param persistent the "persistent" attribute
     * @return this instance (useful for chaining)
     */
    public BPMComponentImplementationModel setPersistent(boolean persistent);

    /**
     * Gets the "processId" attribute.
     * @return the "processId" attribute
     */
    public String getProcessId();

    /**
     * Sets the "processId" attribute.
     * @param processId the "processId" attribute
     * @return this instance (useful for chaining)
     */
    public BPMComponentImplementationModel setProcessId(String processId);

    /**
     * Gets the child userGroupCallback model.
     * @return the child userGroupCallback model
     */
    public UserGroupCallbackModel getUserGroupCallback();

    /**
     * Sets the child userGroupCallback model.
     * @param userGroupCallback the child userGroupCallback model
     * @return this instance (useful for chaining)
     */
    public BPMComponentImplementationModel setUserGroupCallback(UserGroupCallbackModel userGroupCallback);

    /**
     * Gets the child workItemHandlers model.
     * @return the child workItemHandlers model
     */
    public WorkItemHandlersModel getWorkItemHandlers();

    /**
     * Sets the child workItemHandlers model.
     * @param workItemHandlers the child workItemHandlers model
     * @return this instance (useful for chaining)
     */
    public BPMComponentImplementationModel setWorkItemHandlers(WorkItemHandlersModel workItemHandlers);

}
