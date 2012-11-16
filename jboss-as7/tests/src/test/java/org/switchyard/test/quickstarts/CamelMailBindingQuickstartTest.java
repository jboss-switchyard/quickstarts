/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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
package org.switchyard.test.quickstarts;

import java.io.IOException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.jvnet.mock_javamail.Mailbox;
import org.switchyard.test.ArquillianUtil;

@RunWith(Arquillian.class)
public class CamelMailBindingQuickstartTest {

    @Deployment(testable = false)
    public static JavaArchive createDeployment() throws IOException {
        JavaArchive quickstart = ArquillianUtil.createJarQSDeployment("switchyard-quickstart-camel-mail-binding");
        // copy mock javamail to archive to use mock mail providers instead of real
        quickstart.addAsResource(Mailbox.class.getResource("/META-INF/javamail.providers"),
            "/META-INF/javamail.providers");
        quickstart.addPackage(Mailbox.class.getPackage());
        return quickstart;
    }

    @Test
    public void testDeployment() throws Exception {
        Assert.assertNotNull("Dummy not null", "");
    }

}
