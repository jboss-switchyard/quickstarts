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

import java.util.LinkedList;

import javax.xml.namespace.QName;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.transform.BaseTransformer;
import org.switchyard.transform.Transformer;

public class BaseTransformResolverTest {
    
    private static final QName A = new QName("a");
    private static final QName B = new QName("b");
    private static final QName C = new QName("c");
    private static final QName D = new QName("d");
    private static final QName E = new QName("e");
    
    private Transformer<?,?> fromAtoB;
    private Transformer<?,?> fromBtoC;
    private Transformer<?,?> fromBtoE;
    private Transformer<?,?> fromCtoD;
    
    private BaseTransformResolver resolver;
    private BaseTransformerRegistry registry;
    
    public BaseTransformResolverTest() {
        fromAtoB = createTransformer(A, B);
        fromBtoC = createTransformer(B, C);
        fromCtoD = createTransformer(C, D);
        fromBtoE = createTransformer(B, E);
    }
    
    @Before
    public void setUp() throws Exception {
        registry = new BaseTransformerRegistry();
        resolver = new BaseTransformResolver(registry);
    }

    @Test
    public void AtoCwithTwoHops() {
        registry.addTransformer(fromAtoB);
        registry.addTransformer(fromBtoC);
        registry.addTransformer(fromBtoE);
        
        LinkedList<QName> list = new LinkedList<QName>();
        boolean resolved = resolver.resolvePath(list, A, C, 2);
        Assert.assertTrue(resolved);
        
        // Check the path
        Assert.assertEquals(B, list.pop());
        Assert.assertEquals(C, list.pop());
    }
    
    @Test
    public void AtoCwithOneHop() {
        registry.addTransformer(fromAtoB);
        registry.addTransformer(fromBtoC);
        LinkedList<QName> list = new LinkedList<QName>();

        // This should fail because it requires two hops
        boolean resolved = resolver.resolvePath(list, A, C, 1);
        Assert.assertFalse(resolved);
    }
    
    @Test
    public void AtoDwithThreeHop() {
        registry.addTransformer(fromAtoB);
        registry.addTransformer(fromBtoC);
        registry.addTransformer(fromCtoD);
        LinkedList<QName> list = new LinkedList<QName>();

        boolean resolved = resolver.resolvePath(list, A, D, 3);
        Assert.assertTrue(resolved);
        
        // Check the path
        Assert.assertEquals(B, list.pop());
        Assert.assertEquals(C, list.pop());
        Assert.assertEquals(D, list.pop());
    }
    
    @Test
    public void AtoBwithOneHop() {
        registry.addTransformer(fromAtoB);
        
        LinkedList<QName> list = new LinkedList<QName>();
        boolean resolved = resolver.resolvePath(list, A, B, 1);
        Assert.assertTrue(resolved);
        
        // Check the path
        Assert.assertEquals(B, list.pop());
    }
    
    @Test
    public void AtoEwithCycleAtoA() {
        registry.addTransformer(fromAtoB);
        registry.addTransformer(createTransformer(A, A));
        registry.addTransformer(createTransformer(B, A));
        
        LinkedList<QName> list = new LinkedList<QName>();
        boolean resolved = resolver.resolvePath(list, A, E, 2);
        
        // This won't resolve, but we're really just checking to make sure a cycle in 
        // the graph doesn't produce an infinite loop
        Assert.assertFalse(resolved);
    }
    
    @SuppressWarnings("rawtypes")
    Transformer createTransformer(QName from, QName to) {
        return new BaseTransformer(from, to) {
            public Object transform(Object from) {
                return null;
            }
        };
    }
}

