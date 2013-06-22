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
package org.switchyard.component.rules.config.model.v1;

import static org.switchyard.component.common.knowledge.config.model.ChannelsModel.CHANNELS;
import static org.switchyard.component.common.knowledge.config.model.ListenersModel.LISTENERS;
import static org.switchyard.component.common.knowledge.config.model.LoggersModel.LOGGERS;
import static org.switchyard.component.common.knowledge.config.model.ManifestModel.MANIFEST;
import static org.switchyard.component.common.knowledge.config.model.OperationsModel.OPERATIONS;
import static org.switchyard.config.model.property.PropertiesModel.PROPERTIES;

import org.switchyard.component.common.knowledge.config.model.v1.V1KnowledgeComponentImplementationModel;
import org.switchyard.component.rules.config.model.RulesComponentImplementationModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * A "rules" implementation of a KnowledgeComponentImplementationModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class V1RulesComponentImplementationModel extends V1KnowledgeComponentImplementationModel implements RulesComponentImplementationModel {

    /**
     * Default constructor for application use.
     */
    public V1RulesComponentImplementationModel() {
        super(RULES, DEFAULT_NAMESPACE);
        setModelChildrenOrder(CHANNELS, LISTENERS, LOGGERS, MANIFEST, OPERATIONS, PROPERTIES);
    }

    /**
     * Constructor for Marshaller use (ie: V1RulesMarshaller).
     *
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1RulesComponentImplementationModel(Configuration config, Descriptor desc) {
        super(config, desc);
        setModelChildrenOrder(CHANNELS, LISTENERS, LOGGERS, MANIFEST, OPERATIONS, PROPERTIES);
    }

}
