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

package org.switchyard.config.util.classpath;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.switchyard.common.type.classpath.ClasspathScanner;
import org.switchyard.common.type.classpath.InstanceOfFilter;
import org.switchyard.config.BaseConfiguration;
import org.switchyard.config.Configuration;
import org.switchyard.config.DOMConfiguration;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class ConfigClasspathScanTest {

    @Test
    public void test_folder_scan() throws IOException {
        InstanceOfFilter filter = new InstanceOfFilter(Configuration.class);
        ClasspathScanner scanner = new ClasspathScanner(filter);

        // If running this test inside your IDE... you need to set the cwd to be the
        // root of the config module !!
        scanner.scan(new File("./target/classes").toURI().toURL());
        List<Class<?>> classes = filter.getMatchedTypes();

        Assert.assertTrue(classes.contains(Configuration.class));
        Assert.assertTrue(classes.contains(BaseConfiguration.class));
        Assert.assertTrue(classes.contains(DOMConfiguration.class));
    }
}
