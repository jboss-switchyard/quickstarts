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
package org.switchyard.component.bpm.service;

import static org.switchyard.component.bpm.BPMConstants.CORRELATION_KEY_PROPERTY;
import static org.switchyard.component.bpm.BPMConstants.PROCESSS_INSTANCE_ID_PROPERTY;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.activation.DataSource;
import javax.xml.namespace.QName;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.switchyard.BaseHandler;
import org.switchyard.Exchange;
import org.switchyard.HandlerException;
import org.switchyard.Message;
import org.switchyard.Service;
import org.switchyard.ServiceDomain;
import org.switchyard.component.bpm.annotation.BPM;
import org.switchyard.component.bpm.annotation.SignalEvent;
import org.switchyard.component.bpm.annotation.SignalEventAll;
import org.switchyard.component.bpm.annotation.StartProcess;
import org.switchyard.component.bpm.annotation.WorkItemHandler;
import org.switchyard.component.bpm.config.model.BPMComponentImplementationModel;
import org.switchyard.component.bpm.config.model.BPMSwitchYardScanner;
import org.switchyard.component.bpm.exchange.BPMExchangeHandler;
import org.switchyard.component.common.knowledge.annotation.Input;
import org.switchyard.component.common.knowledge.annotation.Manifest;
import org.switchyard.component.common.knowledge.annotation.Output;
import org.switchyard.component.common.knowledge.annotation.Resource;
import org.switchyard.component.common.knowledge.service.SwitchYardServiceInvoker;
import org.switchyard.extensions.java.JavaService;
import org.switchyard.metadata.InOnlyService;
import org.switchyard.metadata.InOutService;
import org.switchyard.test.InvocationFaultException;
import org.switchyard.test.Invoker;
import org.switchyard.test.SwitchYardRunner;
import org.switchyard.test.TestDataSource;

