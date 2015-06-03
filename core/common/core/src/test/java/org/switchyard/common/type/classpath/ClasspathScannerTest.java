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

package org.switchyard.common.type.classpath;

import org.junit.Assert;
import org.junit.Test;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class ClasspathScannerTest {

    /**
     * See https://issues.jboss.org/browse/SWITCHYARD-425
     * @throws IOException
     */
    @Test
    public void test_folder_with_plus_chars_scan() throws IOException {
        String resourceName = "some_resource.txt";
        ResourceExistsFilter filter = new ResourceExistsFilter(resourceName);
        ClasspathScanner scanner = new ClasspathScanner(filter);

        // If running this test inside your IDE... you need to set the cwd to be the
        // root of the config module !!

        File scanDir = new File("./target/folder+with+plus");
        scanDir.delete();
        scanDir.mkdirs();

        // Create the resource in the scanDir...
        FileWriter writer = new FileWriter(new File(scanDir, resourceName));
        try {
            writer.write("something....");
            writer.flush();
        } finally {
            writer.close();
        }

        scanner.scan(scanDir.toURI().toURL());

        Assert.assertTrue(filter.resourceExists());
    }

    @Test
    public void test_archive_scan() throws IOException {
        InstanceOfFilter filter = new InstanceOfFilter(CommandMap.class);
        ClasspathScanner scanner = new ClasspathScanner(filter);

        // If running this test inside your IDE... you need to set the cwd to be the
        // root of the config module !!
        scanner.scan(new File("./src/test/resources/classpathscan-test.jar").toURI().toURL());
        List<Class<?>> classes = filter.getMatchedTypes();

        Assert.assertTrue(classes.contains(CommandMap.class));
        Assert.assertTrue(classes.contains(MailcapCommandMap.class));
    }
}
