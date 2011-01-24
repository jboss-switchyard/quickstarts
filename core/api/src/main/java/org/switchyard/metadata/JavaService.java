/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.switchyard.metadata;

import org.switchyard.message.PayloadTypeName;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

/**
 * An implementation of ServiceInterface for Java classes.  The 
 * <code>fromClass()</code> method can be used to create a ServiceInterface
 * representation from a Java class or interface.  A ServiceOperation will be 
 * created for each public methods declared directly on the Java class or 
 * interface.  Inherited superclass methods are not included to avoid polluting
 * the ServiceInterface with methods from java.lang.Object, java.io.Serializable,
 * etc.
 * <br><br>
 * Operation names are mapped directly from the method names on the Java class.
 * <br><br>
 * Message names are created using the FQ class name, method name, and 
 * parameter/return type.  A prefix of JavaService.TYPE is used to produce a 
 * name of the format 
 * <code>java:[class.getName]/[methodName]/[type.class.getName]</code>.
 */
public final class JavaService extends BaseService {
    
    /**
     *  The type returned from ServiceInterface.getType().
     */
    public static final String TYPE = "java";
    
    // Java class used to create this ServiceInterface
    private Class<?> _serviceInterface;

    /**
     * Private ctor for creating a new ServiceInterface.  Clients of the API
     * should use fromClass() to create a ServiceInterface from a Java class.
     * @param operations list of operations on the service interface
     * @param serviceInterface the java class used to derive the interface
     */
    private JavaService(Set<ServiceOperation> operations, Class<?> serviceInterface) {
        super(operations, TYPE);
        _serviceInterface = serviceInterface;
    }
    
    /**
     * Creates a ServiceInterface from the specified Java class or interface.
     * @param serviceInterface class or interface representing the service 
     * interface
     * @return ServiceInterface representing the Java class
     */
    public static JavaService fromClass(Class<?> serviceInterface) {
        HashSet<ServiceOperation> ops = new HashSet<ServiceOperation>();
        for (Method m : serviceInterface.getDeclaredMethods()) {
            // We only consider public methods
            if (Modifier.isPublic(m.getModifiers())) {
                // At this point, we only accept methods with a single 
                // parameter which maps to the input message
                Class<?>[] params = m.getParameterTypes();
                if (params.length != 1) {
                    throw new RuntimeException(
                            "Service operations on a Java interface must have exactly one parameter!");
                }
                // Create the appropriate service operation and add it to the list
                String inputType = formatName(serviceInterface.getCanonicalName(), m.getName(), params[0].getCanonicalName());
                if (m.getReturnType().equals(Void.TYPE)) {
                    ops.add(new InOnlyOperation(m.getName(), inputType));
                } else {
                    String outputName = formatName(serviceInterface.getCanonicalName(), m.getName(), m.getReturnType().getCanonicalName());
                    String faultType = null; //TODO: Can have multiple exception types     .. which do we pick?

                    ops.add(new InOutOperation(m.getName(), inputType, outputName, faultType));
                }
            }
        }
        
        return new JavaService(ops, serviceInterface);
    }

    /**
     * Returns the Java class or interface used to create this ServiceInterface.
     * representation
     * @return Java class or interface
     */
    public Class<?> getJavaInterface() {
        return _serviceInterface;
    }

    /**
     * Creates a URN-style name based on a list of parts.
     * @param parts parts of the name
     * @return URN-style name used for the message name
     */
    private static String formatName(String... parts) {
        StringBuilder formattedName = new StringBuilder();
        for (String p : parts) {
            formattedName.append("/" + p);
        }
        return formattedName.insert(0, TYPE + ":").toString();
    }

    /**
     * Convert the supplied java type to a payload type name.
     * <p/>
     * Checks for a {@link org.switchyard.message.PayloadTypeName} on the type.  If not found,
     * the type name is derived from the Java Class name.
     *
     * @param javaType The Java type.
     * @return The payload type.
     */
    public static String toMessageType(Class<?> javaType) {
        PayloadTypeName payloadType = javaType.getAnnotation(PayloadTypeName.class);

        if (payloadType != null) {
            return payloadType.value();
        } else {
            return formatName(javaType.getCanonicalName());
        }
    }
}
