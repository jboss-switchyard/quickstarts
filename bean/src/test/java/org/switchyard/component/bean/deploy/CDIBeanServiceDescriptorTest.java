/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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
    public void test_IsInterface_Fail() {
        try {
            CDIBeanServiceDescriptor.getServiceInterface(YImpl.class);
        } catch (RuntimeException e) {
            Assert.assertEquals("Invalid @Service specification @Service(java.lang.String.class).  @Service interface Class must be a Java Interface.  Cannot be a concrete implementation.", e.getMessage());
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
}
