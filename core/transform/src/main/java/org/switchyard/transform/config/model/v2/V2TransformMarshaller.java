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

package org.switchyard.transform.config.model.v2;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.Model;
import org.switchyard.config.model.transform.TransformModel;
import org.switchyard.transform.config.model.DozerFileEntryModel;
import org.switchyard.transform.config.model.DozerMappingFilesModel;
import org.switchyard.transform.config.model.DozerTransformModel;
import org.switchyard.transform.config.model.v1.V1TransformMarshaller;

/**
 * Marshalls transform Models.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
public class V2TransformMarshaller extends V1TransformMarshaller {

    private static final String TRANSFORM_DOZER = TransformModel.TRANSFORM + "." + DozerTransformModel.DOZER;

    /**
     * Constructs a new V2TransformMarshaller with the specified Descriptor.
     * @param desc the Descriptor
     */
    public V2TransformMarshaller(Descriptor desc) {
        super(desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Model read(Configuration config) {
        Model model = super.read(config);
        if (model != null) {
            return model;
        }

        String name = config.getName();
        Descriptor desc = getDescriptor();
        if (name.equals(TRANSFORM_DOZER)) {
            return new V2DozerTransformModel(config, desc);
        } else if (name.equals(DozerMappingFilesModel.MAPPING_FILES)) {
            return new V2DozerMappingFilesModel(config, desc);
        } else if (name.equals(DozerFileEntryModel.ENTRY)) {
            return new V2DozerFileEntryModel(config, desc);
        }

        return null;
    }

}
