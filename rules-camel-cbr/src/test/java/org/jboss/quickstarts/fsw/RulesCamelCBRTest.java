/** 
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
package org.jboss.quickstarts.fsw;

import org.jboss.quickstarts.fsw.Box;
import org.jboss.quickstarts.fsw.Widget;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.test.Invoker;
import org.switchyard.test.ServiceOperation;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;

/**
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(config = SwitchYardTestCaseConfig.SWITCHYARD_XML, mixins = CDIMixIn.class)
public class RulesCamelCBRTest {

    private static final String[][] TESTS = new String[][] {
        new String[] { "FF0000-ABC-123", "Red" },
        new String[] { "00FF00-DEF-456", "Green" },
        new String[] { "0000FF-GHI-789", "Blue" } };

    @ServiceOperation("RoutingService.processRoute")
    private Invoker router;

    @Test
    public void testRGBWidgets() throws Exception {
        for (String[] test : TESTS) {
            Box box = new Box(new Widget(test[0]));
            router.sendInOnly(box);
            Assert.assertEquals(test[1], box.getDestination());
        }
    }

}
