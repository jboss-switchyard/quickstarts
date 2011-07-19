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
package org.switchyard.component.bpm.drools;

import static org.switchyard.Scope.IN;
import static org.switchyard.Scope.OUT;
import static org.switchyard.common.io.resource.ResourceType.BPMN2;
import static org.switchyard.component.bpm.common.ProcessActionType.SIGNAL_EVENT;
import static org.switchyard.component.bpm.common.ProcessActionType.START_PROCESS;
import static org.switchyard.component.bpm.common.ProcessConstants.PROCESS_ACTION_TYPE_VAR;
import static org.switchyard.component.bpm.common.ProcessConstants.PROCESS_EVENT_TYPE_VAR;
import static org.switchyard.component.bpm.common.ProcessConstants.PROCESS_INSTANCE_ID_VAR;
import static org.switchyard.metadata.ExchangeContract.IN_ONLY;

import javax.xml.namespace.QName;

import org.drools.KnowledgeBase;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Assert;
import org.junit.Test;
import org.switchyard.BaseHandler;
import org.switchyard.Context;
import org.switchyard.Exchange;
import org.switchyard.HandlerException;
import org.switchyard.Property;
import org.switchyard.ServiceDomain;
import org.switchyard.ServiceReference;
import org.switchyard.common.io.resource.SimpleResource;
import org.switchyard.component.bpm.config.model.BpmComponentImplementationModel;
import org.switchyard.component.bpm.config.model.v1.V1BpmComponentImplementationModel;
import org.switchyard.component.bpm.config.model.v1.V1TaskHandlerModel;
import org.switchyard.component.bpm.exchange.BpmExchangeHandler;
import org.switchyard.component.bpm.exchange.BpmExchangeHandlerFactory;
import org.switchyard.component.bpm.task.SwitchYardServiceTaskHandler;
import org.switchyard.component.bpm.task.drools.DroolsWorkItemHandler;
import org.switchyard.test.SwitchYardTestCase;

/**
 * Tests the Drools BPM implementation.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class BpmDroolsTests extends SwitchYardTestCase {

    private static final String TEST_CALL_SERVICE = "org/switchyard/component/bpm/drools/BpmDroolsTests-CallService.bpmn";
    private static final String TEST_CONTROL_PROCESS = "org/switchyard/component/bpm/drools/BpmDroolsTests-ControlProcess.bpmn";
    private static final String TEST_REUSE_HANDLER = "org/switchyard/component/bpm/drools/BpmDroolsTests-ReuseHandler.bpmn";

    @Test
    public void testCallService() throws Exception {
        final Holder holder = new Holder();
        ServiceDomain domain = getServiceDomain();
        domain.registerService(new QName("CallService"), new BaseHandler(){
            public void handleMessage(Exchange exchange) throws HandlerException {
                holder.value = "message handled";
            }
        });
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource(TEST_CALL_SERVICE), ResourceType.BPMN2);
        KnowledgeBase kbase = kbuilder.newKnowledgeBase();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        SwitchYardServiceTaskHandler stih = new SwitchYardServiceTaskHandler();
        stih.setServiceDomain(getServiceDomain());
        ksession.getWorkItemManager().registerWorkItemHandler(stih.getName(), new DroolsWorkItemHandler(ksession, stih));
        ksession.startProcess("CallService");
        ksession.halt();
        ksession.dispose();
        Assert.assertEquals("message handled", holder.value);
    }

    @Test
    public void testControlProcess() throws Exception {
        final Holder holder = new Holder();
        ServiceDomain serviceDomain = getServiceDomain();
        serviceDomain.registerService(new QName("CallService"), new BaseHandler(){
            public void handleMessage(Exchange exchange) throws HandlerException {
                holder.value = "message handled";
            }
        });
        QName qname = new QName("ControlProcess");
        BpmExchangeHandler handler = BpmExchangeHandlerFactory.instance().newBpmExchangeHandler(serviceDomain);
        ServiceReference serviceRef = serviceDomain.registerService(qname, handler);
        BpmComponentImplementationModel bci_model = new V1BpmComponentImplementationModel();
        bci_model.setProcessDefinition(new SimpleResource(TEST_CONTROL_PROCESS, BPMN2));
        bci_model.setProcessId(qname.getLocalPart());
        bci_model.addTaskHandler(new V1TaskHandlerModel().setClazz(SwitchYardServiceTaskHandler.class).setName(SwitchYardServiceTaskHandler.SWITCHYARD_SERVICE));
        handler.init(qname, bci_model);
        handler.start(serviceRef);
        Exchange exchange = serviceRef.createExchange(IN_ONLY);
        Context context = exchange.getContext();
        context.setProperty(PROCESS_ACTION_TYPE_VAR, START_PROCESS, IN);
        exchange.send(exchange.createMessage());
        Property property = context.getProperty(PROCESS_INSTANCE_ID_VAR, OUT);
        Long processInstanceId = property != null ? (Long)property.getValue() : null;
        exchange = serviceRef.createExchange(IN_ONLY);
        context = exchange.getContext();
        context.setProperty(PROCESS_ACTION_TYPE_VAR, SIGNAL_EVENT, IN);
        context.setProperty(PROCESS_EVENT_TYPE_VAR, "test", IN);
        context.setProperty(PROCESS_INSTANCE_ID_VAR, processInstanceId, IN);
        exchange.send(exchange.createMessage());
        handler.stop(serviceRef);
        handler.destroy(serviceRef);
        Assert.assertEquals("message handled", holder.value);
    }

    @Test
    public void testReuseHandler() throws Exception {
        ServiceDomain serviceDomain = getServiceDomain();
        QName qname = new QName("ReuseHandler");
        BpmExchangeHandler handler = BpmExchangeHandlerFactory.instance().newBpmExchangeHandler(serviceDomain);
        ServiceReference serviceRef = serviceDomain.registerService(qname, handler);
        BpmComponentImplementationModel bci_model = new V1BpmComponentImplementationModel();
        bci_model.setProcessDefinition(new SimpleResource(TEST_REUSE_HANDLER, BPMN2));
        bci_model.setProcessId(qname.getLocalPart());
        bci_model.addTaskHandler(new V1TaskHandlerModel().setClazz(ReuseHandler.class).setName(qname.getLocalPart()));
        handler.init(qname, bci_model);
        handler.start(serviceRef);
        Exchange exchange = serviceRef.createExchange(IN_ONLY);
        Context context = exchange.getContext();
        context.setProperty(PROCESS_ACTION_TYPE_VAR, START_PROCESS, IN);
        exchange.send(exchange.createMessage());
        Assert.assertEquals("handler executed", ReuseHandler._holder.value);
        ReuseHandler._holder.value = null;
    }

    static final class Holder {
        public Object value;
    }

}
