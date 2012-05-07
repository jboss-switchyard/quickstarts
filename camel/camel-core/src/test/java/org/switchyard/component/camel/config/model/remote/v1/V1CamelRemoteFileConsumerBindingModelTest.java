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
package org.switchyard.component.camel.config.model.remote.v1;

import static org.junit.Assert.assertEquals;

import org.apache.camel.component.file.remote.RemoteFileEndpoint;
import org.junit.Test;
import org.switchyard.component.camel.config.model.OperationSelector;
import org.switchyard.component.camel.config.model.ftp.v1.V1CamelFtpBindingModel;
import org.switchyard.component.camel.config.model.remote.CamelRemoteFileBindingModel;
import org.switchyard.component.camel.config.model.remote.CamelRemoteFileConsumerBindingModel;
import org.switchyard.component.camel.config.model.v1.V1BaseCamelModelTest;
import org.switchyard.component.camel.config.model.v1.V1OperationSelector;

/**
 * Test for {@link V1CamelRemoteFileConsumerBindingModel}.
 * 
 * @author Lukasz Dywicki
 */
public class V1CamelRemoteFileConsumerBindingModelTest extends V1BaseCamelModelTest<V1CamelRemoteFileBindingModel> {

    private static final boolean RECURSIVE = true;
    private static final Integer INITIAL_DELAY = 100;
    private static final String OPERATION_NAME = "print";
    private static final int PORT = 1000;
    private static final String HOST = "local";
    private static final String USERNAME = "abba";

    private static final String CAMEL_ENDPOINT_URI = "ftp://abba@local:1000?initialDelay=100&recursive=true";

    @Test
    public void testCamelEndpoint() {
        CamelRemoteFileBindingModel model = createFileConsumerModel();
        RemoteFileEndpoint<?> endpoint = getEndpoint(model, RemoteFileEndpoint.class);

        assertEquals(USERNAME, endpoint.getConfiguration().getUsername());
        assertEquals(HOST, endpoint.getConfiguration().getHost());
        assertEquals(PORT, endpoint.getConfiguration().getPort());

        assertEquals(INITIAL_DELAY, model.getConsumer().getInitialDelay());
        assertEquals(RECURSIVE, model.getConsumer().getRecursive());
        assertEquals(OPERATION_NAME, model.getOperationSelector().getOperationName());

        assertEquals(model.getComponentURI().toString(), CAMEL_ENDPOINT_URI);
    }

    private CamelRemoteFileBindingModel createFileConsumerModel() {
        OperationSelector operationSelector = new V1OperationSelector();
        operationSelector.setOperationName(OPERATION_NAME);

        // use FTP as most known binding
        V1CamelRemoteFileBindingModel fileModel = new V1CamelFtpBindingModel();
        fileModel.setUsername(USERNAME);
        fileModel.setHost(HOST);
        fileModel.setPort(PORT);
        fileModel.setOperationSelector(operationSelector);

        CamelRemoteFileConsumerBindingModel consumer = new V1CamelRemoteFileConsumerBindingModel()
            .setInitialDelay(INITIAL_DELAY)
            .setRecursive(RECURSIVE);

        fileModel.setConsumer(consumer);

        return fileModel;
    }

}
