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
package org.switchyard.component.rules.config.model.v1;

import static org.switchyard.component.rules.config.model.RulesComponentImplementationModel.DEFAULT_NAMESPACE;

import java.io.File;

import javax.xml.namespace.QName;

import org.switchyard.component.rules.common.RulesAuditType;
import org.switchyard.component.rules.config.model.RulesAuditModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * The 1st version RulesAuditModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class V1RulesAuditModel extends BaseModel implements RulesAuditModel {

    /**
     * Creates a new RulesAuditModel in the default namespace.
     */
    public V1RulesAuditModel() {
        super(new QName(DEFAULT_NAMESPACE, RULES_AUDIT));
    }

    /**
     * Creates a new RulesAuditModel with the specified configuration and descriptor.
     * @param config the configuration
     * @param desc the descriptor
     */
    public V1RulesAuditModel(Configuration config, Descriptor desc) {
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
    public RulesAuditModel setInterval(Integer interval) {
        setModelAttribute("interval", interval != null ? interval.toString() : null);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File getFile() {
        String file = getModelAttribute("file");
        return file != null ? new File(file) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RulesAuditModel setFile(File file) {
        setModelAttribute("file", file != null ? file.getAbsolutePath() : null);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RulesAuditType getType() {
        String rat = getModelAttribute("type");
        return rat != null ? RulesAuditType.valueOf(rat) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RulesAuditModel setType(RulesAuditType type) {
        String rat = type != null ? type.name() : null;
        setModelAttribute("type", rat);
        return this;
    }

}
