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

package org.switchyard.component.camel.processor;

import static org.switchyard.metadata.java.JavaService.OperationTypeQNames;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;

import org.apache.camel.Exchange;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.switchyard.common.type.Classes;
import org.switchyard.component.camel.CamelConstants;
import org.switchyard.component.common.composer.MessageComposer;
import org.switchyard.metadata.InOnlyOperation;
import org.switchyard.metadata.InOutOperation;
import org.switchyard.metadata.ServiceOperation;

/**
 * A cxfrs Camel Processor that uses HTTP client api. The HTTP method is set directly
 * from SwitchYard exchange. This is useful for urls of the form
 *
 * cxfrs://<host>:<port>/[<context-path>]?rescourceClasses=...
 *
 * The resourceClasses parameter will be used to determine the apporiate path, method and response type.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class CxfRsHttpDynamicProcessor extends DefaultProcessor {

    private Map<String, RsMethod> _resourcePaths = new HashMap<String, RsMethod>();

    /**
     * Create a CxfRsHttpDynamicProcessor that handles cxfrs http outbound routes that are dynamic.
     *
     * @param composer the message composer to be used
     * @param exchange the switchayrd exchange
     * @param uri the camel cxfrs endpoint uri
     */
    public CxfRsHttpDynamicProcessor(MessageComposer composer, org.switchyard.Exchange exchange, String uri) {
        super(composer, exchange);
        String resourceClasses = uri.split(CamelConstants.RESOURCE_CLASSES)[1].split("&")[0];
        StringTokenizer st = new StringTokenizer(resourceClasses);
        while (st.hasMoreTokens()) {
            String className = st.nextToken();
            Class<?> clazz = Classes.forName(className);
            if (clazz == null) {
                throw new RuntimeException("Unable to load class " + className);
            }
            String classLevelPath = "";
            Path path = clazz.getAnnotation(Path.class);
            if (path != null) {
                classLevelPath = path.value();
            }
            for (Method m : clazz.getDeclaredMethods()) {
                // We only consider public methods
                if (Modifier.isPublic(m.getModifiers())) {
                    // At this point, we only accept methods with a single 
                    // parameter which maps to the input message
                    Class<?>[] params = m.getParameterTypes();
                    if (params.length > 1) {
                        throw new RuntimeException("Service operations on a REST interface must have none or exactly one parameter. " + m.getName());
                    }

                    // Create the appropriate service operation
                    OperationTypeQNames operationTypeNames = new OperationTypeQNames(m);
                    ServiceOperation op = null;
                    if (m.getReturnType().equals(Void.TYPE)) {
                        op = new InOnlyOperation(m.getName(), operationTypeNames.in());
                    } else {
                        op = new InOutOperation(m.getName(), operationTypeNames.in(), operationTypeNames.out(), operationTypeNames.fault());
                    }
                    String methodPath = classLevelPath;
                    String paramName = null;
                    path = m.getAnnotation(Path.class);
                    Boolean foundPathParam = false;
                    if (path != null) {
                        Annotation[][] paramAnnotations= m.getParameterAnnotations();
                        if (paramAnnotations.length > 0
                            && paramAnnotations[0].length > 0) {
                            for (Annotation anno : paramAnnotations[0]) {
                                if (anno instanceof PathParam) {
                                    paramName = ((PathParam)anno).value();
                                    foundPathParam = true;
                                    break;
                                }
                            }
                        }
                        if (foundPathParam) {
                            methodPath = methodPath + path.value().substring(0, path.value().indexOf("{"));
                        } else {
                            methodPath = methodPath + path.value();
                        }
                    }
                    if (m.getAnnotation(DELETE.class) != null) {
                        _resourcePaths.put(op.toString(), new RsMethod(methodPath, CamelConstants.HTTP_DELETE_METHOD, paramName, m.getReturnType(), foundPathParam));
                    } else if (m.getAnnotation(PUT.class) != null) {
                        _resourcePaths.put(op.toString(), new RsMethod(methodPath, CamelConstants.HTTP_PUT_METHOD, paramName, m.getReturnType(), foundPathParam));
                    } else if (m.getAnnotation(POST.class) != null) {
                        _resourcePaths.put(op.toString(), new RsMethod(methodPath, CamelConstants.HTTP_POST_METHOD, paramName, m.getReturnType(), foundPathParam));
                    } else if (m.getAnnotation(OPTIONS.class) != null) {
                        _resourcePaths.put(op.toString(), new RsMethod(methodPath, CamelConstants.HTTP_OPTIONS_METHOD, paramName, m.getReturnType(), foundPathParam));
                    } else if (m.getAnnotation(HEAD.class) != null) {
                        _resourcePaths.put(op.toString(), new RsMethod(methodPath, CamelConstants.HTTP_HEAD_METHOD, paramName, m.getReturnType(), foundPathParam));
                    } else if (m.getAnnotation(GET.class) != null) {
                        _resourcePaths.put(op.toString(), new RsMethod(methodPath, CamelConstants.HTTP_GET_METHOD, paramName, m.getReturnType(), foundPathParam));
                    } else {
                        throw new RuntimeException("Encountered unknown REST method type.");
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(Exchange camelExchange) throws Exception {
        final RsMethod restMethod = _resourcePaths.get(getExchange().getContract().getServiceOperation().toString());
        if (restMethod == null) {
            throw new RuntimeException("Could not map " + getExchange().getContract().getServiceOperation().getName() + " to any REST method.");
        }
        camelExchange.getIn().setHeader(CxfConstants.CAMEL_CXF_RS_USING_HTTP_API, Boolean.TRUE);
        camelExchange.getIn().setHeader(CxfConstants.CAMEL_CXF_RS_RESPONSE_CLASS, restMethod.getResponseType());
        camelExchange.getIn().setHeader(Exchange.HTTP_METHOD, restMethod.getMethod());
        if (restMethod.hasPathParam()) {
            camelExchange.getIn().setHeader(Exchange.HTTP_PATH, restMethod.getPath() + "/" + getExchange().getMessage().getContent());
        } else {
            camelExchange.getIn().setHeader(Exchange.HTTP_PATH, restMethod.getPath());
        }
        super.process(camelExchange);
    }

    /**
     * Represents the REST method's tuple {http_path, http_method, param_name, response type}.
     */
    private class RsMethod {
        private String _path;
        private String _method;
        private String _paramName;
        private Class<?> _responseType;
        private Boolean _pathParam;

        public RsMethod(String path, String method, String paramName, Class<?> responseType, Boolean hasPathParam) {
            _path = path;
            _method = method;
            _paramName = paramName;
            _responseType = responseType;
            _pathParam = hasPathParam;
        }

        /**
         * Return the HTTP method for the REST method.
         *
         * @return the HTTP method
         */
        public String getMethod() {
            return _method;
        }

        /**
         * Return the HTTP path for the REST method.
         *
         * @return the HTTP path
         */
        public String getPath() {
            return _path;
        }

        /**
         * Return the method's parameter name.
         *
         * @return the method's parameter name
         */
        public String getParamName() {
            return _paramName;
        }

        /**
         * Return the method's response type.
         *
         * @return the method's response type
         */
        public Class<?> getResponseType() {
            return _responseType;
        }

        /**
         * Return true if this method has a path param.
         *
         * @return true if path param exists
         */
        public Boolean hasPathParam() {
            return _pathParam;
        }
    }
}
