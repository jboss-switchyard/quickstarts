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


package org.switchyard.deploy;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.junit.Test;
import org.switchyard.ExchangeHandler;
import org.switchyard.ServiceReference;
import org.switchyard.config.model.Model;

public class BaseActivatorTest {

    @Test
    public void testNullCtor() {
        SimpleActivator activator = new SimpleActivator((String)null);
        Assert.assertNotNull(activator.getActivationTypes());
        Assert.assertFalse(activator.canActivate("foo"));
    }
    
    @Test
    public void testSingleActivationType() {
        SimpleActivator activator = new SimpleActivator("bar");
        Assert.assertEquals(1, activator.getActivationTypes().size());
        Assert.assertTrue(activator.canActivate("bar"));
    }
    

    @Test
    public void testMultipleActivationTypes() {
        SimpleActivator activator = new SimpleActivator(
                new String[] {"abc", "xyz"});
        Assert.assertEquals(2, activator.getActivationTypes().size());
        Assert.assertTrue(activator.canActivate("abc"));
        Assert.assertTrue(activator.canActivate("xyz"));
    }
}

class SimpleActivator extends BaseActivator {
    
    SimpleActivator(String ... types) {
        super(types);
    }

    @Override
    public void destroy(ServiceReference service) {
    }

    @Override
    public ExchangeHandler init(QName name, Model config) {
        return null;
    }

    @Override
    public void start(ServiceReference service) {        
    }

    @Override
    public void stop(ServiceReference service) {        
    }
    
}
