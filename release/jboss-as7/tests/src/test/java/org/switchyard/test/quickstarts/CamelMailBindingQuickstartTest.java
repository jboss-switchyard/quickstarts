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
        JavaArchive quickstart = ArquillianUtil.createJarQSDeployment("switchyard-camel-mail-binding");
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
