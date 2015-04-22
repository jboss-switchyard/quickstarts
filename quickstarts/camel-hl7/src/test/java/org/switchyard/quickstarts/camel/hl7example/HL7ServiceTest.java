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

import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v24.datatype.ST;
import ca.uhn.hl7v2.model.v24.message.QRY_A19;
import ca.uhn.hl7v2.model.v24.segment.QRD;
import ca.uhn.hl7v2.parser.PipeParser;

import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.test.Invoker;
import org.switchyard.test.ServiceOperation;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;

@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(
    config = SwitchYardTestCaseConfig.SWITCHYARD_XML,
    mixins = CDIMixIn.class)
public class HL7ServiceTest {

    private static final String LINE_ONE = "MSH|^~\\&|MYSENDER|MYRECEIVER|MYAPPLICATION||200612211200||QRY^A19|1234|P|2.4";
    private static final String LINE_TWO = "QRD|200612211200|R|I|GetPatient|||1^RD|0101701234|DEM||";

    @ServiceOperation("HL7Route.process")
    private Invoker processMessage;

    @Test
    public void testCamelRoute() {
        StringBuilder in = new StringBuilder();
        in.append(LINE_ONE);
        in.append("\r");
        in.append(LINE_TWO);
        
        String result = processMessage.sendInOut(in).getContent(String.class);;
        PipeParser pipeParser = new PipeParser();
        
        try {
            //parse the message string into a Message object 
            Message message = pipeParser.parse(result);
            if (message instanceof QRY_A19) {
                QRD qrd = (QRD) message.get("QRD");
                ST st = qrd.getQueryID();
                Assert.assertTrue(st.getValue().equals("GetPatient"));
            } else {
                Assert.fail("Message not instance of QRY_A19");
            }
            
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
        
    }
}
