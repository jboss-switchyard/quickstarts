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

package org.switchyard.common.xml;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import javax.xml.namespace.QName;

import org.switchyard.common.type.Classes;

/**
 * QName utility methods.
 *
 * @author <a href="mailto:tom.fennelly@gmail.com">tom.fennelly@gmail.com</a>
 */
public final class QNameUtil {

    private QNameUtil() {
    }

    /**
     *  Java message type.
     */
    public static final String JAVA_TYPE = "java";

    /**
     * Java Type prefix.
     */
    private static final String JAVA_TYPE_PREFIX = JAVA_TYPE + ":";

    /**
     * Primitive array types.
     */
    private static final List<String> PRIMITIVES = Arrays.asList(
        "byte[]", "short[]", "int[]", "long[]", "float[]", "double[]", "boolean[]", "char[]"
    );

    /**
     * Is the specified message type QName a Java message type.
     * @param name The message type {@link javax.xml.namespace.QName} to be tested.
     * @return True if it is a Java message type, otherwise false.
     */
    public static boolean isJavaMessageType(QName name) {
        return name.getLocalPart().startsWith(JAVA_TYPE_PREFIX);
    }

    /**
     * Get the Java runtime class for the specified message type QName.
     * @param name The message type {@link javax.xml.namespace.QName}.
     * @return The Java runtime class for the specified message type QName, otherwise null.
     */
    public static Class<?> toJavaMessageType(QName name) {
        if (!isJavaMessageType(name)) {
            throw new RuntimeException("Invalid call.  Not a Java message type.  Use isJavaMessageType before calling this method.");
        }

        String className = name.getLocalPart().substring(JAVA_TYPE_PREFIX.length());
        if (!PRIMITIVES.contains(className) && className.contains("[]")) {
            className = className.substring(0, className.length() - 2);
            return Array.newInstance(Classes.forName(className), 0).getClass();
        }
        return Classes.forName(className);
    }
}
