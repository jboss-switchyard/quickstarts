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

package org.switchyard.component.bean.tests;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.component.bean.config.model.BeanSwitchYardScanner;
import org.switchyard.config.model.ScannerInput;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;

/**
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public class ServiceAnnotationTest {
    
    private SwitchYardModel _scannedModel;
    
    @Before
    public void setUp() throws Exception {
        BeanSwitchYardScanner scanner = new BeanSwitchYardScanner();
        List<URL> urls = new ArrayList<URL>();

        // If running this test inside your IDE... you need to set the cwd to be the
        // root of the bean module !!
        urls.add(new File("./target/test-classes").toURI().toURL());

        ScannerInput<SwitchYardModel> input = new ScannerInput<SwitchYardModel>().setURLs(urls);
        _scannedModel = scanner.scan(input).getModel();
    }

    @Test
    public void test() throws IOException, ClassNotFoundException {
        List<ComponentModel> components = _scannedModel.getComposite().getComponents();
        boolean customServiceNameFound = false;
        boolean emptyServiceNameFound = false;
        boolean customComponentNameFound = false;
        for(ComponentModel component : components) {
            if(component.getName().equals("CustomServiceAnnotationServiceName")){
                customServiceNameFound = true;
            }
            if(component.getName().equals("")){
                emptyServiceNameFound = true;
            }
            if(component.getName().equals("CustomServiceAnnotationComponentName")){
                customComponentNameFound = true;
            }
        }
        Assert.assertTrue(customServiceNameFound);
        Assert.assertFalse(emptyServiceNameFound);
        Assert.assertTrue(customComponentNameFound);
    }

}
