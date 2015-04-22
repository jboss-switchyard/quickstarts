/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.switchyard.component.common.knowledge.config.model.v2;

import static org.switchyard.component.common.knowledge.config.model.ExtraJaxbClassModel.EXTRA_JAXB_CLASS;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.switchyard.component.common.knowledge.config.model.ExtraJaxbClassModel;
import org.switchyard.component.common.knowledge.config.model.ExtraJaxbClassesModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * A version 2 ExtraJaxbClassesModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
public class V2ExtraJaxbClassesModel extends BaseModel implements ExtraJaxbClassesModel {

    private List<ExtraJaxbClassModel> _extraJaxbClasses = new ArrayList<ExtraJaxbClassModel>();

    /**
     * Creates a new ExtraJaxbClassesModel in the specified namespace.
     * @param namespace the specified namespace
     */
    public V2ExtraJaxbClassesModel(String namespace) {
        super(new QName(namespace, EXTRA_JAXB_CLASSES));
        setModelChildrenOrder(EXTRA_JAXB_CLASS);
    }

    /**
     * Creates a new ExtraJaxbClassesModel with the specified configuration and descriptor.
     * @param config the configuration
     * @param desc the descriptor
     */
    public V2ExtraJaxbClassesModel(Configuration config, Descriptor desc) {
        super(config, desc);
        for (Configuration extraJaxbClass_config : config.getChildren(EXTRA_JAXB_CLASS)) {
            ExtraJaxbClassModel extraJaxbClass = (ExtraJaxbClassModel)readModel(extraJaxbClass_config);
            if (extraJaxbClass != null) {
                _extraJaxbClasses.add(extraJaxbClass);
            }
        }
        setModelChildrenOrder(EXTRA_JAXB_CLASS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<ExtraJaxbClassModel> getExtraJaxbClasses() {
        return Collections.unmodifiableList(_extraJaxbClasses);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ExtraJaxbClassesModel addExtraJaxbClass(ExtraJaxbClassModel extraJaxbClass) {
        addChildModel(extraJaxbClass);
        _extraJaxbClasses.add(extraJaxbClass);
        return this;
    }

}
