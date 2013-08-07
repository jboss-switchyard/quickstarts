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
package org.switchyard.component.rules.config.model;

import java.io.File;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.builder.ReleaseId;
import org.kie.api.event.rule.DebugWorkingMemoryEventListener;
import org.kie.api.runtime.Channel;
import org.switchyard.common.io.pull.StringPuller;
import org.switchyard.common.io.resource.ResourceDetail;
import org.switchyard.common.io.resource.ResourceType;
import org.switchyard.common.type.Classes;
import org.switchyard.component.common.knowledge.LoggerType;
import org.switchyard.component.common.knowledge.config.model.ChannelModel;
import org.switchyard.component.common.knowledge.config.model.ContainerModel;
import org.switchyard.component.common.knowledge.config.model.FaultModel;
import org.switchyard.component.common.knowledge.config.model.GlobalModel;
import org.switchyard.component.common.knowledge.config.model.InputModel;
import org.switchyard.component.common.knowledge.config.model.ListenerModel;
import org.switchyard.component.common.knowledge.config.model.LoggerModel;
import org.switchyard.component.common.knowledge.config.model.ManifestModel;
import org.switchyard.component.common.knowledge.config.model.OperationModel;
import org.switchyard.component.common.knowledge.config.model.OutputModel;
import org.switchyard.component.common.knowledge.util.Containers;
import org.switchyard.component.rules.RulesOperationType;
import org.switchyard.config.model.ModelPuller;
import org.switchyard.config.model.Scanner;
import org.switchyard.config.model.ScannerInput;
import org.switchyard.config.model.ScannerOutput;
import org.switchyard.config.model.composite.ComponentImplementationModel;
import org.switchyard.config.model.composite.ComponentModel;
import org.switchyard.config.model.composite.CompositeModel;
import org.switchyard.config.model.property.PropertyModel;
import org.switchyard.config.model.resource.ResourceModel;
import org.switchyard.config.model.resource.ResourcesModel;
import org.switchyard.config.model.switchyard.SwitchYardModel;

