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

package org.switchyard.transform.config.model.v1;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseMarshaller;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.Model;
import org.switchyard.config.model.transform.TransformModel;
import org.switchyard.transform.config.model.DozerFileEntryModel;
import org.switchyard.transform.config.model.DozerMappingFilesModel;
import org.switchyard.transform.config.model.DozerTransformModel;
import org.switchyard.transform.config.model.JAXBTransformModel;
import org.switchyard.transform.config.model.JSONTransformModel;
import org.switchyard.transform.config.model.JavaTransformModel;
import org.switchyard.transform.config.model.SmooksTransformModel;
import org.switchyard.transform.config.model.XsltTransformModel;

/**
 * Marshalls transform Models.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class V1TransformMarshaller extends BaseMarshaller {

    private static final String TRANSFORM_JAVA = TransformModel.TRANSFORM + "." + JavaTransformModel.JAVA;
    private static final String TRANSFORM_SMOOKS = TransformModel.TRANSFORM + "." + SmooksTransformModel.SMOOKS;
    private static final String TRANSFORM_JSON = TransformModel.TRANSFORM + "." + JSONTransformModel.JSON;
    private static final String TRANSFORM_XSLT = TransformModel.TRANSFORM + "." + XsltTransformModel.XSLT;
    private static final String TRANSFORM_JAXB = TransformModel.TRANSFORM + "." + JAXBTransformModel.JAXB;
    private static final String TRANSFORM_DOZER = TransformModel.TRANSFORM + "." + DozerTransformModel.DOZER;

    /**
     * Constructs a new V1TransformMarshaller with the specified Descriptor.
     * @param desc the Descriptor
     */
    public V1TransformMarshaller(Descriptor desc) {
        super(desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Model read(Configuration config) {
        String name = config.getName();
        Descriptor desc = getDescriptor();

        if (name.equals(TRANSFORM_JAVA)) {
            return new V1JavaTransformModel(config, desc);
        } else if (name.equals(TRANSFORM_SMOOKS)) {
            return new V1SmooksTransformModel(config, desc);
        } else if (name.equals(TRANSFORM_JSON)) {
            return new V1JSONTransformModel(config, desc);
        } else if (name.equals(TRANSFORM_XSLT)) {
            return new V1XsltTransformModel(config, desc);
        } else if (name.equals(TRANSFORM_JAXB)) {
            return new V1JAXBTransformModel(config, desc);
        } else if (name.equals(TRANSFORM_DOZER)) {
            return new V1DozerTransformModel(config, desc);
        } else if (name.equals(DozerMappingFilesModel.MAPPING_FILES)) {
            return new V1DozerMappingFilesModel(config, desc);
        } else if (name.equals(DozerFileEntryModel.ENTRY)) {
            return new V1DozerFileEntryModel(config, desc);
        }

        return null;
    }

}
