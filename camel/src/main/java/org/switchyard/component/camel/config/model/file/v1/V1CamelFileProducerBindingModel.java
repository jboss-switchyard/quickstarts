/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
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

package org.switchyard.component.camel.config.model.file.v1;

import javax.xml.namespace.QName;

import org.switchyard.component.camel.config.model.file.CamelFileProducerBindingModel;
import org.switchyard.component.camel.config.model.v1.NameValueModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * A binding for Camel's file component, for producer configurations.
 * 
 * @author Mario Antollini
 * 
 */
public class V1CamelFileProducerBindingModel extends BaseModel implements
        CamelFileProducerBindingModel {

    /**
     * The name of the 'fileExist' element.
     */
    public static final String FILE_EXIST = "fileExist";

    /**
     * The name of the 'tempPrefix' element.
     */
    public static final String TEMP_PREFIX = "tempPrefix";

    /**
     * The name of the 'tempFileName' element.
     */
    public static final String TEMP_FILENAME = "tempFileName";

    /**
     * The name of the 'keepLastModified' element.
     */
    public static final String KEEP_LAST_MODIFIED = "keepLastModified";

    /**
     * The name of the 'eagerDeleteTargetFile' element.
     */
    public static final String EAGER_DELETE_TARGET_FILE = "eagerDeleteTargetFile";

    /**
     * The name of the 'doneFileName' element.
     */
    public static final String DONE_FILE_NAME = "doneFileName";

    /**
     * Create a new V1CamelFileProducerBindingModel.
     */
    public V1CamelFileProducerBindingModel() {
        super(new QName(V1CamelFileBindingModel.PRODUCE));
        setModelChildrenOrder(FILE_EXIST, TEMP_PREFIX, TEMP_FILENAME,
                KEEP_LAST_MODIFIED, EAGER_DELETE_TARGET_FILE, DONE_FILE_NAME);
    }

    /**
     * Create a V1CamelFileProducerBindingModel from the specified configuration
     * and descriptor.
     * 
     * @param config
     *            The switchyard configuration instance.
     * @param desc
     *            The switchyard descriptor instance.
     */
    public V1CamelFileProducerBindingModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    @Override
    public String getFileExist() {
        return getConfig(FILE_EXIST);
    }

    @Override
    public V1CamelFileProducerBindingModel setFileExist(String fileExist) {
        setConfig(FILE_EXIST, fileExist);
        return this;
    }

    @Override
    public String getTempPrefix() {
        return getConfig(TEMP_PREFIX);
    }

    @Override
    public V1CamelFileProducerBindingModel setTempPrefix(String tempPrefix) {
        setConfig(TEMP_PREFIX, tempPrefix);
        return this;
    }

    @Override
    public String getTempFileName() {
        return getConfig(TEMP_FILENAME);
    }

    @Override
    public V1CamelFileProducerBindingModel setTempFileName(String tempFileName) {
        setConfig(TEMP_FILENAME, tempFileName);
        return this;
    }

    @Override
    public Boolean isKeepLastModified() {
        return getBooleanConfig(KEEP_LAST_MODIFIED);
    }

    @Override
    public V1CamelFileProducerBindingModel setKeepLastModified(
            Boolean keepLastModified) {
        setConfig(KEEP_LAST_MODIFIED, String.valueOf(keepLastModified));
        return this;
    }

    @Override
    public Boolean isEagerDeleteTargetFile() {
        return getBooleanConfig(EAGER_DELETE_TARGET_FILE);
    }

    @Override
    public V1CamelFileProducerBindingModel setEagerDeleteTargetFile(
            Boolean eagerDeleteTargetFile) {
        setConfig(EAGER_DELETE_TARGET_FILE,
                String.valueOf(eagerDeleteTargetFile));
        return this;
    }

    @Override
    public String getDoneFileName() {
        return getConfig(DONE_FILE_NAME);
    }

    @Override
    public V1CamelFileProducerBindingModel setDoneFileName(String doneFileName) {
        setConfig(DONE_FILE_NAME, doneFileName);
        return this;
    }

    private Boolean getBooleanConfig(String configName) {
        String value = getConfig(configName);
        return value != null ? Boolean.valueOf(value) : null;
    }

    private String getConfig(String configName) {
        Configuration config = getModelConfiguration()
                .getFirstChild(configName);
        if (config != null) {
            return config.getValue();
        } else {
            return null;
        }
    }

    private void setConfig(String name, String value) {
        Configuration config = getModelConfiguration().getFirstChild(name);
        if (config != null) {
            // set an existing config value
            config.setValue(value);
        } else {
            // create the config model and set the value
            NameValueModel model = new NameValueModel(name);
            model.setValue(value);
            setChildModel(model);
        }
    }

}
