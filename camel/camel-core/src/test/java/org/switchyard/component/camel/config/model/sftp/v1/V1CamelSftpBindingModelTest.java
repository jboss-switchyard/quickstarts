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
package org.switchyard.component.camel.config.model.sftp.v1;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import org.apache.camel.component.file.remote.SftpEndpoint;
import org.junit.Test;
import org.switchyard.component.camel.config.model.sftp.CamelSftpBindingModel;
import org.switchyard.component.camel.config.model.v1.V1BaseCamelModelTest;
import org.switchyard.config.model.Validation;

/**
 * Test for {@link V1CamelSftpBindingModel}.
 * 
 * @author Lukasz Dywicki
 */
public class V1CamelSftpBindingModelTest extends V1BaseCamelModelTest<V1CamelSftpBindingModel> {

    private static final String CAMEL_XML = "switchyard-sftp-binding-beans.xml";

    private static final String CAMEL_URI = "sftp://localhost:9022/test?knownHostsFile=known_hosts"
        + "&privateKeyFile=my.key&privateKeyFilePassphrase=test";

    private static final String KNOWN_HOSTS = "known_hosts";
    private static final String PRIVATE_KEY = "my.key";
    private static final String PRIVATE_KEY_PASSPHRASE = "test";

    @Test
    public void validateCamelBindingModelWithBeanElement() throws Exception {
        final V1CamelSftpBindingModel bindingModel = getFirstCamelBinding(CAMEL_XML);
        final Validation validateModel = bindingModel.validateModel();

        assertTrue(validateModel.isValid());
    }

    @Test
    public void testCamelEndpoint()  throws Exception {
        CamelSftpBindingModel model = getFirstCamelBinding(CAMEL_XML);

        SftpEndpoint endpoint = getEndpoint(model, SftpEndpoint.class);
        assertEquals(V1CamelSftpBindingModel.SFTP, endpoint.getConfiguration().getProtocol());
        assertEquals(KNOWN_HOSTS, endpoint.getConfiguration().getKnownHostsFile());
        assertEquals(PRIVATE_KEY, endpoint.getConfiguration().getPrivateKeyFile());
        assertEquals(PRIVATE_KEY_PASSPHRASE, endpoint.getConfiguration().getPrivateKeyFilePassphrase());
        assertEquals(CAMEL_URI, endpoint.getEndpointUri().toString());
    }

}
