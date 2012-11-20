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
     * Gets the child actions model.
     * @return the child actions model
     */
    public ActionsModel getActions();

    /**
     * Sets the child actions model.
     * @param actions the child actions model
     * @return this KnowledgeComponentImplementationModel (useful for chaining)
     */
    public KnowledgeComponentImplementationModel setActions(ActionsModel actions);

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
