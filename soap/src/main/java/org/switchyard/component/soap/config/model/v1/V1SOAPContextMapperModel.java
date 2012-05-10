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
package org.switchyard.component.soap.config.model.v1;

import static org.switchyard.component.soap.composer.SOAPHeadersType.VALUE;

import org.switchyard.component.soap.composer.SOAPHeadersType;
import org.switchyard.component.soap.config.model.SOAPContextMapperModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.composer.v1.V1ContextMapperModel;

/**
 * V1SOAPContextMapperModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2012 Red Hat Inc.
 */
public class V1SOAPContextMapperModel extends V1ContextMapperModel implements SOAPContextMapperModel {

    /**
     * Constructs a new V1SOAPContextMapperModel in the specified namespace.
     * @param namespace the specified namespace
     */
    public V1SOAPContextMapperModel(String namespace) {
        super(namespace);
    }

    /**
     * Constructs a new V1SOAPContextMapperModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1SOAPContextMapperModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SOAPHeadersType getSOAPHeadersType() {
        String sht = getModelAttribute("soapHeadersType");
        return sht != null ? SOAPHeadersType.valueOf(sht) : VALUE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SOAPContextMapperModel setSOAPHeadersType(SOAPHeadersType soapHeadersType) {
        String sht = soapHeadersType != null ? soapHeadersType.name() : null;
        setModelAttribute("soapHeadersType", sht);
        return this;
    }

}
