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

package org.switchyard.extensions.java;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;

import org.switchyard.APIMessages;
import org.switchyard.annotations.OperationTypes;
import org.switchyard.common.type.Classes;
import org.switchyard.metadata.BaseService;
import org.switchyard.metadata.InOnlyOperation;
import org.switchyard.metadata.InOutOperation;
import org.switchyard.metadata.JavaTypes;
import org.switchyard.metadata.ServiceOperation;

/**
 * An implementation of ServiceInterface for Java classes.  The 
 * <code>fromClass()</code> method can be used to create a ServiceInterface
 * representation from a Java class or interface.  A ServiceOperation will be 
 * created for each public methods on the Java interface.  Inherited 
 * superclass methods are not included to avoid polluting
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
     * Interface type for Java service contracts.
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
     * This is the equivalent of <code>fromClass(serviceInterface, null)</code>.
     * @param serviceInterface class or interface representing the service 
     * interface
     * @return ServiceInterface representing the Java class
     */
    public static JavaService fromClass(Class<?> serviceInterface) {
        HashSet<ServiceOperation> ops = new HashSet<ServiceOperation>();
        // Use all methods in type hierarchy for interfaces, but avoid this
        // for classes which can drag in garbage from java.lang.Object, etc.
        Method[] methods = serviceInterface.isInterface()
                ? serviceInterface.getMethods()
                : serviceInterface.getDeclaredMethods();
        for (Method m : methods) {
            // We only consider public methods
            if (Modifier.isPublic(m.getModifiers())) {
                // At this point, we only accept methods with a single 
                // parameter which maps to the input message
                Class<?>[] params = m.getParameterTypes();
                if (params.length > 1) {
                    throw APIMessages.MESSAGES.serviceOpNeedOneParamater();
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

                return JavaTypes.toMessageType(inputType);
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

            return JavaTypes.toMessageType(_operationMethod.getReturnType());
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
                throw APIMessages.MESSAGES.serviceOpOnlyOneParameter();
            }

            if (_methodTypeNames != null && _methodTypeNames.fault().length() != 0) {
                return QName.valueOf(_methodTypeNames.fault());
            }

            return JavaTypes.toMessageType(exceptions[0]);
        }
    }
}
