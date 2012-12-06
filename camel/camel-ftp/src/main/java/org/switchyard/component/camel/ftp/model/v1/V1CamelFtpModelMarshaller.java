/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
