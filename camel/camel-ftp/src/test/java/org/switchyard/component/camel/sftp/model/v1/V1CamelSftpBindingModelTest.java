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
package org.switchyard.component.camel.sftp.model.v1;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import org.apache.camel.component.file.remote.SftpEndpoint;
import org.junit.Test;
import org.switchyard.component.camel.config.test.v1.V1BaseCamelServiceBindingModelTest;
import org.switchyard.config.model.Validation;

/**
 * Test for {@link V1CamelSftpBindingModel}.
 * 
 * @author Lukasz Dywicki
 */
public class V1CamelSftpBindingModelTest extends V1BaseCamelServiceBindingModelTest<V1CamelSftpBindingModel, SftpEndpoint> {

    private static final String CAMEL_XML = "/v1/switchyard-sftp-binding-beans.xml";

    private static final String DIRECTORY = "test";
    private static final String HOST = "localhost";
    private static final int PORT = 9022;
    private static final String KNOWN_HOSTS = "known_hosts";
    private static final String PRIVATE_KEY = "my.key";
    private static final String PRIVATE_KEY_PASSPHRASE = "test";

    private static final String CAMEL_URI = "sftp://localhost:9022/test?knownHostsFile=known_hosts"
        + "&privateKeyFile=my.key&privateKeyFilePassphrase=test";

    public V1CamelSftpBindingModelTest() {
        super(SftpEndpoint.class, CAMEL_XML);
    }

    @Test
    public void validateCamelBindingModelWithBeanElement() throws Exception {
        final V1CamelSftpBindingModel bindingModel = getFirstCamelBinding(CAMEL_XML);
        final Validation validateModel = bindingModel.validateModel();

        assertTrue(validateModel.getMessage(), validateModel.isValid());
    }

    @Override
    protected void createModelAssertions(V1CamelSftpBindingModel model) {
        assertEquals(DIRECTORY, model.getDirectory());
        assertEquals(HOST, model.getHost());
        assertEquals(KNOWN_HOSTS, model.getKnownHostsFile());
        assertEquals(PRIVATE_KEY, model.getPrivateKeyFile());
        assertEquals(PRIVATE_KEY_PASSPHRASE, model.getPrivateKeyFilePassphrase());
    }

    @Override
    protected V1CamelSftpBindingModel createTestModel() {
        V1CamelSftpBindingModel model = (V1CamelSftpBindingModel) new V1CamelSftpBindingModel()
            .setDirectory(DIRECTORY)
            .setHost(HOST)
            .setPort(PORT);

        return model.setKnownHostsFile(KNOWN_HOSTS)
            .setPrivateKeyFile(PRIVATE_KEY)
            .setPrivateKeyFilePassphrase(PRIVATE_KEY_PASSPHRASE);
    }

    @Override
    protected String createEndpointUri() {
        return CAMEL_URI;
    }


}