/**
 * Tests the BPM implementation.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
@RunWith(SwitchYardRunner.class)
public class BPMServiceTests {

    private static final String ACCESS_ATTACHMENT_BPMN = "org/switchyard/component/bpm/service/BPMServiceTests-AccessAttachment.bpmn";
    private static final String CALL_SERVICE_BPMN = "org/switchyard/component/bpm/service/BPMServiceTests-CallService.bpmn";
    private static final String CONTROL_PROCESS_BPMN = "org/switchyard/component/bpm/service/BPMServiceTests-ControlProcess.bpmn";
    private static final String FAULT_RESULT_PROCESS_BPMN = "org/switchyard/component/bpm/service/BPMServiceTests-FaultResultProcess.bpmn";
    private static final String FAULT_EVENT_PROCESS_BPMN = "org/switchyard/component/bpm/service/BPMServiceTests-FaultEventProcess.bpmn";
    private static final String FAULT_BOUNDARY_PROCESS_BPMN = "org/switchyard/component/bpm/service/BPMServiceTests-FaultBoundaryProcess.bpmn";
    private static final String REUSE_HANDLER_BPMN = "org/switchyard/component/bpm/service/BPMServiceTests-ReuseHandler.bpmn";
    private static final String RULES_FIRED_BPMN = "org/switchyard/component/bpm/service/BPMServiceTests-RulesFired.bpmn";
    private static final String RULES_FIRED_DRL = "org/switchyard/component/bpm/service/BPMServiceTests-RulesFired.drl";
    private static final String SIGNAL_PROCESS_BPMN = "org/switchyard/component/bpm/service/BPMServiceTests-SignalProcess.bpmn";

    private ServiceDomain serviceDomain;

    @Test
    public void testCallService() throws Exception {
        final Holder holder = new Holder();
        serviceDomain.registerService(new QName("CallService"), new InOnlyService(), new BaseHandler(){
            public void handleMessage(Exchange exchange) throws HandlerException {
                holder.setValue("message handled");
            }
        });
        serviceDomain.registerServiceReference(new QName("CallService"), new InOnlyService());
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource(CALL_SERVICE_BPMN), ResourceType.BPMN2);
        KieBase kbase = kbuilder.newKnowledgeBase();
        KieSession ksession = kbase.newKieSession();
        SwitchYardServiceTaskHandler ssth = new SwitchYardServiceTaskHandler();
        ssth.setProcessRuntime(ksession);
        ssth.setInvoker(new SwitchYardServiceInvoker(serviceDomain));
        ksession.getWorkItemManager().registerWorkItemHandler(ssth.getName(), ssth);
        ksession.startProcess("CallService");
        ksession.halt();
        ksession.dispose();
        Assert.assertEquals("message handled", holder.getValue());
    }

    @BPM(processId="AccessAttachment", manifest=@Manifest(resources=@Resource(location=ACCESS_ATTACHMENT_BPMN, type="BPMN2")))
    public interface AccessAttachment {
        @StartProcess(inputs={
            @Input(from="message.attachmentMap['someAttach']", to="attachment"),
            @Input(from="message.content", to="holder")
        })
        public Object process(Object content);
    }

    @Test
    public void testAccessAttachment() throws Exception {
        final Holder holder = new Holder();
        BPMComponentImplementationModel bci_model = (BPMComponentImplementationModel)new BPMSwitchYardScanner().scan(AccessAttachment.class).getImplementation();
        QName serviceName = new QName("AccessAttachment");
        BPMExchangeHandler handler = new BPMExchangeHandler(bci_model, serviceDomain, serviceName);
        Service service = serviceDomain.registerService(serviceName, JavaService.fromClass(AccessAttachment.class), handler);
        serviceDomain.registerServiceReference(service.getName(), service.getInterface(), service.getProviderHandler());
        handler.start();
        DataSource attachment = new TestDataSource("someAttach", "text/plain", "someAttachData");
        new Invoker(serviceDomain, serviceName).operation("process").attachment(attachment.getName(), attachment).sendInOnly(holder);
        handler.stop();
        Assert.assertEquals("someAttachData", holder.getValue());
    }

    @BPM(processId="ControlProcess", manifest=@Manifest(resources=@Resource(location=CONTROL_PROCESS_BPMN, type="BPMN2")))
    public interface ControlProcess {
        @StartProcess
        public Object process(Object content);
        @SignalEvent(eventId="test")
        public void signal(Object content);
    }

    @Test
    public void testControlProcess() throws Exception {
        final Holder holder = new Holder();
        Service callService = serviceDomain.registerService(new QName("CallService"), new InOnlyService(), new BaseHandler(){
            public void handleMessage(Exchange exchange) throws HandlerException {
                holder.setValue("message handled");
            }
        });
        serviceDomain.registerServiceReference(callService.getName(), callService.getInterface(), callService.getProviderHandler());
        BPMComponentImplementationModel bci_model = (BPMComponentImplementationModel)new BPMSwitchYardScanner().scan(ControlProcess.class).getImplementation();
        // setting the component name to null so that the service reference doesn't use the component-qualified name
        bci_model.getComponent().setName(null);
        QName serviceName = new QName("ControlProcess");
        BPMExchangeHandler handler = new BPMExchangeHandler(bci_model, serviceDomain, serviceName);
        Service controlService = serviceDomain.registerService(serviceName, JavaService.fromClass(ControlProcess.class), handler);
        serviceDomain.registerServiceReference(controlService.getName(), controlService.getInterface(), controlService.getProviderHandler());
        handler.start();
        Invoker processInvoker = new Invoker(serviceDomain, serviceName);
        Message processResponse = processInvoker.operation("process").sendInOut(null);
        Long processInstanceId = (Long)processResponse.getContext().getPropertyValue(PROCESSS_INSTANCE_ID_PROPERTY);
        Invoker signalInvoker = new Invoker(serviceDomain, serviceName);
        signalInvoker.operation("signal").property(PROCESSS_INSTANCE_ID_PROPERTY, processInstanceId).sendInOut(null);
        handler.stop();
        Assert.assertEquals("message handled", holder.getValue());
    }

    @Test
    public void testCorrelateProcessSuccess() throws Exception {
        runCorrelateProcess(false);
    }

    @Test
    public void testCorrelateProcessFailure() throws Exception {
        runCorrelateProcess(true);
    }

    private void runCorrelateProcess(final boolean bomb) throws Exception {
        final AtomicInteger counter = new AtomicInteger();
        final Holder holder = new Holder();
        Service callService = serviceDomain.registerService(new QName("CallService"), new InOnlyService(), new BaseHandler(){
            public void handleMessage(Exchange exchange) throws HandlerException {
                int count = counter.incrementAndGet();
                holder.setValue(String.valueOf(count));
            }
        });
        serviceDomain.registerServiceReference(callService.getName(), callService.getInterface(), callService.getProviderHandler());
        BPMComponentImplementationModel bci_model = (BPMComponentImplementationModel)new BPMSwitchYardScanner().scan(ControlProcess.class).getImplementation();
        // setting the component name to null so that the service reference doesn't use the component-qualified name
        bci_model.getComponent().setName(null);
        QName serviceName = new QName("ControlProcess");
        BPMExchangeHandler handler = new BPMExchangeHandler(bci_model, serviceDomain, serviceName);
        Service controlService = serviceDomain.registerService(serviceName, JavaService.fromClass(ControlProcess.class), handler);
        serviceDomain.registerServiceReference(controlService.getName(), controlService.getInterface(), controlService.getProviderHandler());
        handler.start();
        new Invoker(serviceDomain, serviceName).operation("process").property(CORRELATION_KEY_PROPERTY, "A").sendInOnly(null);
        new Invoker(serviceDomain, serviceName).operation("process").property(CORRELATION_KEY_PROPERTY, "B").sendInOnly(null);
        new Invoker(serviceDomain, serviceName).operation("signal").property(CORRELATION_KEY_PROPERTY, "A").sendInOnly(null);
        InvocationFaultException fault = null;
        try {
            new Invoker(serviceDomain, serviceName).operation("signal").property(CORRELATION_KEY_PROPERTY, bomb ? "A" : "B").sendInOut(null);
        } catch (InvocationFaultException ife) {
            fault = ife;
        }
        handler.stop();
        if (bomb) {
            Assert.assertNotNull(fault);
            Assert.assertEquals("1", holder.getValue());
        } else {
            Assert.assertNull(fault);
            Assert.assertEquals("2", holder.getValue());
        }
    }

    @BPM(processId="ControlProcess", manifest=@Manifest(resources=@Resource(location=CONTROL_PROCESS_BPMN, type="BPMN2")))
    public interface SignalAllProcesses {
        @StartProcess
        public Object process(Object content);
        @SignalEventAll(eventId="test")
        public void signal(Object content);
    }

    @Test
    public void testSignalAllProcesses() throws Exception {
        final AtomicInteger counter = new AtomicInteger();
        final Holder holder = new Holder();
        Service callService = serviceDomain.registerService(new QName("CallService"), new InOnlyService(), new BaseHandler(){
            public void handleMessage(Exchange exchange) throws HandlerException {
                int count = counter.incrementAndGet();
                holder.setValue(String.valueOf(count));
            }
        });
        serviceDomain.registerServiceReference(callService.getName(), callService.getInterface(), callService.getProviderHandler());
        BPMComponentImplementationModel bci_model = (BPMComponentImplementationModel)new BPMSwitchYardScanner().scan(SignalAllProcesses.class).getImplementation();
        // setting the component name to null so that the service reference doesn't use the component-qualified name
        bci_model.getComponent().setName(null);
        QName serviceName = new QName("ControlProcess");
        BPMExchangeHandler handler = new BPMExchangeHandler(bci_model, serviceDomain, serviceName);
        Service controlService = serviceDomain.registerService(serviceName, JavaService.fromClass(SignalAllProcesses.class), handler);
        serviceDomain.registerServiceReference(controlService.getName(), controlService.getInterface(), controlService.getProviderHandler());
        handler.start();
        new Invoker(serviceDomain, serviceName).operation("process").sendInOnly(null);
        new Invoker(serviceDomain, serviceName).operation("process").sendInOnly(null);
        new Invoker(serviceDomain, serviceName).operation("signal").sendInOnly(null);
        handler.stop();
        Assert.assertEquals("2", holder.getValue());
    }

    @Test
    public void testFaultResultProcessSuccess() throws Exception {
        runFaultResultProcess(false);
    }

    @Test
    public void testFaultResultProcessFailure() throws Exception {
        runFaultResultProcess(true);
    }

    private void runFaultResultProcess(final boolean bomb) throws Exception {
        serviceDomain.registerService(new QName("TestService"), new InOnlyService(), new BaseHandler(){
            public void handleMessage(Exchange exchange) throws HandlerException {
                if (bomb) {
                    throw new HandlerException("BOOM!");
                }
            }
        });
        serviceDomain.registerServiceReference(new QName("TestService"), new InOutService());
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource(FAULT_RESULT_PROCESS_BPMN), ResourceType.BPMN2);
        KieBase kbase = kbuilder.newKnowledgeBase();
        KieSession ksession = kbase.newKieSession();
        SwitchYardServiceTaskHandler ssth = new SwitchYardServiceTaskHandler();
        ssth.setProcessRuntime(ksession);
        ssth.setInvoker(new SwitchYardServiceInvoker(serviceDomain));
        ksession.getWorkItemManager().registerWorkItemHandler(ssth.getName(), ssth);
        WorkflowProcessInstance wpi = (WorkflowProcessInstance)ksession.startProcess("FaultResultProcess");
        HandlerException he = (HandlerException)wpi.getVariable("faultResult");
        if (bomb) {
            Assert.assertNotNull(he);
            Assert.assertEquals("BOOM!", he.getMessage());
        } else {
            Assert.assertNull(he);
        }
        ksession.halt();
        ksession.dispose();
    }

    @Test
    public void testFaultEventProcessSuccess() throws Exception {
        runFaultEventProcess(false);
    }

    @Test
    public void testFaultEventProcessFailure() throws Exception {
        runFaultEventProcess(true);
    }

    private void runFaultEventProcess(final boolean bomb) throws Exception {
        serviceDomain.registerService(new QName("TestService"), new InOnlyService(), new BaseHandler(){
            public void handleMessage(Exchange exchange) throws HandlerException {
                if (bomb) {
                    throw new HandlerException("BOOM!");
                }
            }
        });
        serviceDomain.registerServiceReference(new QName("TestService"), new InOutService());
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource(FAULT_EVENT_PROCESS_BPMN), ResourceType.BPMN2);
        KieBase kbase = kbuilder.newKnowledgeBase();
        KieSession ksession = kbase.newKieSession();
        SwitchYardServiceTaskHandler ssth = new SwitchYardServiceTaskHandler();
        ssth.setProcessRuntime(ksession);
        ssth.setInvoker(new SwitchYardServiceInvoker(serviceDomain));
        ksession.getWorkItemManager().registerWorkItemHandler(ssth.getName(), ssth);
        WorkflowProcessInstance wpi = (WorkflowProcessInstance)ksession.startProcess("FaultEventProcess");
        HandlerException he = (HandlerException)wpi.getVariable("faultEvent");
        if (bomb) {
            Assert.assertNotNull(he);
            Assert.assertEquals("BOOM!", he.getMessage());
        } else {
            Assert.assertNull(he);
        }
        ksession.halt();
        ksession.dispose();
    }

    @Test
    public void testFaultBoundaryProcessSuccess() throws Exception {
        runFaultBoundaryProcess(false);
    }

    @Test
    public void testFaultBoundaryProcessFailure() throws Exception {
        runFaultBoundaryProcess(true);
    }

    private void runFaultBoundaryProcess(final boolean bomb) throws Exception {
        serviceDomain.registerService(new QName("TestService"), new InOnlyService(), new BaseHandler(){
            public void handleMessage(Exchange exchange) throws HandlerException {
                if (bomb) {
                    throw new HandlerException("BOOM!");
                }
            }
        });
        serviceDomain.registerServiceReference(new QName("TestService"), new InOutService());
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource(FAULT_BOUNDARY_PROCESS_BPMN), ResourceType.BPMN2);
        KieBase kbase = kbuilder.newKnowledgeBase();
        KieSession ksession = kbase.newKieSession();
        SwitchYardServiceTaskHandler ssth = new SwitchYardServiceTaskHandler();
        ssth.setProcessRuntime(ksession);
        ssth.setInvoker(new SwitchYardServiceInvoker(serviceDomain));
        ksession.getWorkItemManager().registerWorkItemHandler(ssth.getName(), ssth);
        WorkflowProcessInstance wpi = (WorkflowProcessInstance)ksession.startProcess("FaultBoundaryProcess");
        String output = (String)wpi.getVariable("TestOutput");
        Assert.assertEquals(bomb ? "Failure" : "Success", output);
        ksession.halt();
        ksession.dispose();
    }

    @BPM(processId="ReuseHandler", manifest=@Manifest(resources=@Resource(location=REUSE_HANDLER_BPMN, type="BPMN2")),
            workItemHandlers=@WorkItemHandler(name="ReuseHandler", value=ReuseHandler.class))
    public interface ReuseHandlerProcess {
        @StartProcess(inputs=@Input(from="message.content", to="holder"))
        public void process(Object content);
    }

    @Test
    public void testReuseHandler() throws Exception {
        BPMComponentImplementationModel bci_model = (BPMComponentImplementationModel)new BPMSwitchYardScanner().scan(ReuseHandlerProcess.class).getImplementation();
        QName serviceName = new QName("ReuseHandler");
        BPMExchangeHandler handler = new BPMExchangeHandler(bci_model, serviceDomain, serviceName);
        Service service = serviceDomain.registerService(serviceName, new InOnlyService("process"), handler);
        serviceDomain.registerServiceReference(service.getName(), service.getInterface(), service.getProviderHandler());
        handler.start();
        new Invoker(serviceDomain, serviceName).operation("process").sendInOnly(null);
        handler.stop();
        Assert.assertEquals("handler executed", ReuseHandler._holder.getValue());
        ReuseHandler._holder.setValue(null);
    }

    @BPM(processId="RulesFired", manifest=@Manifest(resources={
            @Resource(location=RULES_FIRED_BPMN, type="BPMN2"),
            @Resource(location=RULES_FIRED_DRL, type="DRL")}))
    public interface RulesFiredProcess {
        @StartProcess(inputs=@Input(from="message.content", to="holder"))
        public void process(Object content);
    }

    @Test
    public void testRulesFired() throws Exception {
        final Holder holder = new Holder();
        BPMComponentImplementationModel bci_model = (BPMComponentImplementationModel)new BPMSwitchYardScanner().scan(RulesFiredProcess.class).getImplementation();
        QName serviceName = new QName("RulesFired");
        BPMExchangeHandler handler = new BPMExchangeHandler(bci_model, serviceDomain, serviceName);
        Service service = serviceDomain.registerService(serviceName, new InOnlyService("process"), handler);
        serviceDomain.registerServiceReference(service.getName(), service.getInterface(), service.getProviderHandler());
        handler.start();
        new Invoker(serviceDomain, serviceName).operation("process").sendInOnly(holder);
        handler.stop();
        Assert.assertEquals("rules fired", holder.getValue());
    }

    @BPM(processId="SignalProcess", manifest=@Manifest(resources=@Resource(location=SIGNAL_PROCESS_BPMN, type="BPMN2")))
    public interface SignalProcess {
        @StartProcess(
            inputs={@Input(from="message.content", to="Parameter")},
            outputs={@Output(from="Result", to="message.content")}
        )
        public Object process(Object content);
        @SignalEvent(
            eventId="Signal_1",
            inputs={@Input(from="message.content", to="Parameter")},
            outputs={@Output(from="Result", to="message.content")}
        )
        public void signal(Object content);
    }

    @Test
    public void testSignalProcess() throws Exception {
        final Map<String,String> testAssertionMap = new HashMap<String,String>();
        Service serviceOne = serviceDomain.registerService(new QName("ServiceOne"), new InOnlyService(), new BaseHandler(){
            public void handleMessage(Exchange exchange) throws HandlerException {
                Holder h = exchange.getMessage().getContent(Holder.class);
                testAssertionMap.put("ServiceOne", h.getValue());
            }
        });
        Service serviceTwo = serviceDomain.registerService(new QName("ServiceTwo"), new InOutService(), new BaseHandler(){
            public void handleMessage(Exchange exchange) throws HandlerException {
                Holder h = exchange.getMessage().getContent(Holder.class);
                testAssertionMap.put("ServiceTwo", h.getValue());
            }
        });
        serviceDomain.registerServiceReference(serviceOne.getName(), serviceOne.getInterface(), serviceOne.getProviderHandler());
        serviceDomain.registerServiceReference(serviceTwo.getName(), serviceTwo.getInterface(), serviceTwo.getProviderHandler());
        BPMComponentImplementationModel bci_model = (BPMComponentImplementationModel)new BPMSwitchYardScanner().scan(SignalProcess.class).getImplementation();
        // setting the component name to null so that the service reference doesn't use the component-qualified name
        bci_model.getComponent().setName(null);
        QName serviceName = new QName("SignalProcess");
        BPMExchangeHandler handler = new BPMExchangeHandler(bci_model, serviceDomain, serviceName);
        Service signalService = serviceDomain.registerService(serviceName, JavaService.fromClass(SignalProcess.class), handler);
        serviceDomain.registerServiceReference(signalService.getName(), signalService.getInterface(), signalService.getProviderHandler());
        handler.start();
        Invoker processInvoker = new Invoker(serviceDomain, serviceName);
        Holder holderOne = new Holder();
        holderOne.setValue("HolderOne");
        Message processResponse = processInvoker.operation("process").sendInOut(holderOne);
        Long processInstanceId = (Long)processResponse.getContext().getPropertyValue(PROCESSS_INSTANCE_ID_PROPERTY);
        Invoker signalInvoker = new Invoker(serviceDomain, serviceName);
        Holder holderTwo = new Holder();
        holderTwo.setValue("HolderTwo");
        Message signalResponse = signalInvoker.operation("signal").property(PROCESSS_INSTANCE_ID_PROPERTY, processInstanceId).sendInOut(holderTwo);
        Holder holderResponse = signalResponse.getContent(Holder.class);
        handler.stop();
        Assert.assertEquals(holderOne.getValue(), testAssertionMap.get("ServiceOne"));
        Assert.assertEquals(holderTwo.getValue(), testAssertionMap.get("ServiceTwo"));
        Assert.assertEquals(holderTwo.getValue(), holderResponse.getValue());
    }

    public static final class Holder {
        private String _value;
        public String getValue() { return _value; }
        public void setValue(String value) { _value = value; }
        public String toString() { return _value; }
    }

}
