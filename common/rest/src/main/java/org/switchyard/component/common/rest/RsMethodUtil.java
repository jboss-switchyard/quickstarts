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

package org.switchyard.component.common.rest;

import static org.switchyard.metadata.java.JavaService.OperationTypeQNames;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.PUT;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.switchyard.Exchange;
import org.switchyard.common.type.Classes;
import org.switchyard.metadata.InOnlyOperation;
import org.switchyard.metadata.InOutOperation;
import org.switchyard.metadata.ServiceOperation;

/**
 * Creates the REST method's tuple from a Java Class/Interface.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public final class RsMethodUtil {

    /**
     * HTTP GET Method.
     */
    public static final String HTTP_GET_METHOD = "GET";

    /**
     * HTTP POST Method.
     */
    public static final String HTTP_POST_METHOD = "POST";

    /**
     * HTTP PUT Method.
     */
    public static final String HTTP_PUT_METHOD = "PUT";

    /**
     * HTTP DELETE Method.
     */
    public static final String HTTP_DELETE_METHOD = "DELETE";

    /**
     * HTTP HEAD Method.
     */
    public static final String HTTP_HEAD_METHOD = "HEAD";

    /**
     * HTTP OPTIONS Method.
     */
    public static final String HTTP_OPTIONS_METHOD = "OPTIONS";

    private RsMethodUtil() {
    }

    /**
     * Create a list of RsMethod tuples from resource classes.
     *
     * @param resourceClasses a comma separated list of resource classes/interfaces
     * @return a Map of REST method associtaed with the SwitchYard service Operation
     */
    public static Map<String, RsMethod> parseResources(String resourceClasses) {
        Map<String, RsMethod> resourcePaths = new HashMap<String, RsMethod>();
        StringTokenizer st = new StringTokenizer(resourceClasses, ",");
        while (st.hasMoreTokens()) {
            String className = st.nextToken().trim();
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
                    RsMethod method = new RsMethod(clazz);

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
                    Boolean foundQueryParam = false;
                    Boolean foundMatrixParam = false;
                    if (path != null) {
                        Annotation[][] paramAnnotations= m.getParameterAnnotations();
                        if (paramAnnotations.length > 0
                            && paramAnnotations[0].length > 0) {
                            for (Annotation anno : paramAnnotations[0]) {
                                if (anno instanceof PathParam) {
                                    paramName = ((PathParam)anno).value();
                                    foundPathParam = true;
                                    break;
                                } else if (anno instanceof QueryParam) {
                                    paramName = ((QueryParam)anno).value();
                                    foundQueryParam = true;
                                    break;
                                } else if (anno instanceof MatrixParam) {
                                    paramName = ((MatrixParam)anno).value();
                                    foundMatrixParam = true;
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
                    Class<?> paramType = null;
                    if ((m.getParameterTypes() != null) && (m.getParameterTypes().length > 0)) {
                        paramType = m.getParameterTypes()[0];
                    }
                    method.setPath(methodPath);
                    method.setParamName(paramName);
                    method.setRequestType(paramType);
                    method.setResponseType(m.getReturnType());
                    method.setPathParam(foundPathParam);
                    method.setQueryParam(foundQueryParam);
                    method.setMatrixParam(foundMatrixParam);
                    if (m.getAnnotation(DELETE.class) != null) {
                        method.setMethod(HTTP_DELETE_METHOD);
                    } else if (m.getAnnotation(GET.class) != null) {
                        method.setMethod(HTTP_GET_METHOD);
                    } else if (m.getAnnotation(HEAD.class) != null) {
                        method.setMethod(HTTP_HEAD_METHOD);
                    } else if (m.getAnnotation(OPTIONS.class) != null) {
                        method.setMethod(HTTP_OPTIONS_METHOD);
                    } else if (m.getAnnotation(POST.class) != null) {
                        method.setMethod(HTTP_POST_METHOD);
                    } else if (m.getAnnotation(PUT.class) != null) {
                        method.setMethod(HTTP_PUT_METHOD);
                    } else {
                        throw new RuntimeException("Encountered unknown REST method type.");
                    }

                    String[] produces = null;
                    if (m.getAnnotation(Produces.class) != null) {
                        produces = m.getAnnotation(Produces.class).value();
                    } else if (clazz.getAnnotation(Produces.class) != null) {
                        produces = clazz.getAnnotation(Produces.class).value();
                    }
                    method.setProduces(parseMediaTypes(produces));

                    String[] consumes = null;
                    if (m.getAnnotation(Consumes.class) != null) {
                        consumes = m.getAnnotation(Consumes.class).value();
                    } else if (clazz.getAnnotation(Consumes.class) != null) {
                        consumes = clazz.getAnnotation(Consumes.class).value();
                    }
                    method.setConsumes(parseMediaTypes(consumes));

                    resourcePaths.put(op.toString(), method);
                }
            }
        }
        return resourcePaths;
    }

    private static List<MediaType> parseMediaTypes(String[] types) {
        List<MediaType> mediaTypes = new ArrayList<MediaType>();
        if (types != null) {
            for (String type : types) {
                mediaTypes.add(MediaType.valueOf(type));
            }
        } else {
            mediaTypes.add(MediaType.TEXT_PLAIN_TYPE);
        }
        return mediaTypes;
    }

    /**
     * Construct a query path based on the REST method.
     *
     * @param restMethod the REST method's tuple
     * @param exchange the SwitchYard exchange containing the param
     * @return the query path for the REST method
     */
    public static String getPath(RsMethod restMethod, Exchange exchange) {
        String path = restMethod.getPath();
        if (restMethod.hasPathParam()) {
            path = restMethod.getPath() + "/" + exchange.getMessage().getContent();
        } else if (restMethod.hasQueryParam()) {
            path = restMethod.getPath() + "?" + restMethod.getParamName() + "=" + exchange.getMessage().getContent();
        } else if (restMethod.hasMatrixParam()) {
            path = restMethod.getPath() + ";" + restMethod.getParamName() + "=" + exchange.getMessage().getContent();
        }
        return path;
    }
}
