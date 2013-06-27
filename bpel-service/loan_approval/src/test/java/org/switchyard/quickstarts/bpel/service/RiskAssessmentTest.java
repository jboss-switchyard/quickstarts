/*
 * 2012 Red Hat Inc. and/or its affiliates and other contributors.
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

package org.switchyard.quickstarts.bpel.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.test.Invoker;
import org.switchyard.test.ServiceOperation;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.SwitchYardTestKit;

@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(
        config = SwitchYardTestCaseConfig.SWITCHYARD_XML,
        mixins = CDIMixIn.class)
public class RiskAssessmentTest {
    
    @ServiceOperation("riskAssessor")
    private Invoker riskAssessor;
    
    private SwitchYardTestKit testKit;
    
    @Test
    public void testRiskAssessment1() throws Exception {
        String requestTxt = testKit.readResourceString("xml/xml-riskreq1.xml");
        String replyMsg = riskAssessor.sendInOut(requestTxt).getContent(String.class);
        testKit.compareXMLToResource(replyMsg, "xml/xml-riskresp1.xml");
    }
    
    @Test(expected=org.switchyard.test.InvocationFaultException.class)
    public void testRiskAssessment2() throws Exception {
        String requestTxt = testKit.readResourceString("xml/xml-riskreq2.xml");
        riskAssessor.sendInOut(requestTxt).getContent(String.class);
    }
}
