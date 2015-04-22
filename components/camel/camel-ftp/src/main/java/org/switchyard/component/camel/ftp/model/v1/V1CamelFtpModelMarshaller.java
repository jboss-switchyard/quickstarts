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
package org.switchyard.component.camel.ftp.model.v1;

import org.switchyard.component.camel.common.model.v1.V1BaseCamelMarshaller;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.Model;
import org.switchyard.config.model.composite.BindingModel;

/**
 * Ftp/ftps/sftp model marshaller.
 */
public class V1CamelFtpModelMarshaller extends V1BaseCamelMarshaller {

    private static final String BINDING_FTP = BindingModel.BINDING + '.' + V1CamelFtpBindingModel.FTP;
    private static final String BINDING_FTPS = BindingModel.BINDING + '.' + V1CamelFtpsBindingModel.FTPS;
    private static final String BINDING_SFTP = BindingModel.BINDING + '.' + V1CamelSftpBindingModel.SFTP;

    /**
     * Creates new marshaller.
     * 
     * @param desc Descriptor
     */
    public V1CamelFtpModelMarshaller(Descriptor desc) {
        super(desc);
    }

    /**
     * Reads in the Configuration, looking for various knowledge models.
     * If not found, it falls back to the super class (V1BaseCamelMarshaller).
     *
     * @param config the Configuration
     * @return the Model
     */
    @Override
    public Model read(Configuration config) {
        String name = config.getName();
        Descriptor desc = getDescriptor();
        if (BINDING_FTP.equals(name)) {
            return new V1CamelFtpBindingModel(config, desc);
        } else if (BINDING_FTPS.equals(name)) {
            return new V1CamelFtpsBindingModel(config, desc);
        } else if (BINDING_SFTP.equals(name)) {
            return new V1CamelSftpBindingModel(config, desc);
        }
        return super.read(config);
    }
 
}
