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
package org.switchyard.component.camel.config.model.generic;

import org.switchyard.component.camel.config.model.CamelBindingModel;

/**
 * Binding model for file endpoints.
 * 
 * @author Lukasz Dywicki
 */
public interface GenericFileBindingModel extends CamelBindingModel {

    /**
     * The endpoint directory.
     * @return the directory
     */
    public String getDirectory();

    /**
     * Specify the endpoint consume/produce directory.
     * @param directory the target directory
     * @return a reference to this Camel binding model
     */
    public GenericFileBindingModel setDirectory(String directory);

    /**
     * Whether missing directories in the file's pathname will be created or not. 
     * For the file consumer, that means creating the starting directory. 
     * For the file producer, it means the directory to where the files should be written.
     * @return the auto-create setting or null if it has not been specified
     */
    public abstract Boolean isAutoCreate();

    /**
     * Specify whether missing directories in the file's pathname will be created or not.
     * @param autoCreate true if directories will be created, false otherwise
     * @return a reference to this Camel binding model
     */
    public abstract GenericFileBindingModel setAutoCreate(Boolean autoCreate);

    /**
     * Buffer sized in bytes.
     * @return the buffer size setting or null if it has not been specified
     */
    public abstract Integer getBufferSize();

    /**
     * Specify buffer size in bytes.
     * @param bufferSize buffer size in bytes
     * @return a reference to this Camel binding model
     */
    public abstract GenericFileBindingModel setBufferSize(Integer bufferSize);

    /**
     * Expression such as File Language to dynamically set the filename. 
     * For consumers, it's used as a filename filter. 
     * For producers, it's used to evaluate the filename to write
     * @return the file name setting or null if it has not been specified
     */
    public abstract String getFileName();

    /**
     * Specify expression such as File Language to dynamically set the filename.
     * @param fileName expression to dynamically set the filename
     * @return a reference to this Camel binding model
     */
    public abstract GenericFileBindingModel setFileName(String fileName);

    /**
     * Whether the file name path will get leading paths stripped; so it's just the file name. 
     * @return the flatten setting or null if it has not been specified
     */
    public abstract Boolean isFlatten();

    /**
     * Specify whether the file name will be stripped.
     * @param flatten true to get it flattened, false otherwise
     * @return a reference to this Camel binding model
     */
    public abstract GenericFileBindingModel setFlatten(Boolean flatten);

    /**
     * This option is used to specify the encoding of the file, and camel will 
     * set the Exchange property with Exchange.CHARSET_NAME with the value of this option. 
     * @return the charset setting or null if it has not been specified
     */
    public abstract String getCharset();

    /**
     * Specify the encoding of the file.
     * 
     * @param charset the encoding of the file
     * @return a reference to this Camel binding model
     */
    public abstract GenericFileBindingModel setCharset(String charset);

}
