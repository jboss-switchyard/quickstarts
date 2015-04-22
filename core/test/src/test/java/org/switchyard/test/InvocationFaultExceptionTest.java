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

package org.switchyard.test;

import org.junit.Assert;
import org.junit.Test;
import org.switchyard.internal.DefaultMessage;

public class InvocationFaultExceptionTest {

    @Test
    public void testTypeMatchesException() {
        InvocationFaultException infEx = new InvocationFaultException(
                new DefaultMessage().setContent(new DummyException()));
        Assert.assertTrue(infEx.isType(DummyException.class));
    }

    @Test
    public void testTypeMatchesObject() {
        InvocationFaultException infEx = new InvocationFaultException(
                new DefaultMessage().setContent(new DummyObject()));
        Assert.assertTrue(infEx.isType(DummyObject.class));
    }
    
    @Test
    public void testGetCauseMatchesFault() {
        DummyException testEx = new DummyException();
        InvocationFaultException infEx = new InvocationFaultException(new DefaultMessage().setContent(testEx));
        Assert.assertEquals(testEx, infEx.getCause());
    }
}

class DummyException extends Exception {
    
}

class DummyObject {
    
}
