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
package org.switchyard.component.camel.deploy;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.component.test.mixins.cdi.CDIMixIn;
import org.switchyard.test.Invoker;
import org.switchyard.test.ServiceOperation;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.SwitchYardTestCaseConfig;

/**
 * Test for {@link CamelActivator} using a Camel reference binding.
 * 
 * @author Daniel Bevenius
 * 
 */
@RunWith(SwitchYardRunner.class)
@SwitchYardTestCaseConfig(config = "switchyard-activator-ref.xml", mixins = CDIMixIn.class)
public class CamelReferenceTest {

    @ServiceOperation("OrderService.getTitleForItem")
    private Invoker _getTitleForItem;

    private CamelContext _camelContext;

    @Test
    public void invokeCamelEndpointViaInjection() throws Exception {
        _camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("vm://warehouseStatusService").inOut().setBody(constant("Fletch"));
            }
        });
        final String itemId = "1";
        final String title = (String) _getTitleForItem.sendInOut(itemId).getContent();

        assertThat(title, is(equalTo("Fletch")));
    }

}
