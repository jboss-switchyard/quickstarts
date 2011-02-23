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

package org.switchyard.component.bean.config.model;

import org.junit.Assert;
import org.junit.Test;
import org.switchyard.component.bean.Service;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class BeanSwitchYardScannerTest {

    @Test
    public void test() throws IOException, ClassNotFoundException {
        BeanSwitchYardScanner scanner = new BeanSwitchYardScanner();
        List<URL> urls = new ArrayList<URL>();

        // If running this test inside your IDE... you need to set the cwd to be the
        // root of the bean module !!
        urls.add(new File("./target/test-classes").toURI().toURL());

        List<BeanComponentImplementationModel> models = scanner.scan(urls);

        for(BeanComponentImplementationModel model : models) {
            checkBeanModel(model);
        }
    }

    private void checkBeanModel(BeanComponentImplementationModel model) throws ClassNotFoundException {
        Class<?> serviceClass = Class.forName(model.getClazz());

        Assert.assertFalse(serviceClass.isInterface());
        Assert.assertFalse(Modifier.isAbstract(serviceClass.getModifiers()));
        Assert.assertTrue(serviceClass.isAnnotationPresent(Service.class));
    }
}
