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
 
package org.switchyard.component.resteasy.util;

import java.util.ArrayList;
import java.util.List;
import java.lang.reflect.Field;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;

import org.apache.log4j.Logger;
import org.switchyard.common.type.Classes;

/**
 * Utility class that generates RESTEasy singleton instances.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public final class ClassUtil {

    private static final Logger LOGGER = Logger.getLogger(ClassUtil.class);
    private static final String SUFFIX = "RestImpl";
    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir");

    private static final String END_OF_LINE = ";";
    private static final String FIELD_NAME = "_serviceConsumer";
    private static final String FIELD_DECL = "public org.switchyard.component.resteasy.InboundHandler " + FIELD_NAME + END_OF_LINE;
    private static final String METHOD_BODY = "{%s}";
    private static final String METHOD_FRAGMENT = FIELD_NAME + ".invoke(\"%s\", %s";
    private static final String METHOD_NO_RETURN = "%s, %b)" + END_OF_LINE;
    private static final String METHOD_RETURN = "return (%s)%s, %b)" + END_OF_LINE;

    private ClassUtil() {
    }

    /**
     * Generate a singleton class for a JAX-RS Resource interface and create an instance.
     *
     * @param resourceIntfs an array of JAX-RS Resource interface names
     * @param handler a SwitchYard service handler that invokes the respective service
     * @return a List of Resource implementations
     * @throws Exception if generation fails
     */
    public static List<Object> generateSingletons(String[] resourceIntfs, Object handler) throws Exception {
        List<Object> instances = new ArrayList<Object>();
        for (String resourceIntf : resourceIntfs) {
            Class<?> clazz = generateClass(resourceIntf);
            Object instance = clazz.newInstance();
            Field field = clazz.getField(FIELD_NAME);
            field.set(instance, handler);
            instances.add(instance);
        }
        return instances;
    }

    private static Class<?> generateClass(String resourceIntf) throws Exception {
        Class<?> resourceClazz = Classes.forName(resourceIntf);
        String implClass = resourceIntf + SUFFIX;
        Class<?> clazz = Classes.forName(implClass);
        if (clazz == null) {
            ClassPool pool = ClassPool.getDefault();
            pool.insertClassPath(new ClassClassPath(resourceClazz));
            if (LOGGER.isTraceEnabled()) {
                LOGGER.debug("Creating implementation class for " + resourceIntf);
            }
            CtClass intf = pool.get(resourceIntf);
            CtClass cc = null;
            try {
                cc = pool.get(implClass);
                if ((cc != null) && cc.isFrozen()) {
                    cc.defrost();
                    cc.detach();
                }
            } catch (NotFoundException nfe) {
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace(nfe);
                }
            }
            cc = pool.makeClass(implClass);
            if (intf.isInterface()) {
                cc.setInterfaces(new CtClass[]{intf});
            } else {
                cc.setSuperclass(intf);
            }

            CtField ctField = CtField.make(FIELD_DECL, cc);
            cc.addField(ctField);

            CtMethod[] intfMethods = intf.getDeclaredMethods();
            for (CtMethod intfMethod : intfMethods) {
                CtMethod method = CtNewMethod.copy(intfMethod, cc, null);
                CtClass[] paramTypes = method.getParameterTypes();
                String body = "";
                String fragment = "";
                if (paramTypes.length == 1) {
                    fragment = String.format(METHOD_FRAGMENT, method.getName(), "$1");
                } else {
                    fragment = String.format(METHOD_FRAGMENT, method.getName(), "null");
                }
                CtClass returnType = method.getReturnType();
                if (!(returnType == CtClass.voidType)) {
                    body = String.format(METHOD_BODY, String.format(METHOD_RETURN, returnType.getName(), fragment, false));
                } else {
                    body = String.format(METHOD_BODY, String.format(METHOD_NO_RETURN, fragment, true));
                }
                method.setBody(body);
                cc.addMethod(method);
            }
            clazz = cc.toClass();
            if (LOGGER.isTraceEnabled()) {
                cc.writeFile(TEMP_DIR);
            }
        }
        return clazz;
    }
}
