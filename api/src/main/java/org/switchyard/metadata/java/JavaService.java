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

import org.switchyard.annotations.DefaultType;
import org.switchyard.annotations.OperationTypes;
import org.switchyard.common.type.Classes;
import org.switchyard.exception.SwitchYardException;
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
                OperationTypeQNames operationTypeNames = new OperationTypeQNames(m);
                if (m.getReturnType().equals(Void.TYPE)) {
                    ops.add(new InOnlyOperation(m.getName(), operationTypeNames.in()));
                } else {
                    ops.add(new InOutOperation(m.getName(), operationTypeNames.in(), operationTypeNames.out(), operationTypeNames.fault()));
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
     * Checks for a {@link org.switchyard.annotations.DefaultType} on the type.  If not found,
     * the type name is derived from the Java Class name.
     *
     * @param javaType The Java type.
     * @return The payload type.
     */
    public static QName toMessageType(Class<?> javaType) {
        return QName.valueOf(toMessageTypeString(javaType));
    }

    /**
     * Convert the supplied java type to a payload type name.
     * <p/>
     * Checks for a {@link org.switchyard.annotations.DefaultType} on the type.  If not found,
     * the type name is derived from the Java Class name.
     *
     * @param javaType The Java type.
     * @return The payload type.
     */
    public static String toMessageTypeString(Class<?> javaType) {
        DefaultType defaultType = javaType.getAnnotation(DefaultType.class);

        if (defaultType != null) {
            return defaultType.value();
        } else {
            if (javaType.isMemberClass()) {
                return TYPE_PREFIX + javaType.getName();
            } else {
                return TYPE_PREFIX + javaType.getCanonicalName();
            }
        }
    }
    
    /**
     * Tries to parse the passed in {@link QName} and load the class of that
     * type.
     * 
     * @param type The {@link QName} of the Java type to parse 
     * @return Class<?> The Java class type or null if the {@link QName} passed in was null,
     * or if the passed in type {@link QName} does not have the {@value #TYPE_PREFIX}
     */
    public static Class<?> parseType(final QName type) {
        if (type == null) {
            return null;
        }
        
        final String localPart = type.getLocalPart();
        int indexOf = localPart.indexOf(':');
        if (indexOf != -1) {
            return Classes.forName(localPart.substring(indexOf + 1));
        } else {
            return null;
        }
    }
    
    /**
     * Constructs QNames for method parameter and return types.
     */
    public final static class OperationTypeQNames {

        private Method _operationMethod;
        private OperationTypes _methodTypeNames;

        /**
         * Construct am OperationTypeQNames.
         *
         * @param operationMethod the method that needs to pruned
         */
        public OperationTypeQNames(Method operationMethod) {
            this._operationMethod = operationMethod;
            this._methodTypeNames = operationMethod.getAnnotation(OperationTypes.class);
        }

        /**
         * Construct QName for method parameter.
         *
         * @return the QName
         */
        public QName in() {
            if (_operationMethod.getParameterTypes().length > 0) {
                Class<?> inputType = _operationMethod.getParameterTypes()[0];

                if (_methodTypeNames != null && _methodTypeNames.in().length() != 0) {
                    return QName.valueOf(_methodTypeNames.in());
                }

                return toMessageType(inputType);
            } else {
                return null;
            }
        }

        /**
         * Construct QName for method return type.
         *
         * @return the QName
         */
        public QName out() {
            if (_methodTypeNames != null && _methodTypeNames.out().length() != 0) {
                return QName.valueOf(_methodTypeNames.out());
            }

            return toMessageType(_operationMethod.getReturnType());
        }

        /**
         * Construct QName for exception thrown from method.
         *
         * @return the QName
         */
        public QName fault() {
            Class<?>[] exceptions = _operationMethod.getExceptionTypes();

            if (exceptions.length == 0) {
                return null;
            }
            if (exceptions.length > 1) {
                throw new SwitchYardException("Service operations on a Java interface can only throw one type of exception.");
            }

            if (_methodTypeNames != null && _methodTypeNames.fault().length() != 0) {
                return QName.valueOf(_methodTypeNames.fault());
            }

            return toMessageType(exceptions[0]);
        }
    }
}
