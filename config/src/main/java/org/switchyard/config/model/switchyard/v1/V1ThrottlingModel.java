/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.config.model.switchyard.v1;

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.switchyard.SwitchYardModel;
import org.switchyard.config.model.switchyard.ThrottlingModel;

/**
 * Implementation of ThrottlingModel : v1.
 */
public class V1ThrottlingModel extends BaseModel implements ThrottlingModel {

    /**
     * Constructs a new V1ThrottlingModel.
     */
    public V1ThrottlingModel() {
        super(new QName(SwitchYardModel.DEFAULT_NAMESPACE, ThrottlingModel.THROTTLING));
    }

    /**
     * Constructs a new V1ThrottlingModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1ThrottlingModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Long getTimePeriod() {
        final String value = getModelAttribute(ThrottlingModel.TIME_PERIOD);
        return value == null ? null : Long.valueOf(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ThrottlingModel setTimePeriod(Long timePeriod) {
        setModelAttribute(ThrottlingModel.TIME_PERIOD, timePeriod == null ? null : timePeriod.toString());
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getMaxRequests() {
        return Integer.valueOf(getModelAttribute(ThrottlingModel.MAX_REQUESTS));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ThrottlingModel setMaxRequests(int maxRequests) {
        setModelAttribute(ThrottlingModel.MAX_REQUESTS, String.valueOf(maxRequests));
        return this;
    }

}
