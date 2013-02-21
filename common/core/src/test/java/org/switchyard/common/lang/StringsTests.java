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
import org.switchyard.common.property.CompoundPropertyResolver;
import org.switchyard.common.property.PropertiesPropertyResolver;
import org.switchyard.common.property.PropertyResolver;
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
        PropertyResolver resolver = new CompoundPropertyResolver(SystemPropertyResolver.instance(), new PropertiesPropertyResolver(custom));
        final String actual = Strings.replaceProperties(original, resolver);
        Assert.assertEquals(expected, actual);
    }

    @Before
    public void beforeReplaceTestProperties() {
        TestPropertyResolver.instance().getMap().put("test.key", "testValue");
    }

    @After
    public void afterReplaceTestProperties() {
        TestPropertyResolver.instance().getMap().clear();
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
        PropertyResolver resolver = new CompoundPropertyResolver(SystemAndTestPropertyResolver.instance(), new PropertiesPropertyResolver(custom));
        final String actual = Strings.replaceProperties(original, resolver);
        Assert.assertEquals(expected, actual);
    }

}
