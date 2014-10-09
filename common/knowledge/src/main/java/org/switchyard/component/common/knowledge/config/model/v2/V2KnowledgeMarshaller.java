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
import static org.switchyard.component.common.knowledge.config.model.ExtraJaxbClassesModel.EXTRA_JAXB_CLASSES;
import static org.switchyard.component.common.knowledge.config.model.InputModel.INPUT;
import static org.switchyard.component.common.knowledge.config.model.ManifestModel.MANIFEST;
import static org.switchyard.component.common.knowledge.config.model.RemoteJmsModel.REMOTE_JMS;
import static org.switchyard.component.common.knowledge.config.model.RemoteRestModel.REMOTE_REST;
import static org.switchyard.config.model.property.PropertiesModel.PROPERTIES;

import org.switchyard.component.common.knowledge.config.model.v1.V1KnowledgeMarshaller;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.Model;
import org.switchyard.config.model.property.v2.V2PropertiesModel;

/**
 * A CompositeMarshaller which can also create knowledge models.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
public class V2KnowledgeMarshaller extends V1KnowledgeMarshaller {

    /**
     * Required constructor called via reflection.
     *
     * @param desc the Descriptor
     */
    public V2KnowledgeMarshaller(Descriptor desc) {
        super(desc);
    }

    /**
     * Reads in the Configuration, looking for various knowledge models.
     * If not found, it falls back to the super class (V1KnowledgeMarshaller).
     *
     * @param config the Configuration
     * @return the Model
     */
    @Override
    public Model read(Configuration config) {
        String name = config.getName();
        Descriptor desc = getDescriptor();
        if (MANIFEST.equals(name)) {
            return new V2ManifestModel(config, desc);
        } else if (REMOTE_JMS.equals(name)) {
            return new V2RemoteJmsModel(config, desc);
        } else if (REMOTE_REST.equals(name)) {
            return new V2RemoteRestModel(config, desc);
        } else if (EXTRA_JAXB_CLASSES.equals(name)) {
            return new V2ExtraJaxbClassesModel(config, desc);
        } else if (EXTRA_JAXB_CLASS.equals(name)) {
            return new V2ExtraJaxbClassModel(config, desc);
        } else if (INPUT.equals(name)) {
            return new V2InputModel(config, desc);
        } else if (PROPERTIES.equals(name)) {
            return new V2PropertiesModel(config, desc);
        }
        return super.read(config);
    }

}
