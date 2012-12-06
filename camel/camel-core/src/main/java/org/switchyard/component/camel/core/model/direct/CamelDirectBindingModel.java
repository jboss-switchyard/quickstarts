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
package org.switchyard.component.camel.core.model.direct;

import org.switchyard.component.camel.common.model.CamelBindingModel;

/**
 * Represents the configuration settings for a Direct endpoint in Camel. The 
 * direct component provides direct, synchronous invocation of any consumers 
 * when a producer sends a message exchange. This endpoint can be used to 
 * connect existing routes in the same camel context.
 */
public interface CamelDirectBindingModel extends CamelBindingModel {

    /**
     * The name can be any String to uniquely identify the endpoint.
     * @return the unique identifier for the endpoint
     */
    String getName();

    /**
     * The name can be any String to uniquely identify the endpoint.
     * @param name the unique identifier for the endpoint
     * @return a reference to this Direct binding model
     */
    CamelDirectBindingModel setName(String name);

}
