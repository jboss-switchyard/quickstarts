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
package org.switchyard.as7.extension.ws;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.jboss.security.SecurityContextAssociation;
import org.jboss.wsf.spi.deployment.Endpoint;
import org.jboss.wsf.stack.cxf.configuration.BusHolder;
import org.jboss.wsf.stack.cxf.configuration.NonSpringBusHolder;
import org.jboss.wsf.stack.cxf.deployment.EndpointImpl;
import org.jboss.wsf.stack.cxf.security.authentication.SubjectCreatingInterceptor;
import org.jboss.wsf.stack.cxf.security.authentication.SubjectCreatingPolicyInterceptor;
import org.switchyard.common.type.reflect.Construction;
import org.switchyard.common.type.reflect.FieldAccess;
import org.switchyard.component.soap.config.model.InterceptorModel;
import org.switchyard.component.soap.config.model.InterceptorsModel;
import org.switchyard.component.soap.config.model.SOAPBindingModel;
import org.switchyard.config.model.property.PropertiesModel;
import org.switchyard.SwitchYardException;

/**
 * Interceptor functions.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public final class Interceptors {

    private static final Class<?>[][] PARAMETER_TYPES = new Class<?>[][]{
        new Class<?>[]{Map.class},
        new Class<?>[0]
    };

    /**
     * Adds any binding model-configured inInterceptors and outInterceptors to the endpoint.
     * @param endpoint the endpoint
     * @param bindingModel the binding model
     * @param loader the classloader to use
     */
    public static void addInterceptors(Endpoint endpoint, SOAPBindingModel bindingModel, ClassLoader loader) {
        BusHolder busHolder = endpoint.getService().getDeployment().getAttachment(BusHolder.class);
        if (busHolder instanceof NonSpringBusHolder) {
            List<?> list = new FieldAccess<List<?>>(NonSpringBusHolder.class, "endpoints").read(busHolder);
            for (Object o : list) {
                for (org.apache.cxf.endpoint.Endpoint e : ((EndpointImpl)o).getService().getEndpoints().values()) {
                    e.getInInterceptors().addAll(getConfiguredInInterceptors(bindingModel, loader));
                    e.getOutInterceptors().addAll(getConfiguredOutInterceptors(bindingModel, loader));
                    e.getOutInterceptors().add(new ClearSecurityContextOutInterceptor());
                    e.getOutFaultInterceptors().add(new ClearSecurityContextOutFaultInterceptor());
                }
            }
        }
    }

    /**
     * Gets any binding model-configured inInterceptors.
     * @param <T> the type of Interceptor
     * @param bindingModel the binding model
     * @param loader the classloader to use
     * @return the inInterceptors
     */
    public static <T extends Interceptor<? extends Message>> List<T> getConfiguredInInterceptors(SOAPBindingModel bindingModel, ClassLoader loader) {
        if (bindingModel != null) {
            return getConfiguredInterceptors(bindingModel.getInInterceptors(), loader);
        }
        return null;
    }

    /**
     * Gets any binding model-configured outInterceptors.
     * @param <T> the type of Interceptor
     * @param bindingModel the binding model
     * @param loader the classloader to use
     * @return the outInterceptors
     */
    public static <T extends Interceptor<? extends Message>> List<T> getConfiguredOutInterceptors(SOAPBindingModel bindingModel, ClassLoader loader) {
        if (bindingModel != null) {
            return getConfiguredInterceptors(bindingModel.getOutInterceptors(), loader);
        }
        return null;
    }

    private static <T extends Interceptor<? extends Message>> List<T> getConfiguredInterceptors(InterceptorsModel interceptorsModel, ClassLoader loader) {
        List<T> interceptors = new ArrayList<T>();
        if (interceptorsModel != null) {
            for (InterceptorModel interceptorModel : interceptorsModel.getInterceptors()) {
                if (interceptorModel != null) {
                    @SuppressWarnings("unchecked")
                    Class<T> interceptorClass = (Class<T>)interceptorModel.getClazz(loader);
                    if (interceptorClass != null) {
                        PropertiesModel propertiesModel = interceptorModel.getProperties();
                        Map<String, String> properties = propertiesModel != null ? propertiesModel.toMap() : new HashMap<String, String>();
                        T interceptor = newInterceptor(interceptorClass, properties);
                        if (interceptor != null) {
                            if (interceptor instanceof SubjectCreatingInterceptor) {
                                ((SubjectCreatingInterceptor)interceptor).setPropagateContext(true);
                            } else if (interceptor instanceof SubjectCreatingPolicyInterceptor) {
                                ((SubjectCreatingPolicyInterceptor)interceptor).setPropagateContext(true);
                            }
                            interceptors.add(interceptor);
                        }
                    }
                }
            }
        }
        return interceptors;
    }

    private static <T extends Interceptor<? extends Message>> T newInterceptor(Class<T> interceptorClass, Map<String, String> properties) {
        T interceptor = null;
        Constructor<T> constructor = getConstructor(interceptorClass);
        Class<?>[] parameterTypes = constructor != null ? constructor.getParameterTypes() : new Class<?>[0];
        try {
            if (parameterTypes.length == 0) {
                interceptor = Construction.construct(interceptorClass);
            } else if (parameterTypes.length == 1) {
                interceptor = Construction.construct(interceptorClass, parameterTypes, new Object[]{properties});
            }
        } catch (Throwable t) {
            throw new SwitchYardException("Could not instantiate interceptor class: " + interceptorClass.getName(), t);
        }
        return interceptor;
    }

    private static <T extends Interceptor<? extends Message>> Constructor<T> getConstructor(Class<T> interceptorClass) {
        Constructor<T> constructor = null;
        for (Class<?>[] parameterTypes : PARAMETER_TYPES) {
            try {
                constructor = interceptorClass.getConstructor(parameterTypes);
                if (constructor != null) {
                    break;
                }
            } catch (Throwable t) {
                // keep checkstyle happy ("at least one statement")
                t.getMessage();
            }
        }
        return constructor;
    }

    private static class ClearSecurityContextInterceptor extends AbstractPhaseInterceptor<Message> {
        private ClearSecurityContextInterceptor() {
            super(Phase.POST_LOGICAL_ENDING);
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public void handleMessage(Message message) throws Fault {
            SecurityContextAssociation.clearSecurityContext();
        }
        /**
         * {@inheritDoc}
         */
        @Override
        public void handleFault(Message message) {
            SecurityContextAssociation.clearSecurityContext();
        }
    }
    // class simple names must be unique
    private static final class ClearSecurityContextOutInterceptor extends ClearSecurityContextInterceptor {}
    private static final class ClearSecurityContextOutFaultInterceptor extends ClearSecurityContextInterceptor {}

    private Interceptors() {}

}
