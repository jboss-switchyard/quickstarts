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
package org.switchyard.config.model.domain.v1;

import static org.switchyard.config.model.domain.SecurityModel.SECURITY;
import static org.switchyard.config.model.switchyard.SwitchYardModel.DEFAULT_NAMESPACE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.domain.DomainModel;
import org.switchyard.config.model.domain.SecuritiesModel;
import org.switchyard.config.model.domain.SecurityModel;

/**
 * A version 1 SecuritiesModel.
 */
public class V1SecuritiesModel extends BaseModel implements SecuritiesModel {

    private List<SecurityModel> _securities = new ArrayList<SecurityModel>();

    /**
     * Constructs a new V1SecuritiesModel.
     */
    public V1SecuritiesModel() {
        super(new QName(DEFAULT_NAMESPACE, SECURITIES));
        setModelChildrenOrder(SECURITY);
    }

    /**
     * Constructs a new V1SecuritiesModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1SecuritiesModel(Configuration config, Descriptor desc) {
        super(config, desc);
        for (Configuration security_config : config.getChildrenStartsWith(SECURITY)) {
            SecurityModel security = (SecurityModel)readModel(security_config);
            if (security != null) {
                _securities.add(security);
            }
        }
        setModelChildrenOrder(SECURITY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DomainModel getDomain() {
        return (DomainModel)getModelParent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<SecurityModel> getSecurities() {
        return Collections.unmodifiableList(_securities);
    }

}
