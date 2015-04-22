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
package org.switchyard.component.camel.switchyard;

import org.apache.camel.CamelContext;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.switchyard.ServiceDomain;
import org.switchyard.common.camel.SwitchYardCamelContext;
import org.switchyard.common.camel.SwitchYardCamelContextImpl;
import org.switchyard.component.camel.common.CamelConstants;

/**
 * Base class for switchyard-camel integration.
 */
public abstract class SwitchYardComponentTestBase extends CamelTestSupport {

    protected ServiceDomain _serviceDomain;

    protected SwitchYardCamelContextImpl _camelContext;

    @Override
    protected CamelContext createCamelContext() throws Exception {
        _camelContext = new SwitchYardCamelContextImpl(false);
        _camelContext.setServiceDomain(_serviceDomain);
        return _camelContext;
    }

}
