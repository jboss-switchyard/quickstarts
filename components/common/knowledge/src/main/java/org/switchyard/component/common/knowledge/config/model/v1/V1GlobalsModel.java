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

import static org.switchyard.component.common.knowledge.config.model.GlobalModel.GLOBAL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.switchyard.common.xml.XMLHelper;
import org.switchyard.component.common.knowledge.config.model.GlobalModel;
import org.switchyard.component.common.knowledge.config.model.GlobalsModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * A version 1 GlobalsModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class V1GlobalsModel extends BaseModel implements GlobalsModel {

    private List<GlobalModel> _globals = new ArrayList<GlobalModel>();

    /**
     * Creates a new V1GlobalsModel in the specified namespace.
     * @param namespace the specified namespace
     */
    public V1GlobalsModel(String namespace) {
        super(XMLHelper.createQName(namespace, GLOBALS));
        setModelChildrenOrder(GLOBAL);
    }

    /**
     * Creates a new V1GlobalsModel with the specified configuration and descriptor.
     * @param config the configuration
     * @param desc the descriptor
     */
    public V1GlobalsModel(Configuration config, Descriptor desc) {
        super(config, desc);
        for (Configuration global_config : config.getChildren(GLOBAL)) {
            GlobalModel global = (GlobalModel)readModel(global_config);
            if (global != null) {
                _globals.add(global);
            }
        }
        setModelChildrenOrder(GLOBAL);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<GlobalModel> getGlobals() {
        return Collections.unmodifiableList(_globals);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GlobalsModel addGlobal(GlobalModel global) {
        addChildModel(global);
        _globals.add(global);
        return this;
    }

}
