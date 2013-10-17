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

package org.switchyard.config.model.switchyard.test.validate.xml;

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.validate.ValidateModel;
import org.switchyard.config.model.validate.v1.V1BaseValidateModel;

/**
 * XmlValidateModel.
 *
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
public class XmlValidateModel extends V1BaseValidateModel {

    public static final String XML = "xml";
    public static final String SCHEMA_TYPE = "schemaType";
    public static final String SCHEMA_FILE = "schemaFile";
    public static final String FAIL_ON_WARN = "failOnWarn";

    public XmlValidateModel() {
        super(new QName("urn:switchyard-config:test-validate-xml:1.0", ValidateModel.VALIDATE + '.' + XML));
    }

    public XmlValidateModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    public String getSchemaType() {
        return getModelAttribute(SCHEMA_TYPE);
    }

    public XmlValidateModel setSchemaType(String type) {
        setModelAttribute(SCHEMA_TYPE, type);
        return this;
    }

    public String getSchemaFile() {
        return getModelAttribute(SCHEMA_FILE);
    }

    public XmlValidateModel setSchemaFile(String file) {
        setModelAttribute(SCHEMA_FILE, file);
        return this;
    }

    public boolean getFailOnWarning() {
        String fow = getModelAttribute(FAIL_ON_WARN);
        return Boolean.parseBoolean(fow);
    }

    public XmlValidateModel setFailOnWarning(boolean fow) {
        setModelAttribute(FAIL_ON_WARN, Boolean.toString(fow));
        return this;
    }

}
