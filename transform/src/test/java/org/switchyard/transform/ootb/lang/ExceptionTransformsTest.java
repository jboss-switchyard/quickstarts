/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *  *
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

package org.switchyard.transform.ootb.lang;

import org.junit.Assert;
import org.junit.Test;

/**
 * ExceptionTransforms test.
 */
public class ExceptionTransformsTest {
    
    private static final String NL = System.getProperty("line.separator");
    
    @Test
    public void testToString() throws Exception {
        Assert.assertEquals(
                "java.lang.Exception: level-1" + NL
                        + " --- Caused by java.lang.IllegalStateException: level-2" + NL
                        + " --- Caused by java.lang.NullPointerException: level-3",
                new ExceptionTransforms().toString(
                    new Exception("level-1", 
                        new IllegalStateException("level-2", 
                            new NullPointerException("level-3")))));
    }
}
