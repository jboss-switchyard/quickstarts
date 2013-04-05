/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
 * See the copyright.txt in the distribution for a 
 * full listing of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use, 
 * modify, copy, or redistribute it subject to the terms and conditions 
 * of the GNU Lesser General Public License, v. 2.1. 
 * This program is distributed in the hope that it will be useful, but WITHOUT A 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more details. 
 * You should have received a copy of the GNU Lesser General Public License, 
 * v.2.1 along with this distribution; if not, write to the Free Software 
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, 
 * MA  02110-1301, USA.
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
