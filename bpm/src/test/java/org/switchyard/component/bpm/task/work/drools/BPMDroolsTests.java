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
package org.switchyard.component.bpm.task.work.drools;

import static org.switchyard.Scope.EXCHANGE;
import static org.switchyard.component.bpm.ProcessActionType.SIGNAL_EVENT;
import static org.switchyard.component.bpm.ProcessActionType.START_PROCESS;
import static org.switchyard.component.bpm.ProcessConstants.ACTION_TYPE_VAR;
import static org.switchyard.component.bpm.ProcessConstants.PROCESS_EVENT_TYPE_VAR;
import static org.switchyard.component.bpm.ProcessConstants.PROCESS_INSTANCE_ID_VAR;

import javax.xml.namespace.QName;

import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.switchyard.BaseHandler;
import org.switchyard.Context;
import org.switchyard.Exchange;
import org.switchyard.HandlerException;
import org.switchyard.Property;
import org.switchyard.ServiceDomain;
import org.switchyard.ServiceReference;
import org.switchyard.common.io.resource.SimpleResource;
import org.switchyard.component.bpm.config.model.BPMComponentImplementationModel;
import org.switchyard.component.bpm.config.model.v1.V1BPMComponentImplementationModel;
import org.switchyard.component.bpm.config.model.v1.V1TaskHandlerModel;
import org.switchyard.component.bpm.exchange.BPMExchangeHandler;
import org.switchyard.component.bpm.exchange.BPMExchangeHandlerFactory;
import org.switchyard.component.bpm.task.work.SwitchYardServiceTaskHandler;
import org.switchyard.metadata.InOnlyService;
import org.switchyard.test.SwitchYardRunner;

/**
 * Tests the Drools BPM implementation.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
@RunWith(SwitchYardRunner.class)
public class BPMDroolsTests {

    private static final String TEST_CALL_SERVICE = "org/switchyard/component/bpm/drools/BPMDroolsTests-CallService.bpmn";
    private static final String TEST_CONTROL_PROCESS = "org/switchyard/component/bpm/drools/BPMDroolsTests-ControlProcess.bpmn";
    private static final String TEST_REUSE_HANDLER = "org/switchyard/component/bpm/drools/BPMDroolsTests-ReuseHandler.bpmn";

    private ServiceDomain serviceDomain;

    @Test
    public void testCallService() throws Exception {
        final Holder holder = new Holder();
        serviceDomain.registerService(new QName("CallService"), new InOnlyService(), new BaseHandler(){
            public void handleMessage(Exchange exchange) throws HandlerException {
                holder.value = "message handled";
            }
        });
        serviceDomain.registerServiceReference(new QName("CallService"), new InOnlyService());
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource(TEST_CALL_SERVICE), ResourceType.BPMN2);
        KnowledgeBase kbase = kbuilder.newKnowledgeBase();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        SwitchYardServiceTaskHandler ssth = new SwitchYardServiceTaskHandler();
        ssth.setServiceDomain(serviceDomain);
        ksession.getWorkItemManager().registerWorkItemHandler(ssth.getName(), new DroolsWorkItemHandler(ssth, new DroolsTaskManager(ksession)));
        ksession.startProcess("CallService");
        ksession.halt();
        ksession.dispose();
        Assert.assertEquals("message handled", holder.value);
    }

    @Test
    public void testControlProcess() throws Exception {
        final Holder holder = new Holder();
        serviceDomain.registerService(new QName("CallService"), new InOnlyService(), new BaseHandler(){
            public void handleMessage(Exchange exchange) throws HandlerException {
                holder.value = "message handled";
            }
        });
        serviceDomain.registerServiceReference(new QName("CallService"), new InOnlyService());
        QName qname = new QName("ControlProcess");
        BPMExchangeHandler handler = BPMExchangeHandlerFactory.instance().newBPMExchangeHandler(serviceDomain);
        serviceDomain.registerService(qname, new InOnlyService(), handler);
        ServiceReference serviceRef = serviceDomain.registerServiceReference(qname, new InOnlyService());
        BPMComponentImplementationModel bci_model = new V1BPMComponentImplementationModel();
        bci_model.setProcessDefinition(new SimpleResource(TEST_CONTROL_PROCESS, "BPMN2"));
        bci_model.setProcessId(qname.getLocalPart());
        bci_model.setPersistent(false);
        bci_model.addTaskHandler(new V1TaskHandlerModel().setClazz(SwitchYardServiceTaskHandler.class).setName(SwitchYardServiceTaskHandler.SWITCHYARD_SERVICE));
        handler.init(qname, bci_model);
        handler.start();
        Exchange exchange = serviceRef.createExchange();
        Context context = exchange.getContext();
        context.setProperty(ACTION_TYPE_VAR, START_PROCESS, EXCHANGE);
        exchange.send(exchange.createMessage());
        Property property = context.getProperty(PROCESS_INSTANCE_ID_VAR, EXCHANGE);
        Long processInstanceId = property != null ? (Long)property.getValue() : null;
        exchange = serviceRef.createExchange();
        context = exchange.getContext();
        context.setProperty(ACTION_TYPE_VAR, SIGNAL_EVENT, EXCHANGE);
        context.setProperty(PROCESS_EVENT_TYPE_VAR, "test", EXCHANGE);
        context.setProperty(PROCESS_INSTANCE_ID_VAR, processInstanceId, EXCHANGE);
        exchange.send(exchange.createMessage());
        handler.stop();
        handler.destroy();
        Assert.assertEquals("message handled", holder.value);
    }

    @Test
    public void testReuseHandler() throws Exception {
        QName qname = new QName("ReuseHandler");
        BPMExchangeHandler handler = BPMExchangeHandlerFactory.instance().newBPMExchangeHandler(serviceDomain);
        serviceDomain.registerService(qname, new InOnlyService(), handler);
        ServiceReference serviceRef = serviceDomain.registerServiceReference(qname, new InOnlyService());
        BPMComponentImplementationModel bci_model = new V1BPMComponentImplementationModel();
        bci_model.setProcessDefinition(new SimpleResource(TEST_REUSE_HANDLER, "BPMN2"));
        bci_model.setProcessId(qname.getLocalPart());
        bci_model.addTaskHandler(new V1TaskHandlerModel().setClazz(ReuseHandler.class).setName(qname.getLocalPart()));
        handler.init(qname, bci_model);
        handler.start();
        Exchange exchange = serviceRef.createExchange();
        Context context = exchange.getContext();
        context.setProperty(ACTION_TYPE_VAR, START_PROCESS, EXCHANGE);
        exchange.send(exchange.createMessage());
        Assert.assertEquals("handler executed", ReuseHandler._holder.value);
        ReuseHandler._holder.value = null;
    }

    static final class Holder {
        public Object value;
    }

}
