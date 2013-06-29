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
package org.switchyard.test.quickstarts.demo;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.custommonkey.xmlunit.XMLAssert;
import org.switchyard.test.ArquillianUtil;
import org.switchyard.component.test.mixins.http.HTTPMixIn;
import org.xml.sax.SAXException;

import java.io.IOException;

import junit.framework.Assert;

/**
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
@RunWith(Arquillian.class)
public class OrdersDemoQuickstartTest {

    @Deployment(testable = false)
    public static WebArchive createDeployment() {
        return ArquillianUtil.createWarDemoDeployment("switchyard-quickstart-demo-orders");
    }

    @Test
    public void testOrders() throws Exception {
    	// Only testing that deployment is successful at this point.  The web service
    	// endpoint is tested as part of the bean-service quickstart
    	Assert.assertTrue(true);
    }
}
