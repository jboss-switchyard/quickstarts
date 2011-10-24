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
package org.switchyard.composer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.Context;
import org.switchyard.common.xml.XMLHelper;
import org.switchyard.composer.BaseContextMapper;
import org.switchyard.composer.ContextMapper;

/**
 * ContextMapperTests.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class ContextMapperTests {

    private ContextMapper<Object> _contextMapper;

    @Before
    public void before() throws Exception {
        _contextMapper = new BaseContextMapper<Object>() {
            public void mapFrom(Object source, Context context) throws Exception {}
            public void mapTo(Context context, Object target) throws Exception { }
        };
    }

    @After
    public void after() throws Exception {
        _contextMapper = null;
    }

    @Test
    public void testName() throws Exception {
        assertTrue(_contextMapper.matches("foo"));
    }

    @Test
    public void testNameIncludeEverything() throws Exception {
        _contextMapper.setIncludes(".*");
        assertTrue(_contextMapper.matches("foo"));
    }

    @Test
    public void testNameExcludeEverything() throws Exception {
        _contextMapper.setExcludes(".*");
        assertFalse(_contextMapper.matches("foo"));
    }

    @Test
    public void testNameIncludeEverythingExcludeEverything() throws Exception {
        _contextMapper.setIncludes(".*");
        _contextMapper.setExcludes(".*");
        assertFalse(_contextMapper.matches("foo"));
    }

    @Test
    public void testNameIncludeFooExcludeBar() throws Exception {
        _contextMapper.setIncludes(".*foo.*");
        _contextMapper.setExcludes(".*bar.*");
        assertTrue(_contextMapper.matches("abc_foo_def"));
        assertFalse(_contextMapper.matches("abc_bar_def"));
    }

    @Test
    public void testQName() throws Exception {
        _contextMapper.setIncludeNamespaces("urn:foo:1.0");
        assertTrue(_contextMapper.matches(XMLHelper.createQName("urn:foo:1.0", "banana")));
        assertFalse(_contextMapper.matches(XMLHelper.createQName("urn:foo:2.0", "banana")));
    }

    @Test
    public void testQNameIncludeEverything() throws Exception {
        _contextMapper.setIncludeNamespaces(".*");
        assertTrue(_contextMapper.matches(XMLHelper.createQName("urn:foo:1.0", "banana")));
        assertTrue(_contextMapper.matches(XMLHelper.createQName("urn:foo:2.0", "banana")));
    }

    @Test
    public void testQNameIncludeEverythingExcludeEverything() throws Exception {
        _contextMapper.setIncludeNamespaces(".*");
        _contextMapper.setExcludeNamespaces(".*");
        assertFalse(_contextMapper.matches(XMLHelper.createQName("urn:foo:1.0", "banana")));
        assertFalse(_contextMapper.matches(XMLHelper.createQName("urn:foo:2.0", "banana")));
    }

    @Test
    public void testQNameIncludeFooIncludeBar() throws Exception {
        _contextMapper.setIncludeNamespaces("urn:foo:1.0");
        _contextMapper.setIncludes("bar");
        assertTrue(_contextMapper.matches(XMLHelper.createQName("urn:foo:1.0", "bar")));
        assertFalse(_contextMapper.matches(XMLHelper.createQName("urn:foo:1.0", "banana")));
        assertFalse(_contextMapper.matches(XMLHelper.createQName("urn:foo:2.0", "bar")));
    }

    @Test
    public void testQNameFooExcludeTxt() throws Exception {
        _contextMapper.setIncludeNamespaces("urn:foo:.*");
        _contextMapper.setExcludes(".*.txt");
        assertTrue(_contextMapper.matches(XMLHelper.createQName("urn:foo:1.0", "bar.xml")));
        assertTrue(_contextMapper.matches(XMLHelper.createQName("urn:foo:2.0", "bar.pdf")));
        assertFalse(_contextMapper.matches(XMLHelper.createQName("urn:foo:1.0", "bar.txt")));
    }

    @Test
    public void testQNameInclude2Exclude1() throws Exception {
        _contextMapper.setIncludeNamespaces("urn:.*:2.0");
        _contextMapper.setExcludeNamespaces("urn:.*:1.0");
        assertTrue(_contextMapper.matches(XMLHelper.createQName("urn:foo:2.0", "foo")));
        assertFalse(_contextMapper.matches(XMLHelper.createQName("urn:foo:1.0", "foo")));
    }

    @Test
    public void testMixedIncludesExcludes() throws Exception {
        _contextMapper.setIncludes("david, tom.*");
        _contextMapper.setExcludes("keith");
        _contextMapper.setIncludeNamespaces("urn:.*:2.0,urn:.*:3.0");
        _contextMapper.setExcludeNamespaces("urn:.*:3.0");
        assertTrue(_contextMapper.matches(XMLHelper.createQName("urn:foo:2.0", "tomc")));
        assertTrue(_contextMapper.matches(XMLHelper.createQName("urn:foo:2.0", "tomf")));
        assertFalse(_contextMapper.matches(XMLHelper.createQName("urn:foo:1.0", "tomc")));
        assertFalse(_contextMapper.matches(XMLHelper.createQName("urn:foo:3.0", "david")));
        assertFalse(_contextMapper.matches(XMLHelper.createQName("urn:foo:2.0", "keith")));
    }

}
