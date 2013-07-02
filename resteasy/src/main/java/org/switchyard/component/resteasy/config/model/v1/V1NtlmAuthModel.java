/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.component.resteasy.config.model.v1;

import org.switchyard.component.resteasy.config.model.NtlmAuthModel;
import org.switchyard.component.resteasy.config.model.RESTEasyNameValueModel;
import org.switchyard.component.resteasy.config.model.RESTEasyNameValueModel.RESTEasyName;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * A NtlmAuthModel V1 implementation.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2013 Red Hat Inc.
 */
public class V1NtlmAuthModel extends V1BasicAuthModel implements NtlmAuthModel {

    private RESTEasyNameValueModel _domain;

    /**
     * Creates a new NtlmAuthModel.
     */
    public V1NtlmAuthModel() {
        super(RESTEasyName.ntlm.name());
    }

    /**
     * Creates a new NtlmAuthModel with the specified configuration and descriptor.
     * @param config the configuration
     * @param desc the descriptor
     */
    public V1NtlmAuthModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    public String getDomain() {
        if (_domain == null) {
            _domain = getNameValue(RESTEasyName.domain);
        }
        return _domain != null ? _domain.getValue() : null;
    }

    /**
     * {@inheritDoc}
     */
    public NtlmAuthModel setDomain(String domain) {
        _domain = setNameValue(_domain, RESTEasyName.domain, domain);
        return this;
    }
}
