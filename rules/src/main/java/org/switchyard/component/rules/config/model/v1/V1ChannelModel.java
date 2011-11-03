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

import javax.xml.namespace.QName;

import org.switchyard.common.xml.XMLHelper;
import org.switchyard.component.rules.config.model.ChannelModel;
import org.switchyard.component.rules.config.model.RulesComponentImplementationModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseNamedModel;
import org.switchyard.config.model.Descriptor;

/**
 * The 1st version ChannelModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class V1ChannelModel extends BaseNamedModel implements ChannelModel {

    /**
     * Creates a new V1ChannelModel.
     */
    public V1ChannelModel() {
        super(XMLHelper.createQName(RulesComponentImplementationModel.DEFAULT_NAMESPACE, CHANNEL));
    }

    /**
     * Creates a new V1ChannelModel.
     * @param config the configuration
     * @param desc the descriptor
     */
    public V1ChannelModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getClazz() {
        return getModelAttribute("class");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ChannelModel setClazz(String clazz) {
        setModelAttribute("class", clazz);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QName getInput() {
        String input = getModelAttribute("input");
        return input != null ? XMLHelper.createQName(input) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ChannelModel setInput(QName input) {
        setModelAttribute("input", input != null ? input.toString() : null);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getOperation() {
        return getModelAttribute("operation");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ChannelModel setOperation(String operation) {
        setModelAttribute("operation", operation);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getReference() {
        return getModelAttribute("reference");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ChannelModel setReference(String reference) {
        setModelAttribute("reference", reference);
        return this;
    }

}
