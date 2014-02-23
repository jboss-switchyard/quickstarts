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

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.transform.TransformModel;
import org.switchyard.config.model.transform.v1.V1BaseTransformModel;
import org.switchyard.transform.config.model.DozerMappingFilesModel;
import org.switchyard.transform.config.model.DozerTransformModel;
import org.switchyard.transform.internal.TransformerFactoryClass;
import org.switchyard.transform.dozer.internal.DozerTransformFactory;

/**
 * A version 1 DozerTransformModel.
 */
@TransformerFactoryClass(DozerTransformFactory.class)
public class V1DozerTransformModel extends V1BaseTransformModel implements DozerTransformModel {

    private DozerMappingFilesModel _mappingFilesModel;

    /**
     * Constructs a new V1DozerTransformModel.
     * @param namespace namespace
     */
    public V1DozerTransformModel(String namespace) {
        super(new QName(namespace, TransformModel.TRANSFORM + '.' + DOZER));
    }

    /**
     * Constructs a new V1DozerTransformModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1DozerTransformModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DozerMappingFilesModel getDozerMappingFiles() {
        if (_mappingFilesModel == null) {
            _mappingFilesModel = (DozerMappingFilesModel)getFirstChildModelStartsWith(DozerMappingFilesModel.MAPPING_FILES);
        }
        return _mappingFilesModel;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V1DozerTransformModel setDozerMappingFiles(DozerMappingFilesModel model) {
        setChildModel(model);
        _mappingFilesModel = model;
        return this;
    }

}
