/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved. 
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

import java.util.Map;

import javax.xml.namespace.QName;

import org.jbpm.process.workitem.bpmn2.ServiceTaskHandler;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.ProcessRuntime;
import org.kie.runtime.process.WorkItem;
import org.kie.runtime.process.WorkItemManager;

/**
 * SwitchYardServiceTaskHandler.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public class SwitchYardServiceTaskHandler extends SwitchYardServiceWorkItemHandler {

    /** Service Task. */
    public static final String SERVICE_TASK = "Service Task";

    /** implementation. */
    public static final String IMPLEMENTATION = "implementation";
    /** ##SwitchYard. */
    public static final String IMPLEMENTATION_SWITCHYARD = "##SwitchYard";

    /** Interface. */
    public static final String INTERFACE = "Interface";
    /** interfaceImplementationRef. */
    public static final String INTERFACE_IMPLEMENTATION_REF = "interfaceImplementationRef";
    /** Operation. */
    public static final String OPERATION = "Operation";
    /** operationImplementationRef. */
    public static final String OPERATION_IMPLEMENTATION_REF = "operationImplementationRef";
    /** Parameter. */
    public static final String PARAMETER = "Parameter";
    /** Result. */
    public static final String RESULT = "Result";

    /**
     * Constructs a new SwitchYardServiceTaskHandler with the name "Service Task".
     */
    public SwitchYardServiceTaskHandler() {
        setName(SERVICE_TASK);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        String implementation = getImplementation(workItem.getParameters());
        if (IMPLEMENTATION_SWITCHYARD.equalsIgnoreCase(implementation)) {
            super.executeWorkItem(workItem, manager);
        } else {
            ServiceTaskHandler sth;
            ProcessRuntime runtime = getProcessRuntime();
            if (runtime instanceof StatefulKnowledgeSession) {
                sth = new ServiceTaskHandler((StatefulKnowledgeSession)runtime);
            } else {
                sth = new ServiceTaskHandler();
            }
            sth.setClassLoader(getClass().getClassLoader());
            sth.executeWorkItem(workItem, manager);
        }
    }

    private String getImplementation(Map<String, Object> parameters) {
        return getString(IMPLEMENTATION, parameters, null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected QName getServiceName(Map<String, Object> parameters) {
        return getQName(INTERFACE_IMPLEMENTATION_REF, parameters, getQName(INTERFACE, parameters, null));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getServiceOperationName(Map<String, Object> parameters) {
        return getString(OPERATION_IMPLEMENTATION_REF, parameters, getString(OPERATION, parameters, null));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getContentInputName(Map<String, Object> parameters) {
        return PARAMETER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getContentOutputName(Map<String, Object> parameters) {
        return RESULT;
    }

}
