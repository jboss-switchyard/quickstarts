/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.switchyard.internal;

import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.internal.BaseContext;

/**
 *  Tests for context-related operations.
 */
public class BaseContextTest {
    
    private static final String PROP_NAME = "foo";
    private static final String PROP_VAL= "bar";
    private BaseContext _context;
    
    @Before
    public void setUp() throws Exception {
        _context = new BaseContext();
    }
    
    @Test
    public void testGetSet() throws Exception {
        _context.setProperty(PROP_NAME, PROP_VAL);
        Assert.assertEquals(PROP_VAL, _context.getProperty(PROP_NAME));
    }

    @Test
    public void testHasProperty() throws Exception {
        Assert.assertFalse(_context.hasProperty(PROP_NAME));
        _context.setProperty(PROP_NAME, PROP_VAL);
        Assert.assertTrue(_context.hasProperty(PROP_NAME));
    }

    @Test
    public void testRemove() throws Exception {
        _context.setProperty(PROP_NAME, PROP_VAL);
        Assert.assertTrue(_context.hasProperty(PROP_NAME));
        _context.removeProperty(PROP_NAME);
        Assert.assertFalse(_context.hasProperty(PROP_NAME));
    }
    
    @Test
    public void testRemoveNonexistent() throws Exception {
        // Removing a nonexistent property should not throw an exception
        final String propName = "blahFooYech";
        Assert.assertFalse(_context.hasProperty(propName));
        _context.removeProperty(propName);
    }
    
    @Test
    public void testNullContextValue() throws Exception {
        _context.setProperty(PROP_NAME, PROP_VAL);
        Assert.assertTrue(_context.hasProperty(PROP_NAME));
        // setting the value to null should remove the property from context
        _context.setProperty(PROP_NAME, null);
        Assert.assertFalse(_context.hasProperty(PROP_NAME));
    }

    @Test
    public void testGetProperties() throws Exception {
        _context.setProperty(PROP_NAME, PROP_VAL);
        Map<String, Object> props = _context.getProperties();
        Assert.assertEquals(PROP_VAL, props.get(PROP_NAME));
        
        // operations to the returned map should *not* be reflected in the context
        props.remove(PROP_NAME);
        Assert.assertFalse(props.containsKey(PROP_NAME));
        Assert.assertTrue(_context.hasProperty(PROP_NAME));
    }
    
}
