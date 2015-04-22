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

package org.switchyard.internal;

import org.junit.Assert;
import org.junit.Test;
import org.switchyard.Scope;

public class ContextPropertyTest {
    
    @Test
    public void testNullName() {
        // null name should not be permitted
        try {
            new ContextProperty(null, Scope.MESSAGE, "bar");
            Assert.fail("Null property name should not be permitted!");
        } catch (IllegalArgumentException expected) {
            // All good
            return;
        }
    }
    
    @Test
    public void testNullScope() {
        // null scope should not be permitted
        try {
            new ContextProperty("foo", null, "bar");
            Assert.fail("Null property scope should not be permitted!");
        } catch (IllegalArgumentException expected) {
            // All good
            return;
        }
    }
    
    @Test
    public void testEquals() {
        ContextProperty prop1 = new ContextProperty("foo", Scope.MESSAGE, "bar");
        ContextProperty prop2 = new ContextProperty("foo", Scope.MESSAGE, "bar");
        Assert.assertTrue(prop1.equals(prop2));
    }

    @Test
    public void testNotEquals() {
        ContextProperty propRef = new ContextProperty("foo", Scope.MESSAGE, "bar");
        ContextProperty propDiffName = new ContextProperty("oops", Scope.MESSAGE, "bar");
        ContextProperty propDiffValue = new ContextProperty("foo", Scope.MESSAGE, "nope");
        Assert.assertFalse(propRef.equals(propDiffName));
        Assert.assertFalse(propRef.equals(propDiffValue));
    }
    
}
