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
package org.switchyard.component.camel.config.model.generic;

/**
 * Binding model for file based producers.
 * 
 * @author Lukasz Dywicki
 */
public interface GenericFileProducerBindingModel {

    /**
     * What to do if a file already exists with the same name. The following
     * values can be obtained: Override, Append, Fail and Ignore.
     * 
     * @return the fileExist value
     */
    public abstract String getFileExist();

    /**
     * Specify what to do if a file already exists with the same name.
     * 
     * @param fileExist
     *            one of these: Override, Append, Fail or Ignore
     * @return a reference to this Camel File binding model
     */
    public abstract GenericFileProducerBindingModel setFileExist(String fileExist);

    /**
     * This option is used to write the file using a temporary name and then,
     * after the write is complete, rename it to the real name.
     * 
     * @return the tempPrefix value
     */
    public abstract String getTempPrefix();

    /**
     * Specify the name of the temporary name when writting it. After the write
     * is complete, it will be renamed to the real name.
     * 
     * @param tempPrefix
     *            the temporary file name
     * @return a reference to this Camel File binding model
     */
    public abstract GenericFileProducerBindingModel setTempPrefix(
            String tempPrefix);

    /**
     * Camel 2.1: The same as tempPrefix option but offering a more fine grained
     * control on the naming of the temporary filename.
     * 
     * @return the temporary file name value
     */
    public abstract String getTempFileName();

    /**
     * Camel 2.1: The same as tempPrefix option but offering a more fine grained
     * control on the naming of the temporary filename.
     * 
     * @param tempFileName
     *            the temporary file name
     * @return a reference to this Camel File binding model
     */
    public abstract GenericFileProducerBindingModel setTempFileName(
            String tempFileName);

    /**
     * Camel 2.2: Will keep the last modified timestamp from the source file (if
     * any).
     * 
     * @return true to keep the last modified timestamp; false otherwise
     */
    public abstract Boolean isKeepLastModified();

    /**
     * Camel 2.2: Will keep the last modified timestamp from the source file (if
     * any).
     * 
     * @param keepLastModified
     *            whether to keep the last modified timestamp (true), or not
     *            (false)
     * @return a reference to this Camel File binding model
     */
    public abstract GenericFileProducerBindingModel setKeepLastModified(
            Boolean keepLastModified);

    /**
     * Camel 2.3: Whether or not to eagerly delete any existing target file.
     * 
     * @return true if eagerly delete existing target file; false otherwise
     */
    public abstract Boolean isEagerDeleteTargetFile();

    /**
     * Camel 2.3: Whether or not to eagerly delete any existing target file.
     * 
     * @param eagerDeleteTargetFile
     *            true if eagerly delete existing target file; false otherwise
     * @return a reference to this Camel File binding model
     */
    public abstract GenericFileProducerBindingModel setEagerDeleteTargetFile(
            Boolean eagerDeleteTargetFile);

    /**
     * Camel 2.6: If provided, then Camel will write a 2nd done file when the
     * original file has been written.
     * 
     * @return the file name to use
     */
    public abstract String getDoneFileName();

    /**
     * If provided, then Camel will write a 2nd done file when the original file
     * has been written.
     * 
     * @param doneFileName
     *            the file name to use
     * @return a reference to this Camel File binding model
     */
    public abstract GenericFileProducerBindingModel setDoneFileName(
            String doneFileName);

}
