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
