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
package org.switchyard.label;


/**
 * Labels mark context properties, in their String form.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public interface Label {

    /**
     * Gets the label name.
     * @return the label name
     */
    public String name();

    /**
     * Gets the full-form label String.
     * @return the full-form label String
     */
    public String label();

    /** Util. */
    public static class Util {

        private static final String PREFIX = "org.switchyard.label.";

        /**
         * Builds a label String given the specified label type and name. 
         * @param labelType the type
         * @param name the name
         * @return the full label String
         */
        public static final String toSwitchYardLabel(String labelType, String name) {
            labelType = labelType.trim().toLowerCase();
            name = name.trim().toLowerCase();
            return new StringBuilder(PREFIX).append(labelType).append('.').append(name).toString();
        }

        /**
         * Retrieves a Label enum given the specified enum type and name.
         * @param <T> the enum class type
         * @param enumType the enum type
         * @param name the name
         * @return the Label enum
         */
        public static final <T extends Enum<T>> T ofName(Class<T> enumType, String name) {
            name = trimToNull(name);
            if (name != null) {
                name = name.toUpperCase();
                try {
                    return Enum.valueOf(enumType, name);
                } catch (IllegalArgumentException iae) {
                    return null;
                }
            }
            return null;
        }
        
        /**
         * Trims the specified String, and if after it is zero-length, return null.
         * @param str the specified String
         * @return the trim-to-null'd String
         */
        public static String trimToNull(String str) {
            if (str != null) {
                str = str.trim();
                if (str.length() == 0) {
                    str = null;
                }
            }
            return str;
        }

        /**
         * Prints the labels.
         * @param labels the labels
         */
        public static final void print(Label... labels) {
            for (Label label : labels) {
                System.out.println(String.format("%s: %s = %s", label.getClass().getName(), label.name(), label.label()));
            }
        }

    }

}
