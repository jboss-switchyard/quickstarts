package org.switchyard.quickstarts.camel.service;
import org.junit.Test;
import org.switchyard.component.camel.config.model.RouteScanner;
import org.switchyard.test.SwitchYardTestCase;
import org.switchyard.test.SwitchYardTestCaseConfig;
import org.switchyard.test.mixins.CDIMixIn;

/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
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

@SwitchYardTestCaseConfig(
        config = SwitchYardTestCaseConfig.SWITCHYARD_XML,
        mixins = CDIMixIn.class,
        scanners = RouteScanner.class
)
public class CamelServiceTest extends SwitchYardTestCase  {
    
    private static final String TEST_MESSAGE = "\n"
      + "bob: Hello there!\n"
      + "sally: I like cheese\n"
      + "fred: Math makes me sleepy\n"
      + "bob: E pluribus unum\n"
      + "sally: And milk too\n"
      + "bob: Four score and seven years\n"
      + "sally: Actually, any kind of dairy is OK in my book\n";

    @Test
    public void testCamelRoute() {
        newInvoker("JavaDSL")
        .operation("acceptMessage")
        .sendInOnly(TEST_MESSAGE);
    }
}
