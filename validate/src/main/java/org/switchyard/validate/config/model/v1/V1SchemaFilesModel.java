/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU Lesser General Public License, v. 2.1.
 * This program is distributed in the hope that it will be useful, but WITHOUT A
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License,
 * v.2.1 along with this distribution; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA  02110-1301, USA.
 */

package org.switchyard.validate.config.model.v1;

import java.util.ArrayList;
import java.util.List;

import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.validate.ValidateModel;
import org.switchyard.validate.config.model.FileEntryModel;
import org.switchyard.validate.config.model.SchemaFilesModel;

import javax.xml.namespace.QName;

/**
 * A version 1 SchemaFilesModel.
 */
public class V1SchemaFilesModel extends BaseModel implements SchemaFilesModel {

    private List<FileEntryModel> _entries = new ArrayList<FileEntryModel>();
    
    /**
     * Constructs a new V1SchemaCatalogsModel.
     */
    public V1SchemaFilesModel() {
        super(new QName(ValidateModel.DEFAULT_NAMESPACE, SchemaFilesModel.SCHEMA_FILES));
    }

    /**
     * Constructs a new V1SchemaFilesModel with the specified Configuration and Descriptor.
     * @param config the Configuration
     * @param desc the Descriptor
     */
    public V1SchemaFilesModel(Configuration config, Descriptor desc) {
        super(config, desc);
        for (Configuration entryConfig : config.getChildrenStartsWith(FileEntryModel.ENTRY)) {
            FileEntryModel entry = (FileEntryModel)readModel(entryConfig);
            if (entry != null) {
                _entries.add(entry);
            }
        }
        setModelChildrenOrder(FileEntryModel.ENTRY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FileEntryModel> getEntries() {
        return _entries;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SchemaFilesModel addEntry(FileEntryModel entry) {
        addChildModel(entry);
        _entries.add(entry);
        return this;
    }
}
