/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
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
package org.switchyard.quickstarts.camel.hl7example;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.apache.camel.component.hl7.HL7MLLPCodec;

/**
 * Unit test for HL7 routing.
 */
public class HL7Client {
    private static int MINA2_PORT = 8888;
    

    public void testSendA19() throws Exception {

        SimpleRegistry registry = new SimpleRegistry();
        HL7MLLPCodec codec = new HL7MLLPCodec();
        codec.setCharset("iso-8859-1");
        codec.setConvertLFtoCR(true);

        registry.put("hl7codec", codec);
        CamelContext camelContext = new DefaultCamelContext(registry);
        camelContext.start();
        ProducerTemplate template = camelContext.createProducerTemplate();
        String line1 = "MSH|^~\\&|MYSENDER|MYRECEIVER|MYAPPLICATION||200612211200||QRY^A19|1234|P|2.4";
        String line2 = "QRD|200612211200|R|I|GetPatient|||1^RD|0101701234|DEM||";

        StringBuilder in = new StringBuilder();
        in.append(line1);
        in.append("\r");
        in.append(line2);

        template.requestBody("mina2:tcp://127.0.0.1:" + MINA2_PORT + "?sync=true&codec=#hl7codec", in.toString());
        
        template.stop();
        camelContext.stop();
    }

    public static void main(String args[]) {
        HL7Client client = new HL7Client();
        try {
            client.testSendA19();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
