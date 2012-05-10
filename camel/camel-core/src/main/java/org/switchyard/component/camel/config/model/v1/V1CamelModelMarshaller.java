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
package org.switchyard.component.camel.config.model.v1;

import org.switchyard.component.camel.config.model.atom.v1.V1CamelAtomBindingModel;
import org.switchyard.component.camel.config.model.direct.v1.V1CamelDirectBindingModel;
import org.switchyard.component.camel.config.model.file.v1.V1CamelFileBindingModel;
import org.switchyard.component.camel.config.model.file.v1.V1CamelFileConsumerBindingModel;
import org.switchyard.component.camel.config.model.file.v1.V1CamelFileProducerBindingModel;
import org.switchyard.component.camel.config.model.ftp.v1.V1CamelFtpBindingModel;
import org.switchyard.component.camel.config.model.ftps.v1.V1CamelFtpsBindingModel;
import org.switchyard.component.camel.config.model.mock.v1.V1CamelMockBindingModel;
import org.switchyard.component.camel.config.model.seda.v1.V1CamelSedaBindingModel;
import org.switchyard.component.camel.config.model.sftp.v1.V1CamelSftpBindingModel;
import org.switchyard.component.camel.config.model.timer.v1.V1CamelTimerBindingModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseMarshaller;
import org.switchyard.config.model.Descriptor;
import org.switchyard.config.model.Model;
import org.switchyard.config.model.composer.ContextMapperModel;
import org.switchyard.config.model.composer.MessageComposerModel;
import org.switchyard.config.model.composer.v1.V1ContextMapperModel;
import org.switchyard.config.model.composer.v1.V1MessageComposerModel;
import org.switchyard.config.model.composite.BindingModel;
import org.switchyard.config.model.composite.ComponentImplementationModel;

/**
 * A Marshaler that is able to read a {@link Configuration} and populate a
 * {@link Model} corresponding to the configuration informations.
 * <p>
 * 
 * @author Daniel Bevenius
 */
public class V1CamelModelMarshaller extends BaseMarshaller {

    /**
     * Sole constructor.
     * 
     * @param desc The switchyard descriptor.
     */
    public V1CamelModelMarshaller(final Descriptor desc) {
        super(desc);
    }

    @Override
    public Model read(final Configuration config) {
        String name = config.getName();

        if (name.startsWith(BindingModel.BINDING)) {

            if (name.endsWith(V1CamelFileBindingModel.FILE)) {
                return new V1CamelFileBindingModel(config, getDescriptor());
            } else if (name.endsWith(V1CamelBindingModel.CAMEL)) {
                return new V1CamelBindingModel(config, getDescriptor());
            } else if (name.endsWith(V1CamelAtomBindingModel.ATOM)) {
                return new V1CamelAtomBindingModel(config, getDescriptor());
            } else if (name.endsWith(V1CamelTimerBindingModel.TIMER)) {
                return new V1CamelTimerBindingModel(config, getDescriptor());
            } else if (name.endsWith(V1CamelDirectBindingModel.DIRECT)) {
                return new V1CamelDirectBindingModel(config, getDescriptor());
            } else if (name.endsWith(V1CamelSedaBindingModel.SEDA)) {
                return new V1CamelSedaBindingModel(config, getDescriptor());
            } else if (name.endsWith(V1CamelMockBindingModel.MOCK)) {
                return new V1CamelMockBindingModel(config, getDescriptor());
            } else if (name.endsWith(V1CamelFtpsBindingModel.FTPS)) {
                return new V1CamelFtpsBindingModel(config, getDescriptor());
            } else if (name.endsWith(V1CamelSftpBindingModel.SFTP)) {
                return new V1CamelSftpBindingModel(config, getDescriptor());
            } else if (name.endsWith(V1CamelFtpBindingModel.FTP)) {
                return new V1CamelFtpBindingModel(config, getDescriptor());
            }
        }

        if (name.endsWith(V1CamelFileBindingModel.CONSUME)) {
            return new V1CamelFileConsumerBindingModel(config, getDescriptor());
        }

        if (name.endsWith(V1CamelFileBindingModel.PRODUCE)) {
            return new V1CamelFileProducerBindingModel(config, getDescriptor());
        }

        if (name.endsWith(V1OperationSelector.OPERATION_SELECTOR)) {
            return new V1OperationSelector(config, getDescriptor());
        }

        if (name.startsWith(ComponentImplementationModel.IMPLEMENTATION)) {
            return new V1CamelImplementationModel(config, getDescriptor());
        }

        if (name.equals(ContextMapperModel.CONTEXT_MAPPER)) {
            return new V1ContextMapperModel(config, getDescriptor());
        }

        if (name.equals(MessageComposerModel.MESSAGE_COMPOSER)) {
            return new V1MessageComposerModel(config, getDescriptor());
        }

        return null;
    }

}
