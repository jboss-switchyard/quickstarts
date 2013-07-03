/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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
