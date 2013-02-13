/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.common.io;

import java.io.File;
import java.io.FileWriter;

import junit.framework.Assert;

import org.junit.Test;
import org.switchyard.common.io.pull.StringPuller;

/**
 * File utilities tests.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class FilesTests {

    @Test
    public void testCopy() throws Exception {
        final String expected = "test";
        File testFile1 = File.createTempFile("test-1", ".txt");
        FileWriter testFileWriter1 = new FileWriter(testFile1);
        testFileWriter1.write(expected);
        testFileWriter1.flush();
        testFileWriter1.close();
        File testFile2 = File.createTempFile("test-2", ".txt");
        Files.copy(testFile1, testFile2);
        final String actual = new StringPuller().pull(testFile2);
        testFile1.delete();
        testFile2.delete();
        Assert.assertEquals(expected, actual);
    }

}
