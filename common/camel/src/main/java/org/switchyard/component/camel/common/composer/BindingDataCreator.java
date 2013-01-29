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
package org.switchyard.component.camel.common.composer;

import org.apache.camel.Message;

/**
 * BindingDataCreator interface is an extension point which allows 3rd party endpoints
 * decide what kind of {@link CamelBindingData} instance should be created. This SPI
 * point allows implement SecurityBindingData for these endpoints who support secure
 * exchanges.
 *
 * @param <T> Type of binding data.
 */
public interface BindingDataCreator<T extends CamelBindingData> {

    /**
     * Creates new camel binding data for given input message.
     * 
     * @param message Message instance.
     * @return Camel binding data.
     */
    T createBindingData(Message message);

}
