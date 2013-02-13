/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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
