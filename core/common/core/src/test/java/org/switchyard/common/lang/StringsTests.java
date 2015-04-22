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
package org.switchyard.common.lang;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.switchyard.common.lang.Strings.repeat;
import static org.switchyard.common.lang.Strings.splitTrimToNull;
import static org.switchyard.common.lang.Strings.splitTrimToNullArray;
import static org.switchyard.common.lang.Strings.trimToNull;
import static org.switchyard.common.lang.Strings.uniqueSplitTrimToNull;
import static org.switchyard.common.lang.Strings.uniqueSplitTrimToNullArray;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.common.property.PropertiesPropertyResolver;
import org.switchyard.common.property.SystemAndTestPropertyResolver;
import org.switchyard.common.property.SystemPropertyResolver;
import org.switchyard.common.property.TestPropertyResolver;

/**
 * StringsTests.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class StringsTests {

    @Test
    public void testTrimToNull() throws Exception {
        assertNull(trimToNull(" "));
        assertNotNull(trimToNull("x "));
        assertEquals("x y", trimToNull(" x y "));
    }

    @Test
    public void testCleanseTrimToNull() throws Exception {
        String[][] tests = new String[][]{
            new String[]{"My Cool \n Project", "My-Cool-Project"},
            new String[]{" the One     and  ___ONLY__*  ", "the-One-and-ONLY"},
            new String[]{" --- _", null}
        };
        for (String[] test : tests) {
            String original = test[0];
            String expected = test[1];
            String actual = Strings.cleanseTrimToNull(original);
            //System.out.println("original=[" + original + "], expected=[" + expected + "], actual=[" + actual + "]");
            assertEquals(expected, actual);
        }
    }

    @Test
    public void testRepeatEmptyString() {
        assertEquals("", repeat("", 10));
    }

    @Test
    public void testRepeatZeroTimes() {
        assertEquals("", repeat("a", 0));
        assertEquals("", repeat("a", -1));
    }

    @Test
    public void testRepeating() {
        assertEquals("a", repeat("a", 1));
        assertEquals("aa", repeat("a", 2));
    }

    @Test
    public void testSplitTrimToNull() throws Exception {
        List<String> l = splitTrimToNull("foo// bar/ ", "/");
        assertEquals(2, l.size());
        Iterator<String> i = l.iterator();
        assertEquals("foo", i.next());
        assertEquals("bar", i.next());
    }

    @Test
    public void testSplitTrimToNullArray() throws Exception {
        String[] s = splitTrimToNullArray("foo// bar/ ", "/");
        assertEquals(2, s.length);
        assertEquals("foo", s[0]);
        assertEquals("bar", s[1]);
    }

    @Test
    public void testUniqueSplitTrimToNull() throws Exception {
        Set<String> s = uniqueSplitTrimToNull("foo// bar//foo/bar/ ", "/");
        assertEquals(2, s.size());
        Iterator<String> i = s.iterator();
        assertEquals("foo", i.next());
        assertEquals("bar", i.next());
    }

    @Test
    public void testUniqueSplitTrimToNullArray() throws Exception {
        String[] s = uniqueSplitTrimToNullArray("foo// bar//foo/bar/ ", "/");
        assertEquals(2, s.length);
        assertEquals("foo", s[0]);
        assertEquals("bar", s[1]);
    }

    @Test
    public void testReplaceSystemProperties() {
        final String original = "Hello ${user.name} using ${os.name} skipping ${unknown.property}!";
        final String expected = "Hello " + System.getProperty("user.name") + " using " + System.getProperty("os.name") + " skipping ${unknown.property}!";
        final String actual = Strings.replaceSystemProperties(original);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testReplaceCustomProperties() {
        Properties custom = new Properties();
        custom.setProperty("foo", "bar");
        final String original = "I have a ${foo} but not a ${baz:wiz}.";
        final String expected = "I have a bar but not a wiz.";
        final String actual = Strings.replaceProperties(original, new PropertiesPropertyResolver(custom));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testReplaceCompoundProperties() {
        Properties custom = new Properties();
        custom.setProperty("foo", "bar");
        custom.setProperty("emotion", "loves");
        final String original = "${user.name} has a ${foo}, and he ${emotion:hates} it, unlike his ${sibling:sister}.";
        final String expected = System.getProperty("user.name") + " has a bar, and he loves it, unlike his sister.";
        final String actual = Strings.replaceProperties(original, SystemPropertyResolver.INSTANCE, new PropertiesPropertyResolver(custom));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testReplaceEmbeddedProperties() {
        Properties custom = new Properties();
        custom.setProperty("goto", "bed");
        custom.setProperty("foo", "${em${goto}ded}");
        custom.setProperty("embedded", "bar");
        custom.setProperty("foobar", "tub");
        final String original = "I have a ${foo} and a ${foo${foo}} but not a ${baz:wiz}.";
        final String expected = "I have a bar and a tub but not a wiz.";
        final String actual = Strings.replaceProperties(original, new PropertiesPropertyResolver(custom));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testReplaceButEscapeDoubleDollarProperties() {
        Properties custom = new Properties();
        custom.setProperty("foo", "bar");
        custom.setProperty("boo", "foo");
        final String original = "I have a ${foo} and a ${baz:wiz}, but not a $${foo}, a $${ba${boo}n} but not a $${ba$${boo}n}, a $${baz:wiz}, or a $${baz:$${oo}${ka}}.";
        final String expected = "I have a bar and a wiz, but not a ${foo}, a ${bafoon} but not a ${ba${boo}n}, a ${baz:wiz}, or a ${baz:${oo}${ka}}.";
        final String actual = Strings.replaceProperties(original, new PropertiesPropertyResolver(custom));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testReplaceButHandleDoubleColonVaultProperty() {
        final String key = "VAULT::ds_ExampleDS::password::N2NhZDYzOTMtNWE0OS00ZGQ0LWE4MmEtMWNlMDMyNDdmNmI2TElORV9CUkVBS3ZhdWx0";
        final String expected = "expected";
        Properties properties = new Properties();
        properties.setProperty(key, expected);
        final String original = "${" + key + "}";
        final String actual = Strings.replaceProperties(original, new PropertiesPropertyResolver(properties));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testReplaceButHandleDoubleColonVaultAndCustomProperties() {
        final String nested = "VAULT::${vaultBlock:ds_ExampleDS}::${attributeName:changeit}::N2NhZDYzOTMtNWE0OS00ZGQ0LWE4MmEtMWNlMDMyNDdmNmI2TElORV9CUkVBS3ZhdWx0";
        final String key = "VAULT::ds_ExampleDS::password::N2NhZDYzOTMtNWE0OS00ZGQ0LWE4MmEtMWNlMDMyNDdmNmI2TElORV9CUkVBS3ZhdWx0";
        final String expected = "expected";
        Properties properties = new Properties();
        properties.setProperty("attributeName", "password");
        properties.setProperty(key, expected);
        final String original = "${" + nested + "}";
        final String actual = Strings.replaceProperties(original, new PropertiesPropertyResolver(properties));
        Assert.assertEquals(expected, actual);
    }

    @Before
    public void beforeReplaceTestProperties() {
        TestPropertyResolver.INSTANCE.getMap().put("test.key", "testValue");
    }

    @After
    public void afterReplaceTestProperties() {
        TestPropertyResolver.INSTANCE.getMap().clear();
    }

    @Test
    public void testReplaceTestProperties() {
        final String original = "The test value is ${test.key}.";
        final String expected = "The test value is testValue.";
        final String actual = Strings.replaceTestProperties(original);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testReplaceSystemAndTestProperties() {
        final String original = "${user.name}'s test value is ${test.key}.";
        final String expected = System.getProperty("user.name") + "'s test value is testValue.";
        final String actual = Strings.replaceSystemAndTestProperties(original);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testReplaceSystemAndTestAndCustomProperties() {
        Properties custom = new Properties();
        custom.setProperty("foo", "bar");
        custom.setProperty("emotion", "loves");
        final String original = "${user.name} has a ${foo}, and he ${emotion:hates} it, unlike his ${sibling:sister}, who has a ${test.key}.";
        final String expected = System.getProperty("user.name") + " has a bar, and he loves it, unlike his sister, who has a testValue.";
        final String actual = Strings.replaceProperties(original, SystemAndTestPropertyResolver.INSTANCE, new PropertiesPropertyResolver(custom));
        Assert.assertEquals(expected, actual);
    }

}
