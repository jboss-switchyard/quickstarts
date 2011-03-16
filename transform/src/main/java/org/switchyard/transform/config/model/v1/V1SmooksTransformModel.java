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

package org.switchyard.transform.config.model.v1;

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.transform.TransformModel;
import org.switchyard.config.model.transform.v1.V1BaseTransformModel;
import org.switchyard.transform.config.model.SmooksTransformModel;
import org.switchyard.transform.smooks.SmooksTransformType;

/**
 * A version 1 SmooksTransformModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class V1SmooksTransformModel extends V1BaseTransformModel implements SmooksTransformModel {

    /**
     * Constructs a new V1SmooksTransformModel.
     */
    public V1SmooksTransformModel() {
        super(new QName(TransformModel.DEFAULT_NAMESPACE, TransformModel.TRANSFORM + '.' + SMOOKS));
    }

    /**
     * Constructs a new V1SmooksTransformModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1SmooksTransformModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTransformType() {
        return getModelAttribute(TYPE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V1SmooksTransformModel setTransformType(String type) {
        setModelAttribute(TYPE, type);
        return this;
    }

    /**
     * Set the Smooks Transformation type.
     * @param type The transformation type.
     * @return <code>this</code> TransformModel instance.
     */
    public V1SmooksTransformModel setTransformType(SmooksTransformType type) {
        setModelAttribute(TYPE, type.toString());
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getConfig() {
        return getModelAttribute(CONFIG);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V1SmooksTransformModel setConfig(String config) {
        setModelAttribute(CONFIG, config);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getReportPath() {
        return getModelAttribute(REPORT_PATH);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V1SmooksTransformModel setReportPath(String reportPath) {
        setModelAttribute(REPORT_PATH, reportPath);
        return this;
    }

}
