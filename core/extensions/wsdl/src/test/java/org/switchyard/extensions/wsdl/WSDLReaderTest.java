/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.switchyard.extensions.wsdl;

import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.metadata.ServiceOperation;

public class WSDLReaderTest {

    private static final String ORDER_WSDL = "wsdl/OrderService.wsdl";
    private WSDLReader reader;
    
    @Before
    public void setUp() {
        reader = new WSDLReader();
    }
    
    @Test
    public void testRelativePaths() {
        String base1 = "base.wsdl";
        String base2 = "wsdl/one/base.wsdl";
        String base3 = "wsdl/one/two/base.wsdl";
        String wsdl = "example.wsdl";
        
        Assert.assertEquals("example.wsdl", 
                reader.createRelativePath(base1, wsdl));
        Assert.assertEquals("wsdl/one/example.wsdl", 
                reader.createRelativePath(base2, wsdl));
        Assert.assertEquals("wsdl/one/two/example.wsdl", 
                reader.createRelativePath(base3, wsdl));
    }
    
    @Test
    public void importedWSDLs() throws Exception {
        Assert.assertNotNull(reader.readWSDL(ORDER_WSDL));
        Set<ServiceOperation> ops = reader.readWSDL(ORDER_WSDL, "OrderService");
        Assert.assertEquals(1, ops.size());
    }
}
