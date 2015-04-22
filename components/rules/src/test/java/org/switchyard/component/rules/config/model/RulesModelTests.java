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

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.builder.ReleaseId;
import org.kie.api.event.rule.DebugRuleRuntimeEventListener;
import org.kie.api.runtime.Channel;
import org.switchyard.common.io.pull.StringPuller;
import org.switchyard.common.io.resource.ResourceDetail;
import org.switchyard.common.io.resource.ResourceType;
import org.switchyard.common.type.Classes;
import org.switchyard.component.common.knowledge.LoggerType;
import org.switchyard.component.common.knowledge.config.manifest.ContainerManifest;
import org.switchyard.component.common.knowledge.config.model.ChannelModel;
import org.switchyard.component.common.knowledge.config.model.ContainerModel;
import org.switchyard.component.common.knowledge.config.model.ExtraJaxbClassModel;
import org.switchyard.component.common.knowledge.config.model.ExtraJaxbClassesModel;
import org.switchyard.component.common.knowledge.config.model.FaultModel;
import org.switchyard.component.common.knowledge.config.model.GlobalModel;
import org.switchyard.component.common.knowledge.config.model.InputModel;
import org.switchyard.component.common.knowledge.config.model.ListenerModel;
import org.switchyard.component.common.knowledge.config.model.LoggerModel;
import org.switchyard.component.common.knowledge.config.model.ManifestModel;
import org.switchyard.component.common.knowledge.config.model.OperationModel;
import org.switchyard.component.common.knowledge.config.model.OutputModel;
import org.switchyard.component.common.knowledge.config.model.RemoteJmsModel;
import org.switchyard.component.common.knowledge.config.model.RemoteModel;
import org.switchyard.component.common.knowledge.config.model.RemoteRestModel;
import org.switchyard.component.rules.operation.RulesOperationType;
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
    private static final String REMOTEJMS_XML = "/org/switchyard/component/rules/config/model/RulesModelTests-RemoteJms.xml";
    private static final String REMOTEREST_XML = "/org/switchyard/component/rules/config/model/RulesModelTests-RemoteRest.xml";

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

    @Test
    public void testReadRemoteJms() throws Exception {
        doTestRead(REMOTEJMS_XML);
    }

    @Test
    public void testReadRemoteRest() throws Exception {
        doTestRead(REMOTEREST_XML);
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
        Assert.assertEquals(DebugRuleRuntimeEventListener.class, listener.getClazz(loader));
        LoggerModel logger = rules.getLoggers().getLoggers().get(0);
        Assert.assertEquals(Integer.valueOf(2000), logger.getInterval());
        Assert.assertEquals("theLog", logger.getLog());
        Assert.assertEquals(LoggerType.CONSOLE, logger.getType());
        ManifestModel manifest = rules.getManifest();
        ContainerModel container = manifest.getContainer();
        ResourcesModel resources = manifest.getResources();
        RemoteModel remote = manifest.getRemote();
        if (CONTAINER_XML.equals(xml)) {
            ReleaseId rid = ContainerManifest.toReleaseId(container.getReleaseId());
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
            /* SWITCHYARD-1662
            Assert.assertEquals(true, dtableDetail.isUsingExternalTypes());
            */
        } else if (REMOTEJMS_XML.equals(xml) || REMOTEREST_XML.equals(xml)) {
            Assert.assertNull(container);
            Assert.assertNull(resources);
            Assert.assertEquals("groupId:artifactId:0.0.1", remote.getDeploymentId());
            Assert.assertEquals("kermit", remote.getUserName());
            Assert.assertEquals("the-frog-1", remote.getPassword());
            Assert.assertEquals(5, remote.getTimeout().intValue());
            ExtraJaxbClassesModel extraJaxbClasses = remote.getExtraJaxbClasses();
            Assert.assertNotNull(extraJaxbClasses);
            List<ExtraJaxbClassModel> extraJaxbClassList = extraJaxbClasses.getExtraJaxbClasses();
            Assert.assertEquals(2, extraJaxbClassList.size());
            Assert.assertEquals(Object.class, extraJaxbClassList.get(0).getClazz(loader));
            Assert.assertEquals(String.class, extraJaxbClassList.get(1).getClazz(loader));
            if (REMOTEJMS_XML.equals(xml)) {
                RemoteJmsModel remoteJms = (RemoteJmsModel)remote;
                Assert.assertEquals("remotehost", remoteJms.getHostName());
                Assert.assertEquals(4447, remoteJms.getRemotingPort().intValue());
                Assert.assertEquals(5455, remoteJms.getMessagingPort().intValue());
                Assert.assertEquals(true, remoteJms.isUseSsl());
                Assert.assertEquals("ksp", remoteJms.getKeystorePassword());
                Assert.assertEquals("/ksl", remoteJms.getKeystoreLocation());
                Assert.assertEquals("tsp", remoteJms.getTruststorePassword());
                Assert.assertEquals("/tsl", remoteJms.getTruststoreLocation());
            } else if (REMOTEREST_XML.equals(xml)) {
                RemoteRestModel remoteRest = (RemoteRestModel)remote;
                Assert.assertEquals("http://localhost:8080/kie-wb/", remoteRest.getUrl());
            }
        } else {
            Assert.fail("couldn't find container, resources, remoteJms, or remoteRest");
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

    @Test
    public void testWriteRemoteJms() throws Exception {
        doTestWrite(REMOTEJMS_XML);
    }

    @Test
    public void testWriteRemoteRest() throws Exception {
        doTestWrite(REMOTEREST_XML);
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

    @Test
    public void testValidateRemoteJms() throws Exception {
        doTestValidate(REMOTEJMS_XML);
    }

    @Test
    public void testValidateRemoteRest() throws Exception {
        doTestValidate(REMOTEREST_XML);
    }

    private void doTestValidate(String xml) throws Exception {
        SwitchYardModel switchyard = _puller.pull(xml, getClass());
        switchyard.assertModelValid();
    }

    @Test
    @Ignore
    public void testScanContainer() throws Exception {
        doTestScan(CONTAINER_XML);
    }

    @Test
    public void testScanResources() throws Exception {
        doTestScan(RESOURCES_XML);
    }

    @Test
    @Ignore
    public void testScanRemoteJms() throws Exception {
        doTestScan(REMOTEJMS_XML);
    }

    @Test
    @Ignore
    public void testScanRemoteRest() throws Exception {
        doTestScan(REMOTEREST_XML);
    }

    private void doTestScan(String xml) throws Exception {
        ClassLoader loader = getClass().getClassLoader();
        Scanner<SwitchYardModel> scanner = new RulesSwitchYardScanner();
        ScannerInput<SwitchYardModel> input = new ScannerInput<SwitchYardModel>().setCompositeName(getClass().getSimpleName());
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
