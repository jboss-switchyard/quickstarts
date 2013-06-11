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

import org.switchyard.component.soap.config.model.SOAPBindingModel;
import org.switchyard.component.soap.config.model.SOAPMessageComposerModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.composer.v1.V1MessageComposerModel;

/**
 * V1SOAPMessageComposerModel.
 */
public class V1SOAPMessageComposerModel extends V1MessageComposerModel implements SOAPMessageComposerModel {

    /**
     * Constructs a new V1SOAPContextMapperModel.
     */
    public V1SOAPMessageComposerModel() {
        super(SOAPBindingModel.DEFAULT_NAMESPACE);
    }
    
    /**
     * Constructs a new V1SOAPMessageComposerModel in the specified namespace.
     * @param namespace the specified namespace
     */
    public V1SOAPMessageComposerModel(String namespace) {
        super(namespace);
    }

    /**
     * Constructs a new V1SOAPMessageComposerModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1SOAPMessageComposerModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    @Override
    public Boolean isUnwrapped() {
        String unwrap = getModelAttribute("unwrapped");
        return unwrap != null && Boolean.valueOf(unwrap);
    }

    @Override
    public SOAPMessageComposerModel setUnwrapped(boolean unwrapped) {
        setModelAttribute("unwrapped", String.valueOf(unwrapped));
        return this;
    }

}
