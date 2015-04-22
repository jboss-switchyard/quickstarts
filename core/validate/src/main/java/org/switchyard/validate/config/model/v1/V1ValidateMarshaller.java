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

package org.switchyard.validate.config.model.v1;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseMarshaller;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.Model;
import org.switchyard.config.model.validate.ValidateModel;
import org.switchyard.validate.config.model.FileEntryModel;
import org.switchyard.validate.config.model.JavaValidateModel;
import org.switchyard.validate.config.model.SchemaCatalogsModel;
import org.switchyard.validate.config.model.SchemaFilesModel;
import org.switchyard.validate.config.model.XmlValidateModel;

/**
 * Marshalls validate Models.
 *
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
public class V1ValidateMarshaller extends BaseMarshaller {

    private static final String VALIDATE_JAVA = ValidateModel.VALIDATE + "." + JavaValidateModel.JAVA;
    private static final String VALIDATE_XML = ValidateModel.VALIDATE + "." + XmlValidateModel.XML;

    /**
     * Constructs a new V1ValidateMarshaller with the specified Descriptor.
     * @param desc the Descriptor
     */
    public V1ValidateMarshaller(Descriptor desc) {
        super(desc);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Model read(Configuration config) {
        String name = config.getName();
        Descriptor desc = getDescriptor();

        if (name.equals(VALIDATE_JAVA)) {
            return new V1JavaValidateModel(config, desc);
        } else if (name.equals(VALIDATE_XML)) {
            return new V1XmlValidateModel(config, desc);
        } else if (name.equals(SchemaFilesModel.SCHEMA_FILES)) {
            return new V1SchemaFilesModel(config, desc);
        } else if (name.equals(SchemaCatalogsModel.SCHEMA_CATALOGS)) {
            return new V1SchemaCatalogsModel(config, desc);
        } else if (name.equals(FileEntryModel.ENTRY)) {
            return new V1FileEntryModel(config, desc);
        }

        return null;
    }

}
