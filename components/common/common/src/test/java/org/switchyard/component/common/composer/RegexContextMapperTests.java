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
package org.switchyard.component.common.composer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.common.xml.XMLHelper;

/**
 * RegexContextMapperTests.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class RegexContextMapperTests {

    private static final class TestData implements BindingData {}

    private RegexContextMapper<TestData> _regexContextMapper;

    @Before
    public void before() throws Exception {
        _regexContextMapper = new BaseRegexContextMapper<TestData>();
    }

    @After
    public void after() throws Exception {
        _regexContextMapper = null;
    }

    @Test
    public void testName() throws Exception {
        assertTrue(_regexContextMapper.matches("foo"));
    }

    @Test
    public void testNameIncludeEverything() throws Exception {
        _regexContextMapper.setIncludes(".*");
        assertTrue(_regexContextMapper.matches("foo"));
    }

    @Test
    public void testNameExcludeEverything() throws Exception {
        _regexContextMapper.setExcludes(".*");
        assertFalse(_regexContextMapper.matches("foo"));
    }

    @Test
    public void testNameIncludeEverythingExcludeEverything() throws Exception {
        _regexContextMapper.setIncludes(".*");
        _regexContextMapper.setExcludes(".*");
        assertFalse(_regexContextMapper.matches("foo"));
    }

    @Test
    public void testNameIncludeFooExcludeBar() throws Exception {
        _regexContextMapper.setIncludes(".*foo.*");
        _regexContextMapper.setExcludes(".*bar.*");
        assertTrue(_regexContextMapper.matches("abc_foo_def"));
        assertFalse(_regexContextMapper.matches("abc_bar_def"));
    }

    @Test
    public void testQName() throws Exception {
        _regexContextMapper.setIncludeNamespaces("urn:foo:1.0");
        assertTrue(_regexContextMapper.matches(XMLHelper.createQName("urn:foo:1.0", "banana")));
        assertFalse(_regexContextMapper.matches(XMLHelper.createQName("urn:foo:2.0", "banana")));
    }

    @Test
    public void testQNameIncludeEverything() throws Exception {
        _regexContextMapper.setIncludeNamespaces(".*");
        assertTrue(_regexContextMapper.matches(XMLHelper.createQName("urn:foo:1.0", "banana")));
        assertTrue(_regexContextMapper.matches(XMLHelper.createQName("urn:foo:2.0", "banana")));
    }

    @Test
    public void testQNameIncludeEverythingExcludeEverything() throws Exception {
        _regexContextMapper.setIncludeNamespaces(".*");
        _regexContextMapper.setExcludeNamespaces(".*");
        assertFalse(_regexContextMapper.matches(XMLHelper.createQName("urn:foo:1.0", "banana")));
        assertFalse(_regexContextMapper.matches(XMLHelper.createQName("urn:foo:2.0", "banana")));
    }

    @Test
    public void testQNameIncludeFooIncludeBar() throws Exception {
        _regexContextMapper.setIncludeNamespaces("urn:foo:1.0");
        _regexContextMapper.setIncludes("bar");
        assertTrue(_regexContextMapper.matches(XMLHelper.createQName("urn:foo:1.0", "bar")));
        assertFalse(_regexContextMapper.matches(XMLHelper.createQName("urn:foo:1.0", "banana")));
        assertFalse(_regexContextMapper.matches(XMLHelper.createQName("urn:foo:2.0", "bar")));
    }

    @Test
    public void testQNameFooExcludeTxt() throws Exception {
        _regexContextMapper.setIncludeNamespaces("urn:foo:.*");
        _regexContextMapper.setExcludes(".*.txt");
        assertTrue(_regexContextMapper.matches(XMLHelper.createQName("urn:foo:1.0", "bar.xml")));
        assertTrue(_regexContextMapper.matches(XMLHelper.createQName("urn:foo:2.0", "bar.pdf")));
        assertFalse(_regexContextMapper.matches(XMLHelper.createQName("urn:foo:1.0", "bar.txt")));
    }

    @Test
    public void testQNameInclude2Exclude1() throws Exception {
        _regexContextMapper.setIncludeNamespaces("urn:.*:2.0");
        _regexContextMapper.setExcludeNamespaces("urn:.*:1.0");
        assertTrue(_regexContextMapper.matches(XMLHelper.createQName("urn:foo:2.0", "foo")));
        assertFalse(_regexContextMapper.matches(XMLHelper.createQName("urn:foo:1.0", "foo")));
    }

    @Test
    public void testMixedIncludesExcludes() throws Exception {
        _regexContextMapper.setIncludes("david, tom.*");
        _regexContextMapper.setExcludes("keith");
        _regexContextMapper.setIncludeNamespaces("urn:.*:2.0,urn:.*:3.0");
        _regexContextMapper.setExcludeNamespaces("urn:.*:3.0");
        assertTrue(_regexContextMapper.matches(XMLHelper.createQName("urn:foo:2.0", "tomc")));
        assertTrue(_regexContextMapper.matches(XMLHelper.createQName("urn:foo:2.0", "tomf")));
        assertFalse(_regexContextMapper.matches(XMLHelper.createQName("urn:foo:1.0", "tomc")));
        assertFalse(_regexContextMapper.matches(XMLHelper.createQName("urn:foo:3.0", "david")));
        assertFalse(_regexContextMapper.matches(XMLHelper.createQName("urn:foo:2.0", "keith")));
    }

}
