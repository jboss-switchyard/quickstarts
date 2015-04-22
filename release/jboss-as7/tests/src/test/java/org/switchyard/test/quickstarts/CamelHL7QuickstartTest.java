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

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.hl7.HL7MLLPCodec;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.test.ArquillianUtil;

/**
 * Test to demonstrate deployment of the HL7 Quickstart.
 */
@RunWith(Arquillian.class)
public class CamelHL7QuickstartTest {

    @Deployment(testable = false)
    public static JavaArchive createDeployment() {
        return ArquillianUtil.createJarQSDeployment("switchyard-camel-hl7");
    }

    @Test
    public void testDeployment() throws Exception {
        SimpleRegistry registry = new SimpleRegistry();
        HL7MLLPCodec codec = new HL7MLLPCodec();
        codec.setCharset("iso-8859-1");
        codec.setConvertLFtoCR(true);

        registry.put("hl7codec", codec);
        CamelContext camelContext = null;
        ProducerTemplate template = null;
        
        try {
            camelContext = new DefaultCamelContext(registry);
            camelContext.start();
            template = camelContext.createProducerTemplate();
            String line1 = "MSH|^~\\&|MYSENDER|MYRECEIVER|MYAPPLICATION||200612211200||QRY^A19|1234|P|2.4";
            String line2 = "QRD|200612211200|R|I|GetPatient|||1^RD|0101701234|DEM||";

            StringBuilder in = new StringBuilder();
            in.append(line1);
            in.append("\r");
            in.append(line2);

            template.requestBody("mina2:tcp://127.0.0.1:8888?sync=true&codec=#hl7codec", in.toString());
        } finally {
            if (template != null) {
                try {
                    template.stop();
                } catch (Exception e) {
                }
            }
            if (camelContext != null) {
                try {
                    camelContext.stop();
                } catch (Exception e) {
                }
            }
        }
    }

}
