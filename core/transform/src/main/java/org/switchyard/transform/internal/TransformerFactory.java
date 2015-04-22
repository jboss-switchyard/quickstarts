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

package org.switchyard.transform.internal;

import org.switchyard.ServiceDomain;
import org.switchyard.config.model.transform.TransformModel;
import org.switchyard.transform.Transformer;

/**
 * Transformer Factory.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 *
 * @param <T> TransformerModel type.
 */
public interface TransformerFactory<T extends TransformModel> {

    /**
     * Create a new {@link Transformer} instance.
     * @param domain ServiceDomain instance.
     * @param model The Transformer config model.
     * @return The Transformer instance.
     */
    Transformer<?, ?> newTransformer(ServiceDomain domain, T model);
}
