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
package org.switchyard.component.jca.config.model;

import org.switchyard.config.model.Model;

/**
 * binding.jca/inboundConnection model.
 * 
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 *
 */
public interface InboundConnectionModel extends Model {

    /**
     * get ResouceAdapter model.
     * 
     * @return {@link ResourceAdamterModel}
     */
    ResourceAdapterModel getResourceAdapter();
    
    /**
     * set ResourceAdapter model.
     * 
     * @param config {@link ResourceAdapterModel} to set
     * @return [@link InboundConnectionModel} to support method chaining
     */
    InboundConnectionModel setResourceAdapter(final ResourceAdapterModel config);
    
    /**
     * get ActivationSpec model.
     * 
     * @return {@link ActivationSpecModel}
     */
    ActivationSpecModel getActivationSpec();
    
    /**
     * set ActivationSpec model.
     * 
     * @param config {@link ActivationSpecModel} to set
     * @return {@link InboundConnectionModel} to support method chaining
     */
    InboundConnectionModel setActivationSpec(final ActivationSpecModel config);
    
}
