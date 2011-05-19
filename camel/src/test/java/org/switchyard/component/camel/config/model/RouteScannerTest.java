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

package org.switchyard.component.camel.config.model;

import java.io.File;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.common.type.Classes;
import org.switchyard.config.model.ScannerInput;
import org.switchyard.config.model.composite.ComponentImplementationModel;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;

/**
 * Test scanner looking for @Route methods and generating appropriate config. 
 * 
 * NOTE: I lifted a fair bit of this code from the Bean scanner test - it would 
 * probably be a good idea to create a base Scanner test class so that additional 
 * scanner implementations can reuse the same code.
 */
public class RouteScannerTest {
    
    private SwitchYardModel _scannedModel;
    private RouteScanner _scanner;
    private List<URL> _scannedURLs;
    
    @Before
    public void setUp() throws Exception {
        _scanner = new RouteScanner();
        _scannedURLs = new ArrayList<URL>();
        _scannedURLs.add(new File("./target/test-classes").toURI().toURL());

    }

    @Test
    public void componentImplementationCreated() throws Exception {
        scan(new File("./target/test-classes/org/switchyard/component/camel/config/model").toURI().toURL());
        List<ComponentModel> components = _scannedModel.getComposite().getComponents();
        for(ComponentModel component : components) {
            System.out.println("RouteScanner found component: " + component.getName());
            // Verify component details
            Assert.assertEquals(SingleRouteService.class.getSimpleName(), component.getName());
            Assert.assertTrue(component.getServices().size() == 1);
            ComponentImplementationModel implementation = component.getImplementation();
            Assert.assertTrue(implementation instanceof CamelComponentImplementationModel);
            checkCamelImplementation((CamelComponentImplementationModel)implementation);
        }
    }
    
    private void checkCamelImplementation(CamelComponentImplementationModel model) throws Exception {
        // Load the class
        Class<?> routeClass = Classes.forName(model.getJavaClass(), getClass());
        // make sure the class itself is valid
        Assert.assertFalse(routeClass.isInterface());
        Assert.assertFalse(Modifier.isAbstract(routeClass.getModifiers()));
        
    }
    
    // Takes a list of URLs to scan *instead* of what's defined in @Before.
    private void scan(URL ... urls) throws Exception {
        if (urls != null && urls.length > 0) {
            _scannedURLs.clear();
            _scannedURLs.addAll(Arrays.asList(urls));
        }
        ScannerInput<SwitchYardModel> input = new ScannerInput<SwitchYardModel>().setURLs(_scannedURLs);
        _scannedModel = _scanner.scan(input).getModel();
    }
}