/**
 * Tests Rules models.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class RulesModelTests {

    private static final String CONTAINER_XML = "/org/switchyard/component/rules/config/model/RulesModelTests-Container.xml";
    private static final String RESOURCES_XML = "/org/switchyard/component/rules/config/model/RulesModelTests-Resources.xml";

    private ModelPuller<SwitchYardModel> _puller;

    @Before
    public void before() throws Exception {
        _puller = new ModelPuller<SwitchYardModel>();
    }

    @After
    public void after() throws Exception {
        _puller = null;
    }

    @Test
    public void testReadContainer() throws Exception {
        doTestRead(CONTAINER_XML);
    }

    @Test
    public void testReadResources() throws Exception {
        doTestRead(RESOURCES_XML);
    }

    private void doTestRead(String xml) throws Exception {
        ClassLoader loader = getClass().getClassLoader();
        doTestModel(_puller.pull(xml, loader), xml, loader);
    }

    private void doTestModel(SwitchYardModel switchyard, String xml, ClassLoader loader) throws Exception {
        CompositeModel composite = switchyard.getComposite();
        ComponentModel component = null;
        for (ComponentModel c : composite.getComponents()) {
            if (DoStuffRules.class.getSimpleName().equals(c.getName())) {
                component = c;
                break;
            }
        }
        ComponentImplementationModel implementation = component.getImplementation();
        Assert.assertTrue(implementation instanceof RulesComponentImplementationModel);
        RulesComponentImplementationModel rules = (RulesComponentImplementationModel)implementation;
        Assert.assertEquals("rules", rules.getType());
        OperationModel operation = rules.getOperations().getOperations().get(0);
        Assert.assertEquals("theEventId", operation.getEventId());
        Assert.assertEquals("process", operation.getName());
        Assert.assertEquals(RulesOperationType.FIRE_UNTIL_HALT, operation.getType());
        GlobalModel globalModel = operation.getGlobals().getGlobals().get(0);
        Assert.assertEquals("context['foobar']", globalModel.getFrom());
        Assert.assertEquals("globalVar", globalModel.getTo());
        InputModel inputModel = operation.getInputs().getInputs().get(0);
        Assert.assertEquals("message.content.nested", inputModel.getFrom());
        Assert.assertEquals("inputVar", inputModel.getTo());
        OutputModel outputModel = operation.getOutputs().getOutputs().get(0);
        Assert.assertEquals("outputVar", outputModel.getFrom());
        Assert.assertEquals("message.content", outputModel.getTo());
        FaultModel faultModel = operation.getFaults().getFaults().get(0);
        Assert.assertEquals("faultVar", faultModel.getFrom());
        Assert.assertEquals("message.content", faultModel.getTo());
        ChannelModel channel = rules.getChannels().getChannels().get(0);
        Assert.assertEquals(TestChannel.class, channel.getClazz(loader));
        Assert.assertEquals("theName", channel.getName());
        Assert.assertEquals("theOperation", channel.getOperation());
        Assert.assertEquals("theReference", channel.getReference());
        ListenerModel listener = rules.getListeners().getListeners().get(0);
        Assert.assertEquals(DebugWorkingMemoryEventListener.class, listener.getClazz(loader));
        LoggerModel logger = rules.getLoggers().getLoggers().get(0);
        Assert.assertEquals(Integer.valueOf(2000), logger.getInterval());
        Assert.assertEquals("theLog", logger.getLog());
        Assert.assertEquals(LoggerType.CONSOLE, logger.getType());
        ManifestModel manifest = rules.getManifest();
        ContainerModel container = manifest.getContainer();
        ResourcesModel resources = manifest.getResources();
        Assert.assertTrue((container != null && resources == null) || (container == null && resources != null));
        if (CONTAINER_XML.equals(xml)) {
            ReleaseId rid = Containers.toReleaseId(container.getReleaseId());
            Assert.assertEquals("theGroupId", rid.getGroupId());
            Assert.assertEquals("theArtifactId", rid.getArtifactId());
            Assert.assertEquals("theVersion", rid.getVersion());
            Assert.assertEquals("theBase", container.getBaseName());
            Assert.assertEquals("theSession", container.getSessionName());
            Assert.assertTrue(container.isScan());
            Assert.assertEquals(Long.valueOf(1000), container.getScanInterval());
            Assert.assertNull(resources);
        } else if (RESOURCES_XML.equals(xml)) {
            Assert.assertNull(container);
            ResourceModel drlResource = resources.getResources().get(0);
            Assert.assertEquals("foo.drl", drlResource.getLocation());
            Assert.assertEquals(ResourceType.valueOf("DRL"), drlResource.getType());
            ResourceModel dslResource = resources.getResources().get(1);
            Assert.assertEquals("bar.dsl", dslResource.getLocation());
            Assert.assertEquals(ResourceType.valueOf("DSL"), dslResource.getType());
            ResourceModel dtableResource = resources.getResources().get(2);
            Assert.assertEquals("foobar.xls", dtableResource.getLocation());
            Assert.assertEquals(ResourceType.valueOf("DTABLE"), dtableResource.getType());
            ResourceDetail dtableDetail = dtableResource.getDetail();
            Assert.assertEquals("XLS", dtableDetail.getInputType());
            Assert.assertEquals("MySheet", dtableDetail.getWorksheetName());
            Assert.assertEquals(true, dtableDetail.isUsingExternalTypes());
        }
        PropertyModel property = rules.getProperties().getProperties().get(0);
        Assert.assertEquals("foo", property.getName());
        Assert.assertEquals("bar", property.getValue());
    }

    @Test
    public void testWriteContainer() throws Exception {
        doTestWrite(CONTAINER_XML);
    }

    @Test
    public void testWriteResources() throws Exception {
        doTestWrite(RESOURCES_XML);
    }

    private void doTestWrite(String xml) throws Exception {
        String old_xml = new StringPuller().pull(xml, getClass());
        SwitchYardModel switchyard = _puller.pull(new StringReader(old_xml));
        String new_xml = switchyard.toString();
        XMLUnit.setIgnoreWhitespace(true);
        Diff diff = XMLUnit.compareXML(old_xml, new_xml);
        Assert.assertTrue(diff.toString(), diff.identical());
    }

    @Test
    public void testValidateContainer() throws Exception {
        doTestValidate(CONTAINER_XML);
    }

    @Test
    public void testValidateResources() throws Exception {
        doTestValidate(RESOURCES_XML);
    }

    private void doTestValidate(String xml) throws Exception {
        SwitchYardModel switchyard = _puller.pull(xml, getClass());
        switchyard.assertModelValid();
    }

    /*
    @Test
    public void testScanContainer() throws Exception {
        doTestScan(CONTAINER_XML);
    }
    */

    @Test
    public void testScanResources() throws Exception {
        doTestScan(RESOURCES_XML);
    }

    private void doTestScan(String xml) throws Exception {
        ClassLoader loader = getClass().getClassLoader();
        Scanner<SwitchYardModel> scanner = new RulesSwitchYardScanner();
        ScannerInput<SwitchYardModel> input = new ScannerInput<SwitchYardModel>().setName(getClass().getSimpleName());
        List<URL> urls = new ArrayList<URL>();
        String resPath = getClass().getName().replaceAll("\\.", "/") + ".class";
        String urlPath = Classes.getResource(resPath).getPath();
        File file = new File(urlPath.substring(0, urlPath.length() - resPath.length()));
        urls.add(file.toURI().toURL());
        input.setURLs(urls);
        ScannerOutput<SwitchYardModel> output = scanner.scan(input);
        SwitchYardModel model = output.getModel();
        CompositeModel composite = model.getComposite();
        Assert.assertEquals(getClass().getSimpleName(), composite.getName());
        doTestModel(model, xml, loader);
    }

    @Test
    public void testScanEmpty() throws Exception {
        Scanner<SwitchYardModel> scanner = new RulesSwitchYardScanner();
        ScannerInput<SwitchYardModel> input = new ScannerInput<SwitchYardModel>();
        ScannerOutput<SwitchYardModel> output = scanner.scan(input);
        Assert.assertNull("Composite element should not be created if no components were found.", output.getModel().getComposite());
    }

    public static final class TestChannel implements Channel {
        @Override
        public void send(Object object) {
            System.out.println(object);
        }
    }

}
