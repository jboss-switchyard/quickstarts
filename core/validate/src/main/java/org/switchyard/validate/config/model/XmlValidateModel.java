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

package org.switchyard.validate.config.model;

import org.switchyard.config.model.validate.ValidateModel;

/**
 * A "validate.xml" configuration model.
 *
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
public interface XmlValidateModel extends ValidateModel {

    /** The "xml" name. */
    public static final String XML = "xml";
    
    /** schema file. */
    public static final String SCHEMA_FILE_URI = "schemaFile";

    /** schema language. */
    public static final String SCHEMA_TYPE = "schemaType";
    
    /** whether a warning should be reported as Exception or just log. */
    public static final String FAIL_ON_WARNING = "failOnWarning";
    
    /** whether the validator should be namespace aware or not. */
    public static final String NAMESPACE_AWARE = "namespaceAware";
    
    /**
     * Return whether a warning should be reported as an SwitchYardException.
     * If failOnWarning attribute is "true", then a warning should be reported
     * as an SwitchYardException, otherwise just log.
     * @return true if a warning should be reported as an SwitchYardException, otherwise false
     */
    boolean failOnWarning();

    /**
     * Set whether a warning should be reported as an SwitchYardException.
     * If failOnWarning attribute is "true", then a warning should be reported
     * as an SwitchYardException, otherwise just log.
     * @param failOnWarning true if a warning should be reported as an SwitchYardException, otherwise false
     * @return model representation
     */
    XmlValidateModel setFailOnWarning(boolean failOnWarning);
    
    /**
     * Return whether the validator should be namespace aware or not.
     * @return true if namespace aware
     */
    boolean namespaceAware();
    
    /**
     * Set whether the validator should be namespace aware or not.
     * @param namespaceAware true if namespace aware
     * @return model representation
     */
    XmlValidateModel setNamespaceAware(boolean namespaceAware);
    
    /**
     * Get a SchemaFiles model.
     * @return a SchemaFiles model
     */
    SchemaFilesModel getSchemaFiles();

    /**
     * Set a SchemaFiles model.
     * @param schemaFiles a SchemaFiles model
     * @return model representation
     */
    XmlValidateModel setSchemaFiles(SchemaFilesModel schemaFiles);
    
    /**
     * Get a SchemaCatalogs model.
     * @return a SchemaCatalogs model
     */
    SchemaCatalogsModel getSchemaCatalogs();
    
    /**
     * Set a SchemaCatalogs model.
     * @param schemaCatalogs a SchemaCatalogs model
     * @return model representation
     */
    XmlValidateModel setSchemaCatalogs(SchemaCatalogsModel schemaCatalogs);

    /**
     * Get schema type.
     * @return schema type
     */
    XmlSchemaType getSchemaType();
    
    /**
     * Set schema type.
     * @param type schema type
     * @return model representation
     */
    XmlValidateModel setSchemaType(XmlSchemaType type);
}
