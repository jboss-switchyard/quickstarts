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
package org.switchyard.common.camel;

import static org.junit.Assert.assertTrue;

import javax.xml.namespace.QName;

import org.junit.Before;
import org.junit.Test;
import org.switchyard.internal.DomainImpl;

/**
 * Test start/stop behavior in camel context extension.
 */
public class SwitchYardCamelContextLifecycleTest {

    private SwitchYardCamelContext context;

    @Before
    public void setUp() {
        context = new SwitchYardCamelContext();
        context.setServiceDomain(new DomainImpl(new QName("urn:test-domain:camel")));
    }

	@Test
    public void startOnce() throws Exception {
        context.start();
        assertTrue(context.isStarted());
        context.start();
        context.stop();
        assertTrue(context.isStarted());
        context.stop();
        assertTrue(context.isStopped());
    }
}
