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

@SwitchYardTestCaseConfig(
        config = "/loan_approval/switchyard.xml",
        scanners = {TransformSwitchYardScanner.class },
        mixins = {CDIMixIn.class, HTTPMixIn.class })
public class BPELLoanApprovalTest {

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
        _testKit.getMixIn(HTTPMixIn.class).
                postResourceAndTestXML("http://localhost:18001/loanService",
                		"/loan_approval/soap-loanreq1.xml", "/loan_approval/soap-loanresp1.xml");
    }
    
    
    @Test
    public void sendLoanRequest2() throws Exception {
        _testKit.getMixIn(HTTPMixIn.class).
                postResourceAndTestXML("http://localhost:18001/loanService",
                		"/loan_approval/soap-loanreq2.xml", "/loan_approval/soap-loanresp2.xml");
    }
}
