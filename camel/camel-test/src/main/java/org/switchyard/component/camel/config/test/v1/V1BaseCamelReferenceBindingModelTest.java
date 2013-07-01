/*
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
