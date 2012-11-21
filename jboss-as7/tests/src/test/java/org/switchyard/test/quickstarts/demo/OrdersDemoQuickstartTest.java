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
package org.switchyard.test.quickstarts.demo;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.custommonkey.xmlunit.XMLAssert;
import org.switchyard.test.ArquillianUtil;
import org.switchyard.component.test.mixins.http.HTTPMixIn;
import org.xml.sax.SAXException;

import java.io.IOException;

import junit.framework.Assert;

/**
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
@RunWith(Arquillian.class)
public class OrdersDemoQuickstartTest {

    @Deployment(testable = false)
    public static WebArchive createDeployment() {
        return ArquillianUtil.createWarDemoDeployment("switchyard-quickstart-demo-orders");
    }

    @Test
    public void testOrders() throws Exception {
    	// Only testing that deployment is successful at this point.  The web service
    	// endpoint is tested as part of the bean-service quickstart
    	Assert.assertTrue(true);
    }
}
