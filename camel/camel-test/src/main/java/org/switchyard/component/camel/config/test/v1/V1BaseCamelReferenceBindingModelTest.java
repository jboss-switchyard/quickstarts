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
package org.switchyard.component.camel.config.test.v1;

import org.apache.camel.Endpoint;
import org.switchyard.component.camel.common.model.v1.V1BaseCamelBindingModel;

/**
 * Base class for camel bindings which uses reference elements.
 *
 * @param <T> Type of model
 * @param <E> Type of endpoint.
 */
public abstract class V1BaseCamelReferenceBindingModelTest<T extends V1BaseCamelBindingModel, E extends Endpoint>
    extends V1BaseCamelServiceBindingModelTest<T, E> {

    protected V1BaseCamelReferenceBindingModelTest(Class<E> endpointType, String fileName) {
        this(endpointType, fileName, true);
    }

    protected V1BaseCamelReferenceBindingModelTest(Class<E> endpointType, String fileName, boolean valid) {
        super(endpointType, fileName, valid);
    }

    @Override
    protected T getFirstBinding() throws Exception {
        return getFirstCamelReferenceBinding(getFileName());
    }

}
