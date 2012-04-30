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
package org.switchyard.component.bpm.config.model;

import java.io.File;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import junit.framework.Assert;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;
import org.switchyard.common.io.pull.StringPuller;
import org.switchyard.common.io.resource.ResourceType;
import org.switchyard.common.type.Classes;
import org.switchyard.component.bpm.task.work.SwitchYardServiceTaskHandler;
import org.switchyard.component.common.rules.config.model.MappingModel;
import org.switchyard.component.common.rules.expression.Expression;
import org.switchyard.component.common.rules.expression.ExpressionFactory;
import org.switchyard.component.common.rules.expression.ExpressionType;
import org.switchyard.config.Configuration;
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
 * Tests BPM models.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class BPMModelTests {

    private static final String COMPLETE_XML = "/org/switchyard/component/bpm/config/model/BPMModelTests-Complete.xml";

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
        Assert.assertTrue(implementation instanceof BPMComponentImplementationModel);
        BPMComponentImplementationModel bci = (BPMComponentImplementationModel)implementation;
        Assert.assertEquals("bpm", bci.getType());
        Assert.assertEquals("foobar.bpmn", bci.getProcessDefinition().getLocation());
        Assert.assertSame(ResourceType.valueOf("BPMN2"), bci.getProcessDefinition().getType());
        Assert.assertEquals("foobar", bci.getProcessId());
        Configuration config = bci.getModelConfiguration();
        Assert.assertEquals("implementation.bpm", config.getName());
        QName qname = config.getQName();
        Assert.assertEquals("urn:switchyard-component-bpm:config:1.0", qname.getNamespaceURI());
        Assert.assertEquals("implementation.bpm", qname.getLocalPart());
        ResourceModel prm = bci.getResources().iterator().next();
        Assert.assertEquals("foobar.drl", prm.getLocation());
        Assert.assertSame(ResourceType.valueOf("DRL"), prm.getType());
        TaskHandlerModel th = bci.getTaskHandlers().iterator().next();
        Assert.assertEquals(SwitchYardServiceTaskHandler.class, th.getClazz());
        Assert.assertNull(th.getName());
        MappingModel pmm = bci.getParameters().getMappings().iterator().next();
        Assert.assertEquals("payload", pmm.getVariable());
        Expression pexpr = ExpressionFactory.instance().create(pmm);
        Assert.assertEquals("message.content", pexpr.getExpression());
        Assert.assertEquals(ExpressionType.MVEL, pexpr.getType());
        MappingModel rmm = bci.getResults().getMappings().iterator().next();
        Assert.assertNull(rmm.getVariable());
        Expression rexpr = ExpressionFactory.instance().create(rmm);
        Assert.assertEquals("message.content = payload;", rexpr.getExpression());
        Assert.assertEquals(ExpressionType.MVEL, rexpr.getType());
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
        switchyard.assertModelValid();
    }

    @Test
    public void testScanForProcesses() throws Exception {
        Scanner<SwitchYardModel> scanner = new BPMSwitchYardScanner();
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
        Assert.assertEquals(2, cm_list.size());
        for (ComponentModel c : cm_list) {
            BPMComponentImplementationModel bci = (BPMComponentImplementationModel)c.getImplementation();
            String processId = bci.getProcessId();
            if ("SimpleProcess".equals(processId)) {
                Assert.assertEquals("META-INF/SimpleProcess.bpmn", bci.getProcessDefinition().getLocation());
                Assert.assertSame(ResourceType.valueOf("BPMN"), bci.getProcessDefinition().getType());
            } else if ("ComplexProcess".equals(processId)) {
                Assert.assertEquals("path/to/my.bpmn", bci.getProcessDefinition().getLocation());
                Assert.assertSame(ResourceType.valueOf("BPMN2"), bci.getProcessDefinition().getType());
                Iterator<ResourceModel> rm_iter = bci.getResources().iterator();
                ResourceModel rm = rm_iter.next();
                Assert.assertEquals("path/to/my.dsl", rm.getLocation());
                Assert.assertSame(ResourceType.valueOf("DSL"), rm.getType());
                rm = rm_iter.next();
                Assert.assertEquals("path/to/my.dslr", rm.getLocation());
                Assert.assertSame(ResourceType.valueOf("DSLR"), rm.getType());
                Iterator<TaskHandlerModel> th_iter = bci.getTaskHandlers().iterator();
                TaskHandlerModel th = th_iter.next();
                Assert.assertEquals(SwitchYardServiceTaskHandler.class, th.getClazz());
                Assert.assertEquals(SwitchYardServiceTaskHandler.SWITCHYARD_SERVICE, th.getName());
                th = th_iter.next();
                Assert.assertEquals(ComplexProcess.My1stHandler.class, th.getClazz());
                Assert.assertEquals("My1stHandler", th.getName());
                th = th_iter.next();
                Assert.assertEquals(ComplexProcess.My2ndHandler.class, th.getClazz());
                Assert.assertEquals("My2ndHandler", th.getName());
            } else {
                Assert.fail(processId);
            }
        }
    }

    @Test
    public void testEmptyScan() throws Exception {
        Scanner<SwitchYardModel> scanner = new BPMSwitchYardScanner();
        ScannerInput<SwitchYardModel> input = new ScannerInput<SwitchYardModel>();
        ScannerOutput<SwitchYardModel> output = scanner.scan(input);
        Assert.assertNull("Composite element should not be created if no components were found.", output.getModel().getComposite());
    }

}
