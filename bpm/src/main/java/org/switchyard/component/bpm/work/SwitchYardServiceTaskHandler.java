/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.component.bpm.work;

import org.jbpm.process.workitem.bpmn2.ServiceTaskHandler;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.ProcessRuntime;
import org.kie.runtime.process.WorkItem;
import org.kie.runtime.process.WorkItemHandler;
import org.kie.runtime.process.WorkItemManager;

/**
 * A WorkItemHandler that wraps a {@link SwitchYardServiceWorkItemHandler} and a {@link ServiceTaskHandler}, for the purpose of implementing a BPMN2 serviceTask.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class SwitchYardServiceTaskHandler extends BaseSwitchYardWorkItemHandler {

    /** Service Task. */
    public static final String SERVICE_TASK = "Service Task";

    /** implementation. */
    public static final String IMPLEMENTATION = "implementation";
    /** ##SwitchYard. */
    public static final String IMPLEMENTATION_SWITCHYARD = "##SwitchYard";
    /** ##unspecified. */
    public static final String IMPLEMENTATION_UNSPECIFIED = "##unspecified";

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
        WorkItemHandler wrapped;
        String implementation = (String)workItem.getParameter(IMPLEMENTATION);
        Object serviceName = workItem.getParameter(SwitchYardServiceWorkItemHandler.SERVICE_NAME);
        if (IMPLEMENTATION_SWITCHYARD.equalsIgnoreCase(implementation) || (IMPLEMENTATION_UNSPECIFIED.equalsIgnoreCase(implementation) && serviceName != null)) {
            SwitchYardServiceWorkItemHandler syswih = new SwitchYardServiceWorkItemHandler();
            syswih.setTargetNamespace(getTargetNamespace());
            syswih.setServiceDomain(getServiceDomain());
            syswih.setProcessRuntime(getProcessRuntime());
            wrapped = syswih;
        } else {
            ServiceTaskHandler sth;
            ProcessRuntime runtime = getProcessRuntime();
            if (runtime instanceof StatefulKnowledgeSession) {
                sth = new ServiceTaskHandler((StatefulKnowledgeSession)runtime);
            } else {
                sth = new ServiceTaskHandler();
            }
            sth.setClassLoader(getClass().getClassLoader());
            wrapped = sth;
        }
        wrapped.executeWorkItem(workItem, manager);
    }

}
