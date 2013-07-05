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
