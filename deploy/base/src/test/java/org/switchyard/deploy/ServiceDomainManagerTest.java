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

package org.switchyard.deploy;

import javax.xml.namespace.QName;

import org.junit.Assert;
import org.junit.Test;
import org.switchyard.ServiceDomain;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.switchyard.SwitchYardModel;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class ServiceDomainManagerTest {

    @Test
    public void testHandlerRegistration() throws Exception {
        SwitchYardModel switchyard = new ModelPuller<SwitchYardModel>().pull(
                "/switchyard-config-properties-01.xml", getClass());
        
        ServiceDomain domain = new ServiceDomainManager().createDomain(
                new QName("test"), switchyard);
        
        Assert.assertEquals("abc-value", domain.getProperty("abc"));
        Assert.assertEquals("xyz-value", domain.getProperty("xyz"));
        Assert.assertNull(domain.getProperty("nothing"));
    }
}
