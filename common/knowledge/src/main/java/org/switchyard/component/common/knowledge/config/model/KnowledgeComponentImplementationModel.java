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
package org.switchyard.component.common.knowledge.config.model;

import org.switchyard.config.model.composite.ComponentImplementationModel;
import org.switchyard.config.model.property.PropertiesModel;

/**
 * KnowledgeComponentImplementationModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public interface KnowledgeComponentImplementationModel extends ComponentImplementationModel {

    /**
     * Gets the child channels model.
     * @return the child channels model
     */
    public ChannelsModel getChannels();

    /**
     * Sets the child channels model.
     * @param channels the child channels model
     * @return this KnowledgeComponentImplementationModel (useful for chaining)
     */
    public KnowledgeComponentImplementationModel setChannels(ChannelsModel channels);

    /**
     * Gets the child listeners model.
     * @return the child listeners model
     */
    public ListenersModel getListeners();

    /**
     * Sets the child listeners model.
     * @param listeners the child listeners model
     * @return this KnowledgeComponentImplementationModel (useful for chaining)
     */
    public KnowledgeComponentImplementationModel setListeners(ListenersModel listeners);

    /**
     * Gets the child loggers model.
     * @return the child loggers model
     */
    public LoggersModel getLoggers();

    /**
     * Sets the child loggers model.
     * @param loggers the child loggers model
     * @return this KnowledgeComponentImplementationModel (useful for chaining)
     */
    public KnowledgeComponentImplementationModel setLoggers(LoggersModel loggers);

    /**
     * Gets the child manifest model.
     * @return the child manifest model
     */
    public ManifestModel getManifest();

    /**
     * Sets the child manifest model.
     * @param manifest the child manifest model
     * @return this KnowledgeComponentImplementationModel (useful for chaining)
     */
    public KnowledgeComponentImplementationModel setManifest(ManifestModel manifest);

    /**
     * Gets the child operations model.
     * @return the child operations model
     */
    public OperationsModel getOperations();

    /**
     * Sets the child operations model.
     * @param operations the child operations model
     * @return this KnowledgeComponentImplementationModel (useful for chaining)
     */
    public KnowledgeComponentImplementationModel setOperations(OperationsModel operations);

    /**
     * Gets the child properties model.
     * @return the child properties model
     */
    public PropertiesModel getProperties();

    /**
     * Sets the child properties model.
     * @param properties the child properties model
     * @return this KnowledgeComponentImplementationModel (useful for chaining)
     */
    public KnowledgeComponentImplementationModel setProperties(PropertiesModel properties);

}
