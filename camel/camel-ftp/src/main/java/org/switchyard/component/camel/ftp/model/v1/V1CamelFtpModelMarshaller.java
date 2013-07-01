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

import static org.switchyard.component.camel.ftp.Constants.FTP_NAMESPACE_V1;

import org.switchyard.component.camel.common.marshaller.BaseModelMarshaller;
import org.switchyard.component.camel.common.marshaller.ModelCreator;
import org.switchyard.component.camel.ftps.model.v1.V1CamelFtpsBindingModel;
import org.switchyard.component.camel.sftp.model.v1.V1CamelSftpBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.Descriptor;

/**
 * Ftp/ftps/sftp model marshaller.
 */
public class V1CamelFtpModelMarshaller extends BaseModelMarshaller {

    /**
     * Creates new marshaller.
     * 
     * @param desc Descriptor
     */
    public V1CamelFtpModelMarshaller(Descriptor desc) {
        super(desc, FTP_NAMESPACE_V1);

        registerBinding(V1CamelFtpBindingModel.FTP, new ModelCreator<V1CamelFtpBindingModel>() {
            @Override
            public V1CamelFtpBindingModel create(Configuration config, Descriptor descriptor) {
                return new V1CamelFtpBindingModel(config, descriptor);
            }
        });

        registerBinding(V1CamelFtpsBindingModel.FTPS, new ModelCreator<V1CamelFtpsBindingModel>() {
            @Override
            public V1CamelFtpsBindingModel create(Configuration config, Descriptor descriptor) {
                return new V1CamelFtpsBindingModel(config, descriptor);
            }
        });

        registerBinding(V1CamelSftpBindingModel.SFTP, new ModelCreator<V1CamelSftpBindingModel>() {
            @Override
            public V1CamelSftpBindingModel create(Configuration config, Descriptor descriptor) {
                return new V1CamelSftpBindingModel(config, descriptor);
            }
        });
    }

}
