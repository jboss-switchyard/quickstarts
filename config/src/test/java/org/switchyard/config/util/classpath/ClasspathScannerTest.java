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

package org.switchyard.config.util.classpath;

import org.junit.Assert;
import org.junit.Test;
import org.switchyard.common.type.classpath.ClasspathScanner;
import org.switchyard.common.type.classpath.InstanceOfFilter;
import org.switchyard.config.BaseConfiguration;
import org.switchyard.config.Configuration;
import org.switchyard.config.DOMConfiguration;

import javax.activation.CommandMap;
import javax.activation.MailcapCommandMap;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class ClasspathScannerTest {

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
