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

package org.switchyard.deploy.components.config.v1;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseMarshaller;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.Model;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.config.model.composite.ComponentImplementationModel;

public class V1MockModelMarshaller extends BaseMarshaller {
    
    /**
     * Create a new MockModel marshaller.
     * @param desc The switchyard descriptor.
     */
    public V1MockModelMarshaller(final Descriptor desc) {
        super(desc);
    }
    
    @Override
    public Model read(final Configuration config) {
        Model model = null;
        if (config.getName().startsWith(BindingModel.BINDING)) {
            model = new V1MockBindingModel(config, getDescriptor());
        } else if (config.getName().startsWith(ComponentImplementationModel.IMPLEMENTATION)) {
            model = new V1MockImplementationModel(config, getDescriptor());
        }
        return model;
    }
}
