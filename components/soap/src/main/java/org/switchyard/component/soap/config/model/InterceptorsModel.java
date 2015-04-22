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
package org.switchyard.component.soap.config.model;

import java.util.List;

import org.switchyard.config.model.Model;

/**
 * A Interceptors Model.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public interface InterceptorsModel extends Model {

    /** The "inInterceptors" name. */
    public static final String IN_INTERCEPTORS = "inInterceptors";

    /** The "outInterceptors" name. */
    public static final String OUT_INTERCEPTORS = "outInterceptors";

    /**
     * Gets the child interceptor models.
     * @return the child interceptor models
     */
    public List<InterceptorModel> getInterceptors();

    /**
     * Adds a child interceptor model.
     * @param interceptor the child interceptor model
     * @return this ChannelsModel (useful for chaining)
     */
    public InterceptorsModel addInterceptor(InterceptorModel interceptor);

}
