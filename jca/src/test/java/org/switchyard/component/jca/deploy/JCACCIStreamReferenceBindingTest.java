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
package org.switchyard.component.jca.deploy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.naming.InitialContext;
import javax.resource.cci.InteractionSpec;
import javax.resource.cci.Record;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.component.jca.processor.cci.StreamableRecord;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.component.test.mixins.jca.InteractionListener;
import org.switchyard.component.test.mixins.jca.JCAMixIn;
import org.switchyard.component.test.mixins.jca.MockConnectionFactory;
import org.switchyard.component.test.mixins.jca.MockManagedConnectionFactory;
import org.switchyard.component.test.mixins.jca.ResourceAdapterConfig;
import org.switchyard.test.BeforeDeploy;
import org.switchyard.test.Invoker;
import org.switchyard.test.ServiceOperation;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;

/**
 * Functional test for outbound JCA with streamable record.
 *
 * @author <a href="mailto:tm.igarashi@gmail.com">Tomohisa Igarashi</a>
 * @author Antti Laisi
 *
 */
@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(config = "switchyard-outbound-cci-stream-test.xml", mixins = {CDIMixIn.class, JCAMixIn.class})
public class JCACCIStreamReferenceBindingTest {

    private static final String JNDI_CONNECTION_FACTORY = "java:jboss/MyEISConnectionFactory";
    private JCAMixIn _jcaMixIn;

    @ServiceOperation("JCACCIStreamReferenceService.onMessage")
    private Invoker _service;

    @BeforeDeploy
    public void before() {
        ResourceAdapterConfig ra = new ResourceAdapterConfig(ResourceAdapterConfig.ResourceAdapterType.MOCK)
                                        .setName("myeis-ra.rar")
                                        .addConnectionDefinition(JNDI_CONNECTION_FACTORY, MockManagedConnectionFactory.class.getName());
        _jcaMixIn.deployResourceAdapters(ra);

        MockConnectionFactory factory = null;
        try {
            factory = (MockConnectionFactory) new InitialContext().lookup(JNDI_CONNECTION_FACTORY);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail(e.getMessage());
        }

        factory.setInteractionListener(new InteractionListener() {
            public Record onExecute(InteractionSpec spec, Record input) { return null; }
            public boolean onExecute(InteractionSpec spec, Record input, Record output) {
                StreamableRecord in = (StreamableRecord) input;
                StreamableRecord out = (StreamableRecord) output;
                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    in.write(baos);
                    String msg = "Hello, " + new String(baos.toByteArray()) + "!";
                    out.read(new ByteArrayInputStream(msg.getBytes()));
                } catch(Exception e) {
                    e.printStackTrace();
                    Assert.fail(e.getMessage());
                }
                return true;
            }
        });
    }

    @Test
    public void testOutboundCCI() throws Exception {
        InputStream payload = new ByteArrayInputStream("Antti".getBytes());
        InputStream out = _service.sendInOut(payload).getContent(InputStream.class);
        byte[] name = new byte[out.available()];
        out.read(name);
        Assert.assertEquals("Hello, Antti!", new String(name));
    }

}
