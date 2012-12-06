/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.switchyard.component.camel.common.model.remote.v1;

import org.switchyard.component.camel.common.model.file.v1.V1GenericFileConsumerBindingModel;
import org.switchyard.component.camel.common.model.remote.CamelRemoteFileConsumerBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * Consumer binding model for remote file.
 * 
 * @author Lukasz Dywicki
 */
public class V1CamelRemoteFileConsumerBindingModel extends V1GenericFileConsumerBindingModel
    implements CamelRemoteFileConsumerBindingModel {

    /**
     * Creates consumer binding model.
     * 
     * @param config Configuration.
     * @param desc Descriptor.
     */
    public V1CamelRemoteFileConsumerBindingModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * Creates model bound to given namespace.
     * 
     * @param name Name of element.
     * @param namespace Namespace to bound.
     */
    public V1CamelRemoteFileConsumerBindingModel(String name, String namespace) {
        super(name, namespace);
    }

}
