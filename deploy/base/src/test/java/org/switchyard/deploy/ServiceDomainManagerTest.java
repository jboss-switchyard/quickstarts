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

package org.switchyard.deploy;

import org.junit.Assert;
import org.junit.Test;
import org.switchyard.BaseHandler;
import org.switchyard.ServiceDomain;

import javax.xml.namespace.QName;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class ServiceDomainManagerTest {

    @Test
    public void test() {
        ServiceDomainManager domainManager = new ServiceDomainManager();

        ServiceDomain d1 = domainManager.addApplicationServiceDomain(new QName("D1"));
        ServiceDomain d2 = domainManager.addApplicationServiceDomain(new QName("D2"));

        QName s1 = new QName("S1");
        QName s2 = new QName("S2");
        QName s3 = new QName("S3");
        QName s4 = new QName("S4");
        QName unknown = new QName("unknown");

        d1.registerService(s1, new BaseHandler());
        d2.registerService(s2, new BaseHandler());
        d1.registerService(s3, new BaseHandler());
        d2.registerService(s4, new BaseHandler());

        // Test that any of the services can be found through either of the
        // ServiceDomain instances
        Assert.assertNotNull(d1.getService(s1));
        Assert.assertNotNull(d1.getService(s2));
        Assert.assertNotNull(d1.getService(s3));
        Assert.assertNotNull(d1.getService(s4));
        Assert.assertNotNull(d2.getService(s1));
        Assert.assertNotNull(d2.getService(s2));
        Assert.assertNotNull(d2.getService(s3));
        Assert.assertNotNull(d2.getService(s4));

        Assert.assertNull(d1.getService(unknown));
        Assert.assertNull(d2.getService(unknown));

        // Test that exclude works on findService...
        Assert.assertNotNull(domainManager.findService(s1, d2));
        Assert.assertNull(domainManager.findService(s1, d1));

        // Test that lookup fails after removing the domain ala undeploy...
        domainManager.removeApplicationServiceDomain(d1);
        Assert.assertNull(domainManager.findService(s1, d2));
    }
}
