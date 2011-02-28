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

package org.switchyard.internal.transform;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.switchyard.transform.BaseTransformer;
import org.switchyard.transform.TransformerRegistry;

public class BaseTransformerRegistryTest {
    
    private TransformerRegistry _registry;
    
    @Before
    public void setUp() throws Exception {
        _registry = new BaseTransformerRegistry();
    }
    
    @Test
    public void testAddGetTransformer() {
        final QName fromName = new QName("a");
        final QName toName = new QName("b");
        
        BaseTransformer<String, Integer> t = 
            new BaseTransformer<String, Integer>(fromName, toName) {
                public Integer transform(String from) {
                    return null;
                }
        };
        
        _registry.addTransformer(t);
        Assert.assertEquals(t, _registry.getTransformer(fromName, toName));      
    }   
}
