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

package org.switchyard.component.bean.deploy;

import org.junit.Assert;
import org.junit.Test;
import org.switchyard.component.bean.Service;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class CDIBeanServiceDescriptorTest {

    @Test
    public void test_IsInterface_OK() {
        CDIBeanServiceDescriptor.getServiceInterface(XImpl.class);
    }

    @Test
    public void test_OmmitInterface_OK() {
        CDIBeanServiceDescriptor.getServiceInterface(ZImpl.class);
    }
    
    @Test
    public void test_OmmitInterface_Fail_noInterface() {
        try {
            CDIBeanServiceDescriptor.getServiceInterface(TImpl.class);
        } catch (RuntimeException e) {
            String message = e.getMessage();
            Assert.assertTrue(message.contains("SWITCHYARD030420"));
        }
    }
    
    @Test
    public void test_OmmitInterface_Fail_multipleInterfaces() {
        try {
            CDIBeanServiceDescriptor.getServiceInterface(UImpl.class);
        } catch (RuntimeException e) {
            String message = e.getMessage();
            Assert.assertTrue(message.contains("SWITCHYARD030420"));
        }
    }
    
    @Test
    public void test_IsInterface_Fail() {
        try {
            CDIBeanServiceDescriptor.getServiceInterface(YImpl.class);
        } catch (RuntimeException e) {
            String message = e.getMessage();
            Assert.assertTrue(message.contains("SWITCHYARD030421"));
        }
    }

    @Service(X.class)
    private class XImpl {
    }
    private interface X {
    }
    @Service(String.class)
    private class YImpl {
    }
    @Service
    private class ZImpl implements Z {
    }
    private interface Z {
    }
    @Service
    private class TImpl {
    }
    @Service
    private class UImpl implements U, Z {
    }
    private interface U {
    }
}
