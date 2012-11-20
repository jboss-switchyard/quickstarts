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

import java.lang.reflect.Constructor;

import org.jbpm.process.workitem.wsht.AbstractHTWorkItemHandler;
import org.kie.runtime.KieRuntime;
import org.kie.runtime.KnowledgeRuntime;
import org.kie.runtime.process.ProcessRuntime;
import org.kie.runtime.process.WorkItemHandler;
import org.switchyard.ServiceDomain;
import org.switchyard.common.type.reflect.Construction;
import org.switchyard.component.bpm.config.model.BPMComponentImplementationModel;
import org.switchyard.component.bpm.config.model.WorkItemHandlerModel;
import org.switchyard.component.bpm.config.model.WorkItemHandlersModel;

/**
 * WorkItemHandler functions.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public final class WorkItemHandlers {

    private static final Class<?>[] WIH_CNSTR_ARG_CLASSES = new Class<?>[]{ProcessRuntime.class, KieRuntime.class, KnowledgeRuntime.class};
 
    /**
     * Registers work item handlers.
     * @param model the model
     * @param loader the class loader
     * @param runtime the process runtime
     * @param domain the service domain
     */
    public static void registerWorkItemHandlers(BPMComponentImplementationModel model, ClassLoader loader, ProcessRuntime runtime, ServiceDomain domain) {
        WorkItemHandlersModel workItemHandlersModel = model.getWorkItemHandlers();
        if (workItemHandlersModel != null) {
            String tns = model.getComponent().getTargetNamespace();
            for (WorkItemHandlerModel workItemHandlerModel : workItemHandlersModel.getWorkItemHandlers()) {
                Class<? extends WorkItemHandler> wih_class = workItemHandlerModel.getClazz(loader);
                String name = workItemHandlerModel.getName();
                Constructor<? extends WorkItemHandler> wih_cnstr = getConstructor(wih_class);
                if (wih_cnstr != null) {
                    WorkItemHandler wih = newWorkItemHandler(wih_class, runtime);
                    if (wih != null) {
                        if (wih instanceof SwitchYardWorkItemHandler) {
                            SwitchYardWorkItemHandler sywih = (SwitchYardWorkItemHandler)wih;
                            if (name != null) {
                                sywih.setName(name);
                            } else {
                                name = sywih.getName();
                            }
                            sywih.setTargetNamespace(tns);
                            sywih.setServiceDomain(domain);
                            sywih.setProcessRuntime(runtime);
                        }
                        if (name == null && wih instanceof AbstractHTWorkItemHandler) {
                            name = "Human Task";
                        }
                        if (name != null) {
                            runtime.getWorkItemManager().registerWorkItemHandler(name, wih);
                        }
                    }
                }
            }
        }
    }

    /**
     * Creates a new work item hander.
     * @param wihClass the class
     * @param runtime the process runtime
     * @return the work item handler
     */
    public static WorkItemHandler newWorkItemHandler(Class<? extends WorkItemHandler> wihClass, ProcessRuntime runtime) {
        WorkItemHandler wih = null;
        Constructor<? extends WorkItemHandler> wih_cnstr = getConstructor(wihClass);
        if (wih_cnstr != null) {
            Class<?>[] wih_param_types = wih_cnstr.getParameterTypes();
            if (wih_param_types.length == 0) {
                wih = Construction.construct(wihClass);
            } else if (wih_param_types.length == 1) {
                wih = Construction.construct(wihClass, wih_param_types, new Object[]{runtime});
            }
        }
        return wih;
    }

    private static Constructor<? extends WorkItemHandler> getConstructor(Class<? extends WorkItemHandler> wihClass) {
        Constructor<? extends WorkItemHandler> constructor = null;
        for (Class<?> wihCnstrArgClass : WIH_CNSTR_ARG_CLASSES) {
            try {
                constructor = wihClass.getDeclaredConstructor(wihCnstrArgClass);
            } catch (Throwable t) {
                constructor = null;
            }
        }
        if (constructor == null) {
            try {
                constructor = wihClass.getConstructor();
            } catch (Throwable t) {
                constructor = null;
            }
        }
        return constructor;
    }

    private WorkItemHandlers() {}

}
