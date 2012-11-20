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
package org.switchyard.component.common.knowledge.config.model.v1;

import org.switchyard.common.xml.XMLHelper;
import org.switchyard.component.common.knowledge.LoggerType;
import org.switchyard.component.common.knowledge.config.model.LoggerModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseNamedModel;
import org.switchyard.config.model.Descriptor;

/**
 * The 1st version ChannelModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class V1LoggerModel extends BaseNamedModel implements LoggerModel {

    /**
     * Creates a new MappingModel in the specified namespace.
     * @param namespace the namespace
     */
    public V1LoggerModel(String namespace) {
        super(XMLHelper.createQName(namespace, LOGGER));
    }

    /**
     * Creates a new MappingModel with the specified configuration and descriptor.
     * @param config the configuration
     * @param desc the descriptor
     */
    public V1LoggerModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getInterval() {
        String i = getModelAttribute("interval");
        return i != null ? Integer.valueOf(i) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LoggerModel setInterval(Integer interval) {
        String i = interval != null ? interval.toString() : null;
        setModelAttribute("interval", i);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLog() {
        return getModelAttribute("log");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LoggerModel setLog(String log) {
        setModelAttribute("log", log);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LoggerType getType() {
        String t = getModelAttribute("type");
        return t != null ? LoggerType.valueOf(t) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LoggerModel setType(LoggerType type) {
        String t = type != null ? type.name() : null;
        setModelAttribute("type", t);
        return this;
    }

}
