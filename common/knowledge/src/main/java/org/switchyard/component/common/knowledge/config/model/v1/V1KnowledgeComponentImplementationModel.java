/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.component.common.knowledge.config.model.v1;

import static org.switchyard.component.common.knowledge.config.model.ChannelsModel.CHANNELS;
import static org.switchyard.component.common.knowledge.config.model.ListenersModel.LISTENERS;
import static org.switchyard.component.common.knowledge.config.model.LoggersModel.LOGGERS;
import static org.switchyard.component.common.knowledge.config.model.ManifestModel.MANIFEST;
import static org.switchyard.component.common.knowledge.config.model.OperationsModel.OPERATIONS;
import static org.switchyard.config.model.property.PropertiesModel.PROPERTIES;

import org.switchyard.component.common.knowledge.config.model.ChannelsModel;
import org.switchyard.component.common.knowledge.config.model.KnowledgeComponentImplementationModel;
import org.switchyard.component.common.knowledge.config.model.ListenersModel;
import org.switchyard.component.common.knowledge.config.model.LoggersModel;
import org.switchyard.component.common.knowledge.config.model.ManifestModel;
import org.switchyard.component.common.knowledge.config.model.OperationsModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.composite.v1.V1ComponentImplementationModel;
import org.switchyard.config.model.property.PropertiesModel;

/**
 * An abstract "knowledge" implementation of a ComponentImplementationModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public abstract class V1KnowledgeComponentImplementationModel extends V1ComponentImplementationModel implements KnowledgeComponentImplementationModel {

    private ChannelsModel _channels;
    private ListenersModel _listeners;
    private LoggersModel _loggers;
    private ManifestModel _manifest;
    private OperationsModel _operations;
    private PropertiesModel _properties;


    /**
     * Constructs a new V1KnowledgeComponentImplementationModel of the specified "type", and in the specified namespace.
     * @param type the "type" of KnowledgeComponentImplementationModel
     * @param namespace the namespace
     */
    public V1KnowledgeComponentImplementationModel(String type, String namespace) {
        super(type, namespace);
    }

    /**
     * Constructs a new V1KnowledgeComponentImplementationModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1KnowledgeComponentImplementationModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ChannelsModel getChannels() {
        if (_channels == null) {
            _channels = (ChannelsModel)getFirstChildModel(CHANNELS);
        }
        return _channels;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KnowledgeComponentImplementationModel setChannels(ChannelsModel channels) {
        setChildModel(channels);
        _channels = channels;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListenersModel getListeners() {
        if (_listeners == null) {
            _listeners = (ListenersModel)getFirstChildModel(LISTENERS);
        }
        return _listeners;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KnowledgeComponentImplementationModel setListeners(ListenersModel listeners) {
        setChildModel(listeners);
        _listeners = listeners;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LoggersModel getLoggers() {
        if (_loggers == null) {
            _loggers = (LoggersModel)getFirstChildModel(LOGGERS);
        }
        return _loggers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KnowledgeComponentImplementationModel setLoggers(LoggersModel loggers) {
        setChildModel(loggers);
        _loggers = loggers;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ManifestModel getManifest() {
        if (_manifest == null) {
            _manifest = (ManifestModel)getFirstChildModel(MANIFEST);
        }
        return _manifest;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KnowledgeComponentImplementationModel setManifest(ManifestModel manifest) {
        setChildModel(manifest);
        _manifest = manifest;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OperationsModel getOperations() {
        if (_operations == null) {
            _operations = (OperationsModel)getFirstChildModel(OPERATIONS);
        }
        return _operations;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KnowledgeComponentImplementationModel setOperations(OperationsModel operations) {
        setChildModel(operations);
        _operations = operations;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PropertiesModel getProperties() {
        if (_properties == null) {
            _properties = (PropertiesModel)getFirstChildModel(PROPERTIES);
        }
        return _properties;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public KnowledgeComponentImplementationModel setProperties(PropertiesModel properties) {
        setChildModel(properties);
        _properties = properties;
        return this;
    }

}
