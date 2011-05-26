/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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

package org.switchyard.metadata.java;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;

import org.switchyard.metadata.BaseService;
import org.switchyard.metadata.InOnlyOperation;
import org.switchyard.metadata.InOutOperation;
import org.switchyard.metadata.ServiceOperation;

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
 * Message names are created using the FQ class name. A prefix of 
 * <code>JavaService.TYPE</code> is used to produce a name of the format 
 * <code>java:[class.getName()]</code>, e.g. <code>java:org.example.MyType</code>.
 */
public final class JavaService extends BaseService {
    
    /**
     *  The type returned from ServiceInterface.getType().
     */
    public static final String TYPE = "java";
    /**
     * Type prefix.
     */
    private static final String TYPE_PREFIX = TYPE + ":";
    
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
     * This is the equivalent of <code>fromClass(serviceInterface, null)</code>.
     * @param serviceInterface class or interface representing the service 
     * interface
     * @return ServiceInterface representing the Java class
     */
    public static JavaService fromClass(Class<?> serviceInterface) {
        return fromClass(serviceInterface, null);
    }
    
    /**
     * Creates a ServiceInterface from the specified Java class or interface.
     * All QNames in the interface will use the specified namespace URI.
     * @param serviceInterface class or interface representing the service 
     * interface
     * @param namespaceURI namespace to be used for type QNames
     * @return ServiceInterface representing the Java class
     */
    public static JavaService fromClass(Class<?> serviceInterface, String namespaceURI) {
        HashSet<ServiceOperation> ops = new HashSet<ServiceOperation>();
        for (Method m : serviceInterface.getDeclaredMethods()) {
            // We only consider public methods
            if (Modifier.isPublic(m.getModifiers())) {
                // At this point, we only accept methods with a single 
                // parameter which maps to the input message
                Class<?>[] params = m.getParameterTypes();
                if (params.length != 1) {
                    throw new RuntimeException(
                            "Service operations on a Java interface must have exactly one parameter.");
                }
                // Create the appropriate service operation and add it to the list
                QName inputType = toMessageType(params[0], namespaceURI);
                if (m.getReturnType().equals(Void.TYPE)) {
                    ops.add(new InOnlyOperation(m.getName(), inputType));
                } else {
                    QName outputName = toMessageType(m.getReturnType(), namespaceURI);
                    Class<?>[] exceptions = m.getExceptionTypes();
                    QName faultType = null;
                    if (exceptions.length > 0) {
                        if (exceptions.length > 1) {
                            throw new RuntimeException(
                            "Service operations on a Java interface can only throw one type of exception.");
                        }
                        faultType = toMessageType(exceptions[0], namespaceURI);
                    }
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
     * Equivalent to <code>toMessageType(javaType, null)</code>.
     * <br>
     * @see JavaService#toMessageType(Class)
     * Checks for a {@link org.switchyard.metadata.java.PayloadTypeName} on the type.  If not found,
     * the type name is derived from the Java Class name.
     *
     * @param javaType The Java type.
     * @return The payload type.
     */
    public static QName toMessageType(Class<?> javaType) {
        return toMessageType(javaType, null);
    }
    
    /**
     * Convert the supplied java type to a payload type name.
     * <p/>
     * Checks for a {@link org.switchyard.metadata.java.PayloadTypeName} on the type.  If not found,
     * the type name is derived from the Java Class name.
     *
     * @param javaType The Java type.
     * @param namespaceURI namespace for the type QName
     * @return The payload type.
     */
    public static QName toMessageType(Class<?> javaType, String namespaceURI) {
        PayloadTypeName payloadType = javaType.getAnnotation(PayloadTypeName.class);

        if (payloadType != null) {
            return QName.valueOf(payloadType.value());
        } else {
            if (javaType.isMemberClass()) {
                return new QName(namespaceURI, TYPE_PREFIX + javaType.getName());
            } else {
                return new QName(namespaceURI, TYPE_PREFIX + javaType.getCanonicalName());
            }
        }
    }

    /**
     * Is the specified message type QName a Java message type.
     * @param name The message type {@link QName}to be tested.
     * @return True if it is a Java message type, otherwise false.
     */
    public static boolean isJavaMessageType(QName name) {
        return name.getLocalPart().startsWith(TYPE_PREFIX);
    }

    /**
     * Is the specified message type QName a Java message type.
     * @param name The message type {@link QName}to be tested.
     * @return True if it is a Java message type, otherwise false.
     */
    public static Class<?> toJavaMessageType(QName name) {
        if (!isJavaMessageType(name)) {
            throw new RuntimeException("Invalid call.  Not a Java message type.  Use isJavaMessageType before calling this method.");
        }

        String className = name.getLocalPart().substring(TYPE_PREFIX.length());
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(className);
        } catch (ClassNotFoundException e1) {
            try {
                return Class.forName(className);
            } catch (ClassNotFoundException e2) {
                // can't use this.... ignore...
                return null;
            }
        }
    }
}
