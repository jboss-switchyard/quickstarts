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
package org.switchyard.as7.extension.ws;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.message.Message;
import org.apache.log4j.Logger;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.jboss.wsf.spi.deployment.Endpoint;
import org.jboss.wsf.stack.cxf.configuration.BusHolder;
import org.jboss.wsf.stack.cxf.configuration.NonSpringBusHolder;
import org.jboss.wsf.stack.cxf.deployment.EndpointImpl;
import org.jboss.wsf.stack.cxf.security.authentication.SubjectCreatingInterceptor;
import org.switchyard.common.lang.Strings;
import org.switchyard.common.type.reflect.Construction;
import org.switchyard.common.type.reflect.FieldAccess;
import org.switchyard.component.soap.config.model.InterceptorModel;
import org.switchyard.component.soap.config.model.InterceptorsModel;
import org.switchyard.component.soap.config.model.SOAPBindingModel;

/**
 * Interceptor functions.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public final class Interceptors {

    private static final Logger LOGGER = Logger.getLogger(Interceptors.class);

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
                    e.getInInterceptors().addAll(getSecurityInInterceptors(bindingModel));
                    e.getOutInterceptors().addAll(getConfiguredOutInterceptors(bindingModel, loader));
                }
            }
        }
    }

    private static List<Interceptor<? extends Message>> getSecurityInInterceptors(SOAPBindingModel bindingModel) {
        List<Interceptor<? extends Message>> interceptors = new ArrayList<Interceptor<? extends Message>>();
        /*
        boolean addSecurity = false;
        Model policyModel = null;
        if (bindingModel.isServiceBinding()) {
            policyModel = bindingModel.getService().getComponentService();
        } else if (bindingModel.isReferenceBinding()) {
            policyModel = bindingModel.getReference().getComponentReference();
        }
        if (policyModel != null) {
            Set<String> requires = PolicyConfig.getRequires(policyModel);
            for (SecurityPolicy securityPolicy : SecurityPolicy.values()) {
                if (requires.contains(securityPolicy.getName())) {
                    addSecurity = true;
                    break;
                }
            }
        }
        if (addSecurity) {
        */
        String securityAction = Strings.trimToNull(bindingModel.getSecurityAction());
        if (securityAction != null) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Adding WS-Security SubjectCreatingInterceptor with binding.soap:securityAction: " + securityAction);
            }
            Map<String, Object> sciProps = new HashMap<String, Object>();
            //sciProps.put(WSHandlerConstants.ACTION, WSHandlerConstants.USERNAME_TOKEN);
            sciProps.put(WSHandlerConstants.ACTION, securityAction);
            SubjectCreatingInterceptor sci = new SubjectCreatingInterceptor(sciProps);
            sci.setPropagateContext(true);
            interceptors.add(sci);
            interceptors.add(new ClearSecurityContextInterceptor());
        } else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("binding.soap:securityAction undefined; not adding WS-Security SubjectCreatingInterceptor");
            }
        }
        //}
        return interceptors;
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
                        T interceptor = Construction.construct(interceptorClass);
                        interceptors.add(interceptor);
                    }
                }
            }
        }
        return interceptors;
    }

    private Interceptors() {}

}
