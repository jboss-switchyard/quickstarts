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
package org.switchyard.component.common.rules.config.model.v1;

import org.switchyard.common.xml.XMLHelper;
import org.switchyard.component.common.rules.audit.AuditType;
import org.switchyard.component.common.rules.config.model.AuditModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * The 1st version AuditModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class V1AuditModel extends BaseModel implements AuditModel {

    /**
     * Creates a new AuditModel in the specified namespace.
     * @param namespace the namespace
     */
    public V1AuditModel(String namespace) {
        super(XMLHelper.createQName(namespace, AUDIT));
    }

    /**
     * Creates a new AuditModel with the specified configuration and descriptor.
     * @param config the configuration
     * @param desc the descriptor
     */
    public V1AuditModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getInterval() {
        String interval = getModelAttribute("interval");
        return interval != null ? Integer.valueOf(interval) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AuditModel setInterval(Integer interval) {
        setModelAttribute("interval", interval != null ? interval.toString() : null);
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
    public AuditModel setLog(String log) {
        setModelAttribute("log", log);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AuditType getType() {
        String rat = getModelAttribute("type");
        return rat != null ? AuditType.valueOf(rat) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AuditModel setType(AuditType type) {
        String rat = type != null ? type.name() : null;
        setModelAttribute("type", rat);
        return this;
    }

}
