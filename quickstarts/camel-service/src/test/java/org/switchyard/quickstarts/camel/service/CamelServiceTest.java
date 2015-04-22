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
package org.switchyard.quickstarts.camel.service;

import org.junit.Test;
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
public class CamelServiceTest {

    private static final String TEST_MESSAGE = "\n"
        + "bob: Hello there!\n"
        + "sally: I like cheese\n"
        + "fred: Math makes me sleepy\n"
        + "bob: E pluribus unum\n"
        + "sally: And milk too\n"
        + "bob: Four score and seven years\n"
        + "sally: Actually, any kind of dairy is OK in my book\n";

    @ServiceOperation("JavaDSL.acceptMessage")
    private Invoker acceptMessage;

    @Test
    public void testCamelRoute() {
        acceptMessage.sendInOnly(TEST_MESSAGE);
    }
}
