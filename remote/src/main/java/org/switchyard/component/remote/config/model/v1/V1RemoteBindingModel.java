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
package org.switchyard.component.remote.config.model.v1;

import org.switchyard.component.remote.config.model.RemoteBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.composite.v1.V1BindingModel;

/**
 * Version 1 implementation of a {@link RemoteBindingModel}. 
 *
 */
public class V1RemoteBindingModel extends V1BindingModel implements RemoteBindingModel {
    
    /**
     * No args constructor that uses the default namespace when constructing
     * this model.
     */
    public V1RemoteBindingModel() {
        super(REMOTE, DEFAULT_NAMESPACE);
    }

    /**
     * Constructor.
     * @param config The configuration model.
     * @param desc The descriptor for the model.
     */
    public V1RemoteBindingModel(final Configuration config, final Descriptor desc) {
        super(config, desc);
    }
}
