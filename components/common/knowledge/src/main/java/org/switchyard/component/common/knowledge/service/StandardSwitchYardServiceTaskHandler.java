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
package org.switchyard.component.common.knowledge.service;

import java.util.Map;

import javax.xml.namespace.QName;

import org.jbpm.process.workitem.bpmn2.ServiceTaskHandler;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessRuntime;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;

/**
 * StandardSwitchYardServiceTaskHandler.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public class StandardSwitchYardServiceTaskHandler extends SwitchYardServiceTaskHandler {

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
    /** operationImplementationRef. */
    public static final String OPERATION_IMPLEMENTATION_REF = "operationImplementationRef";

    /**
     * Constructs a new StandardSwitchYardServiceTaskHandler.
     */
    public StandardSwitchYardServiceTaskHandler() {}

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
            if (runtime instanceof KieSession) {
                sth = new ServiceTaskHandler((KieSession)runtime);
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
        return getQName(INTERFACE, parameters, getQName(INTERFACE_IMPLEMENTATION_REF, parameters, null));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getOperationName(Map<String, Object> parameters) {
        return getString(OPERATION, parameters, getString(OPERATION_IMPLEMENTATION_REF, parameters, null));
    }

}
