/*
 * JBoss, Home of Professional Open Source Copyright 2009, Red Hat Middleware
 * LLC, and individual contributors by the @authors tag. See the copyright.txt
 * in the distribution for a full listing of individual contributors.
 * 
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.switchyard.component.camel.common.model.file.v1;

import org.switchyard.component.camel.common.model.file.GenericFileBindingModel;
import org.switchyard.component.camel.common.model.v1.V1BaseCamelBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * Base class for bindings of file based endpoints.
 * 
 * @author Lukasz Dywicki
 */
public abstract class V1GenericFileBindingModel extends V1BaseCamelBindingModel 
    implements GenericFileBindingModel {

    /**
     * The name of the 'targetDir' element.
     */
    public static final String DIRECTORY = "directory";

    /**
     * The name of the 'autoCreate' element.
     */
    private static final String AUTO_CREATE = "autoCreate";

    /**
     * The name of the 'bufferSize' element.
     */
    private static final String BUFFER_SIZE = "bufferSize";

    /**
     * The name of the 'fileName' element.
     */
    private static final String FILE_NAME = "fileName";

    /**
     * The name of the 'flatten' element.
     */
    private static final String FLATTEN = "flatten";

    /**
     * The name of the 'charset' element.
     */
    private static final String CHARSET = "charset";

    /**
     * Creates a binding with specified type.
     * 
     * @param type Type of binding.
     * @param namespace Namespace of the binding
     */
    protected V1GenericFileBindingModel(String type, String namespace) {
        super(type, namespace);
 
        setModelChildrenOrder(DIRECTORY, AUTO_CREATE, BUFFER_SIZE, FILE_NAME, FLATTEN, CHARSET);
    }

    /**
     * Create a binding from the specified configuration and descriptor.
     * 
     * @param config The switchyard configuration instance.
     * @param desc The switchyard descriptor instance.
     */
    protected V1GenericFileBindingModel(Configuration config, Descriptor desc) {
        super(config, desc);
    }

    @Override
    public String getDirectory() {
        return getConfig(DIRECTORY);
    }

    @Override
    public V1GenericFileBindingModel setDirectory(String targetDir) {
        return setConfig(DIRECTORY, targetDir);
    }

    @Override
    public Boolean isAutoCreate() {
        return getBooleanConfig(AUTO_CREATE);
    }

    @Override
    public V1GenericFileBindingModel setAutoCreate(Boolean autoCreate) {
        return setConfig(AUTO_CREATE, autoCreate);
    }

    @Override
    public Integer getBufferSize() {
        return getIntegerConfig(BUFFER_SIZE);
    }

    @Override
    public V1GenericFileBindingModel setBufferSize(Integer bufferSize) {
        return setConfig(BUFFER_SIZE, bufferSize);
    }

    @Override
    public String getFileName() {
        return getConfig(FILE_NAME);
    }

    @Override
    public V1GenericFileBindingModel setFileName(String fileName) {
        return setConfig(FILE_NAME, fileName);
    }

    @Override
    public Boolean isFlatten() {
        return getBooleanConfig(FLATTEN);
    }

    @Override
    public V1GenericFileBindingModel setFlatten(Boolean flatten) {
        return setConfig(FLATTEN, flatten);
    }

    @Override
    public String getCharset() {
        return getConfig(CHARSET);
    }

    @Override
    public V1GenericFileBindingModel setCharset(String charset) {
        return setConfig(CHARSET, charset);
    }

}
