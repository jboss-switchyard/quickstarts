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

package org.switchyard;

import org.junit.Assert;
import org.junit.Test;

public class ScopeTest {

    @Test
    public void testActiveScopeIn() {
        MockExchange exchange = new MockExchange().setPhase(ExchangePhase.IN);
        Scope scope = Scope.activeScope(exchange);
        Assert.assertEquals(Scope.IN, scope);
    }
    
    @Test
    public void testActiveScopeOut() {
        MockExchange exchange = new MockExchange().setPhase(ExchangePhase.OUT);
        Scope scope = Scope.activeScope(exchange);
        Assert.assertEquals(Scope.OUT, scope);
    }
    
    @Test
    public void testActiveScopeNullPhaseNoException() {
        MockExchange exchange = new MockExchange();
        Scope scope = Scope.activeScope(exchange);
        // above should not throw an exception, returned scope should be null
        Assert.assertNull(scope);
    }
}
