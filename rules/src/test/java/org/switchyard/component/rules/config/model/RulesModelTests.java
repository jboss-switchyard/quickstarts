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
package org.switchyard.component.rules.config.model;

import java.io.File;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.common.io.pull.StringPuller;
import org.switchyard.common.io.resource.Resource;
import org.switchyard.common.io.resource.ResourceType;
import org.switchyard.common.type.Classes;
import org.switchyard.component.rules.common.RulesAuditType;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.Scanner;
import org.switchyard.config.model.ScannerInput;
import org.switchyard.config.model.ScannerOutput;
import org.switchyard.config.model.composite.ComponentImplementationModel;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.CompositeModel;
import org.switchyard.config.model.resource.ResourceModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;

/**
 * Tests Rules models.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class RulesModelTests {

    private static final String COMPLETE_XML = "/org/switchyard/component/rules/config/model/RulesModelTests-Complete.xml";

    private ModelPuller<SwitchYardModel> _puller;

    @Before
    public void before() throws Exception {
        _puller = new ModelPuller<SwitchYardModel>();
    }

    @Test
    public void testReadComplete() throws Exception {
        SwitchYardModel switchyard = _puller.pull(COMPLETE_XML, getClass());
        CompositeModel composite = switchyard.getComposite();
        ComponentModel component = composite.getComponents().get(0);
        ComponentImplementationModel implementation = component.getImplementation();
        Assert.assertTrue(implementation instanceof RulesComponentImplementationModel);
        RulesComponentImplementationModel rci = (RulesComponentImplementationModel)implementation;
        Assert.assertEquals("rules", rci.getType());
        Assert.assertEquals(true, rci.isStateful());
        Iterator<ResourceModel> resource_iter = rci.getResources().iterator();
        Resource dsl = resource_iter.next();
        Assert.assertEquals("foo.dsl", dsl.getLocation());
        Assert.assertEquals(ResourceType.DSL, dsl.getType());
        Resource dslr = resource_iter.next();
        Assert.assertEquals("bar.dslr", dslr.getLocation());
        Assert.assertEquals(ResourceType.DSLR, dslr.getType());
        RulesAuditModel ram = rci.getRulesAudit();
        Assert.assertNotNull(ram);
        Assert.assertEquals(Integer.valueOf(2000), ram.getInterval());
        Assert.assertEquals("foobar", ram.getLog());
        Assert.assertEquals(RulesAuditType.CONSOLE, ram.getType());
    }

    @Test
    public void testWriteComplete() throws Exception {
        String old_xml = new StringPuller().pull(COMPLETE_XML, getClass());
        SwitchYardModel switchyard = _puller.pull(new StringReader(old_xml));
        String new_xml = switchyard.toString();
        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = XMLUnit.compareXML(old_xml, new_xml);
        Assert.assertTrue(diff.toString(), diff.identical());
    }

    @Test
    public void testValidation() throws Exception {
        SwitchYardModel switchyard = _puller.pull(COMPLETE_XML, getClass());
        Assert.assertTrue(switchyard.isModelValid());
    }

    @Test
    public void testScanForRules() throws Exception {
        Scanner<SwitchYardModel> scanner = new RulesSwitchYardScanner();
        ScannerInput<SwitchYardModel> input = new ScannerInput<SwitchYardModel>().setName(getClass().getSimpleName());
        List<URL> urls = new ArrayList<URL>();
        String resPath = getClass().getName().replaceAll("\\.", "/") + ".class";
        String urlPath = Classes.getResource(resPath).getPath();
        File file = new File(urlPath.substring(0, urlPath.length() - resPath.length()));
        urls.add(file.toURI().toURL());
        input.setURLs(urls);
        ScannerOutput<SwitchYardModel> output = scanner.scan(input);
        CompositeModel composite = output.getModel().getComposite();
        Assert.assertEquals(getClass().getSimpleName(), composite.getName());
        List<ComponentModel> cm_list = composite.getComponents();
        Assert.assertEquals(1, cm_list.size());
        for (ComponentModel c : cm_list) {
            RulesComponentImplementationModel rci = (RulesComponentImplementationModel)c.getImplementation();
            Iterator<ResourceModel> rm_iter = rci.getResources().iterator();
            ResourceModel rm = rm_iter.next();
            Assert.assertEquals("path/to/my.drl", rm.getLocation());
            Assert.assertEquals(ResourceType.DRL, rm.getType());
        }
    }

}
