package org.switchyard.metadata;

import javax.xml.namespace.QName;

import org.switchyard.annotations.DefaultType;

/**
 * Provides utility methods for converting a Java class to it's corresponding 
 * data type name in SwitchYard.
 */
public final class JavaTypes {
    
    /**
     * Type prefix.
     */
    private static final String TYPE_PREFIX = "java:";

    private JavaTypes() {
        
    }
    
    /**
     * Equivalent to <code>toMessageType(javaType, null)</code>.
     * <br>
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
}
