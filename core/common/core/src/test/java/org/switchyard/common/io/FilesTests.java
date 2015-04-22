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
