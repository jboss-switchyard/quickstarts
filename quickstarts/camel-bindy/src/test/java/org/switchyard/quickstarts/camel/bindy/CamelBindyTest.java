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
package org.switchyard.quickstarts.camel.bindy;

import org.junit.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.test.Invoker;
import org.switchyard.test.ServiceOperation;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.SwitchYardTestKit;
import org.switchyard.transform.config.model.TransformSwitchYardScanner;

@SwitchYardTestCaseConfig(
    config = SwitchYardTestCaseConfig.SWITCHYARD_XML,
    mixins = { CDIMixIn.class },
    scanners = TransformSwitchYardScanner.class)
@RunWith(SwitchYardRunner.class)
public class CamelBindyTest {
    @ServiceOperation("FileProcessor")
    private Invoker fileProcessor;
    
    private SwitchYardTestKit _testKit;
    
    @Test
    public void shouldRetrieveGreetings() throws Exception {
        String requestTxt = _testKit.readResourceString("request.txt");
        String replyMsg = fileProcessor.sendInOut(requestTxt).getContent(String.class);
        
        replyMsg = replyMsg.replaceAll("(\\r|\\n)", "");
        
        String responseTxt = _testKit.readResourceString("response.txt");
        responseTxt = responseTxt.replaceAll("(\\r|\\n)", "");

        Assert.assertEquals(replyMsg, responseTxt);
    }
}
