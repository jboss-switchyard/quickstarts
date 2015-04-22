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

import javax.xml.namespace.QName;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.validate.ValidateModel;
import org.switchyard.config.model.validate.v1.V1BaseValidateModel;
import org.switchyard.validate.config.model.SchemaCatalogsModel;
import org.switchyard.validate.config.model.SchemaFilesModel;
import org.switchyard.validate.config.model.XmlSchemaType;
import org.switchyard.validate.config.model.XmlValidateModel;
import org.switchyard.validate.internal.ValidatorFactoryClass;
import org.switchyard.validate.xml.internal.XmlValidatorFactory;

/**
 * A version 1 XmlValidateModel.
 *
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 */
@ValidatorFactoryClass(XmlValidatorFactory.class)
public class V1XmlValidateModel extends V1BaseValidateModel implements XmlValidateModel {

    private SchemaFilesModel _schemaList;
    private SchemaCatalogsModel _catalogList;
    
    /**
     * Constructs a new V1XmlValidateModel.
     * @param namespace namespace
     */
    public V1XmlValidateModel(String namespace) {
        super(new QName(namespace, ValidateModel.VALIDATE + '.' + XML));
    }

    /**
     * Constructs a new V1XmlValidateModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1XmlValidateModel(Configuration config, Descriptor desc) {
        super(config, desc);
        setModelChildrenOrder(SchemaFilesModel.SCHEMA_FILES, SchemaCatalogsModel.SCHEMA_CATALOGS);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean failOnWarning() {
        String fow = getModelAttribute(FAIL_ON_WARNING);
        return Boolean.parseBoolean(fow);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public XmlValidateModel setFailOnWarning(boolean failOnWarning) {
        setModelAttribute(FAIL_ON_WARNING, Boolean.toString(failOnWarning));
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean namespaceAware() {
        String na = getModelAttribute(NAMESPACE_AWARE);
        return Boolean.parseBoolean(na);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public XmlValidateModel setNamespaceAware(boolean namespaceAware) {
        setModelAttribute(NAMESPACE_AWARE, Boolean.toString(namespaceAware));
        return this;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public SchemaFilesModel getSchemaFiles() {
        if (_schemaList == null) {
            _schemaList = (SchemaFilesModel)getFirstChildModelStartsWith(SchemaFilesModel.SCHEMA_FILES);
        }
        return _schemaList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public XmlValidateModel setSchemaFiles(SchemaFilesModel schemas) {
        setChildModel(schemas);
        _schemaList = schemas;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SchemaCatalogsModel getSchemaCatalogs() {
        if (_catalogList == null) {
            _catalogList = (SchemaCatalogsModel)getFirstChildModelStartsWith(SchemaCatalogsModel.SCHEMA_CATALOGS);
        }
        return _catalogList;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public XmlValidateModel setSchemaCatalogs(SchemaCatalogsModel catalogs) {
        setChildModel(catalogs);
        _catalogList = catalogs;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public XmlSchemaType getSchemaType() {
        String type = getModelAttribute(SCHEMA_TYPE);
        return type != null ? XmlSchemaType.valueOf(type) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public XmlValidateModel setSchemaType(XmlSchemaType type) {
        if (type != null) {
            setModelAttribute(SCHEMA_TYPE, type.toString());
        }
        return this;
    }
}
