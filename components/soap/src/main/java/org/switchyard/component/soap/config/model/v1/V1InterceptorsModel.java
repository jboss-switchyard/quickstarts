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
package org.switchyard.component.soap.config.model.v1;

import static org.switchyard.component.soap.config.model.InterceptorModel.INTERCEPTOR;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.switchyard.component.soap.config.model.InterceptorModel;
import org.switchyard.component.soap.config.model.InterceptorsModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * A version 1 InterceptorsModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public class V1InterceptorsModel extends BaseModel implements InterceptorsModel {

    private List<InterceptorModel> _interceptors = new ArrayList<InterceptorModel>();

    /**
     * Creates a new InterceptorsModel.
     * @param namespace namespace
     * @param name inInterceptors or outInterceptors
     */
    public V1InterceptorsModel(String namespace, String name) {
        super(new QName(namespace, name));
        setModelChildrenOrder(INTERCEPTOR);
    }

    /**
     * Creates a new InterceptorsModel with the specified configuration and descriptor.
     * @param config the configuration
     * @param desc the descriptor
     */
    public V1InterceptorsModel(Configuration config, Descriptor desc) {
        super(config, desc);
        for (Configuration interceptor_config : config.getChildren(INTERCEPTOR)) {
            InterceptorModel interceptor = (InterceptorModel)readModel(interceptor_config);
            if (interceptor != null) {
                _interceptors.add(interceptor);
            }
        }
        setModelChildrenOrder(INTERCEPTOR);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<InterceptorModel> getInterceptors() {
        return Collections.unmodifiableList(_interceptors);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InterceptorsModel addInterceptor(InterceptorModel interceptor) {
        addChildModel(interceptor);
        _interceptors.add(interceptor);
        return this;
    }

}
