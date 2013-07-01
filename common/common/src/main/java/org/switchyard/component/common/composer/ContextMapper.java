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
 * Maps context properties from and to a source or target object.
 * 
 * @param <D> the type of binding data
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2011 Red Hat Inc.
 */
public interface ContextMapper<D extends BindingData> {

    /**
     * Gets the model.
     * @return the model
     */
    public ContextMapperModel getModel();

    /**
     * Sets the model.
     * @param model the model
     */
    public void setModel(ContextMapperModel model);

    /**
     * Maps a source object's properties to the context.
     * @param source the object to map from
     * @param context the context to map to
     * @throws Exception if there was a problem
     */
    public void mapFrom(D source, Context context) throws Exception;

    /**
     * Maps a context's properties into a target object.
     * @param context the context to map from
     * @param target the target to map to
     * @throws Exception if there was a problem
     */
    public void mapTo(Context context, D target) throws Exception;

}
