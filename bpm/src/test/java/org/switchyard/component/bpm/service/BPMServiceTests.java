/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.component.bpm.service;

import static org.switchyard.component.bpm.BPMConstants.CORRELATION_KEY_PROPERTY;
import static org.switchyard.component.bpm.BPMConstants.PROCESSS_INSTANCE_ID_PROPERTY;

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
import org.switchyard.ServiceReference;
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
    private static final String REUSE_HANDLER_BPMN = "org/switchyard/component/bpm/service/BPMServiceTests-ReuseHandler.bpmn";
    private static final String RULES_FIRED_BPMN = "org/switchyard/component/bpm/service/BPMServiceTests-RulesFired.bpmn";
    private static final String RULES_FIRED_DRL = "org/switchyard/component/bpm/service/BPMServiceTests-RulesFired.drl";

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
        Service aaService = serviceDomain.registerService(serviceName, JavaService.fromClass(AccessAttachment.class), handler);
        ServiceReference aaReference = serviceDomain.registerServiceReference(aaService.getName(), aaService.getInterface(), aaService.getProviderHandler());
        handler.start();
        Exchange exchange = aaReference.createExchange("process");
        Message message = exchange.createMessage();
        message.setContent(holder);
        DataSource attachment = new TestDataSource("someAttach", "text/plain", "someAttachData");
        message.addAttachment(attachment.getName(), attachment);
        exchange.send(message);
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

    @BPM(processId="ReuseHandler", manifest=@Manifest(resources=@Resource(location=REUSE_HANDLER_BPMN, type="BPMN2")),
            workItemHandlers=@WorkItemHandler(name="ReuseHandler", value=ReuseHandler.class))
    public interface ReuseHandlerProcess {
        @StartProcess(inputs=@Input(from="message.content", to="holder"))
        public void process(Object content);
    }

    @Test
    public void testReuseHandler() throws Exception {
        QName serviceName = new QName("ReuseHandler");
        ServiceReference serviceRef = serviceDomain.registerServiceReference(serviceName, new InOnlyService("process"));
        BPMComponentImplementationModel bci_model = (BPMComponentImplementationModel)new BPMSwitchYardScanner().scan(ReuseHandlerProcess.class).getImplementation();
        BPMExchangeHandler handler = new BPMExchangeHandler(bci_model, serviceDomain, serviceName);
        serviceDomain.registerService(serviceName, new InOnlyService("process"), handler);
        handler.start();
        Exchange exchange = serviceRef.createExchange();
        exchange.send(exchange.createMessage());
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
        QName serviceName = new QName("RulesFired");
        ServiceReference serviceRef = serviceDomain.registerServiceReference(serviceName, new InOnlyService("process"));
        BPMComponentImplementationModel bci_model = (BPMComponentImplementationModel)new BPMSwitchYardScanner().scan(RulesFiredProcess.class).getImplementation();
        BPMExchangeHandler handler = new BPMExchangeHandler(bci_model, serviceDomain, serviceName);
        serviceDomain.registerService(serviceName, new InOnlyService("process"), handler);
        handler.start();
        Exchange exchange = serviceRef.createExchange();
        Message message = exchange.createMessage();
        message.setContent(holder);
        exchange.send(message);
        handler.stop();
        Assert.assertEquals("rules fired", holder.getValue());
    }

    public static final class Holder {
        private String _value;
        public String getValue() { return _value; }
        public void setValue(String value) { _value = value; }
        public String toString() { return _value; }
    }

}
