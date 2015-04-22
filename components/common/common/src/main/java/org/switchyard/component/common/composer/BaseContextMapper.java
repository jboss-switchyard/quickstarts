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
package org.switchyard.component.common.composer;

import org.switchyard.Context;
import org.switchyard.config.model.composer.ContextMapperModel;

/**
 * Base class for ContextMapper, no-op'ing the required methods in case the extender only needs to override one of them.
 *
 * @param <D> the type of binding data
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2011 Red Hat Inc.
 */
public class BaseContextMapper<D extends BindingData> implements ContextMapper<D> {

    private ContextMapperModel _model;

    /**
     * {@inheritDoc}
     */
    @Override
    public ContextMapperModel getModel() {
        return _model;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setModel(ContextMapperModel model) {
        _model = model;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mapFrom(D source, Context context) throws Exception {
        // No-op; override if desired.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mapTo(Context context, D target) throws Exception {
        // No-op; override if desired.
    }

}
