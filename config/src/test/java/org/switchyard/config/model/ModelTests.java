/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.config.model;

import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;

/**
 * ModelTests.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class ModelTests {

    private static final String[] NS = new String[] {
        "http://www.w3.org/2000/09/xmldsig#",
        "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd",
        "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd",
        "http://docs.oasis-open.org/ns/opencsa/sca/200912",
        "urn:switchyard-config:switchyard:1.0",
        "urn:switchyard-config:transform:1.0",
        "urn:switchyard-component-soap:config:2.0",
        "urn:switchyard-component-soap:config:1.0",
        "urn:switchyard-component-bean:config:2.0",
        "urn:switchyard-component-bean:config:1.0",
        "http://my.custom.namespace:2.0",
        "http://my.custom.namespace:1.0"
    };

    @Test
    public void testNamespaceComparator() throws Exception {
        Set<String> ns = new TreeSet<String>(new NamespaceComparator());
        ns.add(NS[6]);
        ns.add(NS[7]);
        ns.add(NS[10]);
        ns.add(NS[2]);
        ns.add(NS[1]);
        ns.add(NS[9]);
        ns.add(NS[8]);
        ns.add(NS[3]);
        ns.add(NS[0]);
        ns.add(NS[5]);
        ns.add(NS[4]);
        ns.add(NS[11]);
        Assert.assertArrayEquals(NS, ns.toArray(new String[ns.size()]));
    }

}
