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
package org.switchyard.component.common.knowledge.config.model.v1;

import static org.switchyard.component.common.knowledge.config.model.ListenerModel.LISTENER;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.switchyard.component.common.knowledge.config.model.ListenerModel;
import org.switchyard.component.common.knowledge.config.model.ListenersModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * A version 1 ListenersModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class V1ListenersModel extends BaseModel implements ListenersModel {

    private List<ListenerModel> _listeners = new ArrayList<ListenerModel>();

    /**
     * Creates a new ListenersModel in the specified namespace.
     * @param namespace the specified namespace
     */
    public V1ListenersModel(String namespace) {
        super(new QName(namespace, LISTENERS));
        setModelChildrenOrder(LISTENER);
    }

    /**
     * Creates a new ListenersModel with the specified configuration and descriptor.
     * @param config the configuration
     * @param desc the descriptor
     */
    public V1ListenersModel(Configuration config, Descriptor desc) {
        super(config, desc);
        for (Configuration listener_config : config.getChildren(LISTENER)) {
            ListenerModel listener = (ListenerModel)readModel(listener_config);
            if (listener != null) {
                _listeners.add(listener);
            }
        }
        setModelChildrenOrder(LISTENER);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<ListenerModel> getListeners() {
        return Collections.unmodifiableList(_listeners);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ListenersModel addListener(ListenerModel listener) {
        addChildModel(listener);
        _listeners.add(listener);
        return this;
    }

}
