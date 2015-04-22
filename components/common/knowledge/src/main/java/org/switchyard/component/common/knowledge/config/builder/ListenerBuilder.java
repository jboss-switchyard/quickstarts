/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.switchyard.component.common.knowledge.config.builder;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

import org.kie.api.event.KieRuntimeEventManager;
import org.kie.api.runtime.KieRuntime;
import org.switchyard.common.type.reflect.Construction;
import org.switchyard.component.common.knowledge.CommonKnowledgeLogger;
import org.switchyard.component.common.knowledge.CommonKnowledgeMessages;
import org.switchyard.component.common.knowledge.config.model.KnowledgeComponentImplementationModel;
import org.switchyard.component.common.knowledge.config.model.ListenerModel;
import org.switchyard.component.common.knowledge.config.model.ListenersModel;

/**
 * ListenerBuilder.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
public class ListenerBuilder extends KnowledgeBuilder {

    private static final Class<?>[][] PARMAMETER_TYPES = new Class<?>[][]{
        new Class<?>[]{KieRuntimeEventManager.class},
        new Class<?>[]{KieRuntime.class},
        new Class<?>[0]
    };

    private Class<? extends EventListener> _listenerClass;
    private boolean _automaticRegistration;

    /**
     * Creates a new ListenerBuilder.
     * @param classLoader classLoader
     * @param listenerModel listenerModel
     */
    @SuppressWarnings("unchecked")
    public ListenerBuilder(ClassLoader classLoader, ListenerModel listenerModel) {
        super(classLoader);
        if (listenerModel != null) {
            _listenerClass = (Class<? extends EventListener>)listenerModel.getClazz(getClassLoader());
            if (_listenerClass == null) {
                throw CommonKnowledgeMessages.MESSAGES.couldNotLoadListenerClass(listenerModel.getModelConfiguration().getAttribute("class"));
            }
        }
    }

    /**
     * If automatic registration was performed.
     * <br/><br/>
     * <i><b>Note:</b> This method only returns a reliable result after {@link #build()} has been called.</i>
     * @return if automatic registration has been performed
     */
    public boolean wasAutomaticRegistration() {
        return _automaticRegistration;
    }

    /**
     * Builds an EventListener.
     * @param runtimeEventManager runtimeEventManager
     * @return an EventListener
     */
    public EventListener build(KieRuntimeEventManager runtimeEventManager) {
       return construct(runtimeEventManager);
    }

    private EventListener construct(KieRuntimeEventManager runtimeEventManager) {
        EventListener listener = null;
        if (_listenerClass != null) {
            Constructor<? extends EventListener> constructor = null;
            for (Class<?>[] parameterTypes : PARMAMETER_TYPES) {
                try {
                    constructor = _listenerClass.getConstructor(parameterTypes);
                    if (constructor != null) {
                        break;
                    }
                } catch (Throwable t) {
                    // keep checkstyle happy ("at least one statement")
                    t.getMessage();
                }
            }
            Class<?>[] parameterTypes = constructor != null ? constructor.getParameterTypes() : new Class<?>[0];
            try {
                if (parameterTypes.length == 0) {
                    listener = Construction.construct(_listenerClass);
                    // manual registration
                    _automaticRegistration = false;
                } else if (parameterTypes.length == 1) {
                    // automatic registration
                    listener = Construction.construct(_listenerClass, parameterTypes, new Object[]{runtimeEventManager});
                    if (listener == null) {
                        CommonKnowledgeLogger.ROOT_LOGGER.attemptToRegisterListenerReturnedNull();
                    }
                    _automaticRegistration = true;
                }
            } catch (Throwable t) {
                throw CommonKnowledgeMessages.MESSAGES.couldNotInstantiateListenerClass(_listenerClass.getName());
            }
        }
        return listener;
    }

    /**
     * Creates ListenerBuilders. 
     * @param classLoader classLoader
     * @param implementationModel implementationModel
     * @return ListenerBuilders
     */
    public static List<ListenerBuilder> builders(ClassLoader classLoader, KnowledgeComponentImplementationModel implementationModel) {
        List<ListenerBuilder> builders = new ArrayList<ListenerBuilder>();
        if (implementationModel != null) {
            ListenersModel listenersModel = implementationModel.getListeners();
            if (listenersModel != null) {
                for (ListenerModel listenerModel : listenersModel.getListeners()) {
                    if (listenerModel != null) {
                        builders.add(new ListenerBuilder(classLoader, listenerModel));
                    }
                }
            }
        }
        return builders;
    }

}
