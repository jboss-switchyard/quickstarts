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
package org.switchyard.component.bpel.riftsaw;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.SwitchYardTestKit;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.component.test.mixins.http.HTTPMixIn;
import org.switchyard.transform.config.model.TransformSwitchYardScanner;

/*
 * This test is in response to SWITCHYARD-2311.   It uses a modified version
 * of the loan approval / risk assessment processes which do not return a response,
 * and triggers the creation of an IN_ONLY exchange in RiftsawServiceLocator. 
 */
@SwitchYardTestCaseConfig(
        config = "/loan2_approval/switchyard.xml",
        scanners = {TransformSwitchYardScanner.class },
        mixins = {CDIMixIn.class, HTTPMixIn.class })
public class BPELInOnlyLocatorTest {

    private SwitchYardTestKit _testKit;

    @org.junit.Before
    public void init() {
    	try {
    		_testKit = new SwitchYardTestKit(this);
    		_testKit.start();
    	} catch(Exception e) {
    		e.printStackTrace();
    		fail("Unable to initialize testkit: "+ e);
    	}
    }
    
    @org.junit.After
    public void close() {
    	if (_testKit != null) {
	    	try {
	    		_testKit.cleanup();
	    	} catch(Exception e) {
	    		e.printStackTrace();
	    		fail("Unable to cleanup testkit: "+ e);
	    	}
    	}
    }
    
    @Test
    public void sendLoanRequest1() throws Exception {
        String msg = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:exam=\"http://www.jboss.org/bpel/examples/wsdl\">"
                + "<soapenv:Header/><soapenv:Body>"
                + "<ns1:request xmlns:ns1=\"http://example.com/loan2-approval/loan2Service/\">"
                + "<firstName>Fred</firstName>"
                + "<name>Bloggs</name>"
                + "<amount>100</amount>"
                + "</ns1:request>"            
                + "</soapenv:Body></soapenv:Envelope>";
       
        String result = _testKit.getMixIn(HTTPMixIn.class).
                postResourceAndTestXML("http://localhost:18001/loan2Service",
                        "/loan2_approval/soap-loanreq1.xml", "/loan2_approval/soap-loanresp1.xml");
    }
        
}
