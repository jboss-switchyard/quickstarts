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
package org.switchyard.common.lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

import org.switchyard.common.property.PropertyResolver;
import org.switchyard.common.property.SystemPropertiesPropertyResolver;

/**
 * Common String Utilities.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public final class Strings {

    /**
     * Default regex replacements for cleansing Strings.
     */
    public static final Map<String, String> DEFAULT_CLEANSE_REGEX_REPLACEMENTS;
    static {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("[\\W_]", "-");
        DEFAULT_CLEANSE_REGEX_REPLACEMENTS = Collections.unmodifiableMap(map);
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
     * Cleanses a String using the default cleanse regex replacments.
     * @param str the String
     * @return the cleansed String
     * @see {@link #DEFAULT_CLEANSE_REGEX_REPLACEMENTS}
     */
    public static String cleanse(String str) {
        return cleanse(str, DEFAULT_CLEANSE_REGEX_REPLACEMENTS);
    }

    /**
     * Cleanses a String using the specified regex replacements.
     * @param str the String
     * @param regexReplacements the regex replacements
     * @return the cleansed String
     */
    public static String cleanse(String str, Map<String, String> regexReplacements) {
        if (str != null) {
            for (Entry<String, String> entry : regexReplacements.entrySet()) {
                String regex = entry.getKey();
                String replacement = entry.getValue();
                str = str.replaceAll(regex, replacement);
                String doubleReplacement = replacement + replacement;
                while (str.contains(doubleReplacement)) {
                    str = str.replaceAll(doubleReplacement, replacement);
                }
                if (str.startsWith(replacement)) {
                    str = str.substring(replacement.length());
                }
                if (str.endsWith(replacement)) {
                    str = str.substring(0, str.length()-1);
                }
            }
        }
        return str;
    }

    /**
     * Cleanses a String using the default cleanse regex replacments, then calls {@link #trimToNull(String)} on the result.
     * @param str the String
     * @return the cleansed and trimmed-to-null String
     * @see {@link #DEFAULT_CLEANSE_REGEX_REPLACEMENTS}
     */
    public static String cleanseTrimToNull(String str) {
        return trimToNull(cleanse(str));
    }

    /**
     * Cleanses a String using the specified cleanse regex replacments, then calls {@link #trimToNull(String)} on the result.
     * @param str the String
     * @param regexReplacements the regex replacements
     * @return the cleansed and trimmed-to-null String
     */
    public static String cleanseTrimToNull(String str, Map<String, String> regexReplacements) {
        return trimToNull(cleanse(str, regexReplacements));
    }

    /**
     * Splits the specified String per the specified delimiters (per StringTokenizer rules),
     * then trims each split String, and if not zero-length, becomes part of the returned List.
     * @param str the specified String
     * @param delim the specified delimiters
     * @return the split and trimmed-to-null'd Strings in an unmodifiable List (possibly zero-size, but never null)
     */
    public static List<String> splitTrimToNull(String str, String delim) {
        List<String> list = new ArrayList<String>();
        if (str != null) {
            StringTokenizer st = new StringTokenizer(str, delim);
            while (st.hasMoreTokens()) {
                String s = trimToNull(st.nextToken());
                if (s != null) {
                    list.add(s);
                }
            }
        }
        return Collections.unmodifiableList(list);
    }

    /**
     * Same as {@link #splitTrimToNull(String, String)}, except a String array is returned.
     * @param str the specified String
     * @param delim the specified delimiters
     * @return the split and trimmed-to-null'd Strings in an array (possibly zero-length, but never null)
     */
    public static String[] splitTrimToNullArray(String str, String delim) {
        List<String> list = splitTrimToNull(str, delim);
        return list.toArray(new String[list.size()]);
    }

    /**
     * Splits the specified String per the specified delimiters (per StringTokenizer rules),
     * then trims each split String, and if not zero-length, becomes part of the returned Set.
     * @param str the specified String
     * @param delim the specified delimiters
     * @return the split and trimmed-to-null'd Strings in an unmodifiable, insertion-ordered Set (possibly zero-size, but never null)
     */
    public static Set<String> uniqueSplitTrimToNull(String str, String delim) {
        Set<String> set = new LinkedHashSet<String>();
        if (str != null) {
            StringTokenizer st = new StringTokenizer(str, delim);
            while (st.hasMoreTokens()) {
                String s = trimToNull(st.nextToken());
                if (s != null) {
                    set.add(s);
                }
            }
        }
        return Collections.unmodifiableSet(set);
    }

    /**
     * Same as {@link #uniqueSplitTrimToNull(String, String)}, except a String array is returned.
     * @param str the specified String
     * @param delim the specified delimiters
     * @return the split and trimmed-to-null'd Strings in an array (possibly zero-length, but never null)
     */
    public static String[] uniqueSplitTrimToNullArray(String str, String delim) {
        Set<String> set = uniqueSplitTrimToNull(str, delim);
        return set.toArray(new String[set.size()]);
    }

    /**
     * Concatenates Strings using no delimiter, trimming each to null. Nulls are not concatenated.
     * @param str the original Strings
     * @return the concatenated String
     * @see #trimToNull(String)
     */
    public static String concat(String... str) {
        return concat(null, str);
    }

    /**
     * Concatenates Strings using the specified delimiter, trimming each to null. Nulls are not concatenated.
     * @param delim the delimiter
     * @param str the original Strings
     * @return the concatenated String
     * @see #trimToNull(String)
     */
    public static String concat(String delim, String... str) {
        return concat(delim, true, str);
    }

    /**
     * Concatenates Strings using the specified delimiter, possibly trimming each to null. Nulls are not concatenated.
     * @param delim the delimiter
     * @param trimToNull should each String be trimmed to null first?
     * @param str the original Strings
     * @return the concatenated String
     * @see #trimToNull(String)
     */
    public static String concat(String delim, boolean trimToNull, String... str) {
        if (str != null && str.length > 0) {
            StringBuilder sb = new StringBuilder();
            for (int i=0; i < str.length; i++) {
                String s = str[i];
                if (trimToNull) {
                    s = trimToNull(s);
                }
                if (s != null) {
                    sb.append(s);
                    if (i < str.length-1) {
                        sb.append(delim);
                    }
                }
            }
            return sb.toString();
        }
        return null;
    }

    /**
     * Replaces system properties.
     * @param str the original string
     * @return the modified string
     */
    public static String replaceSystemProperties(String str) {
        return replaceProperties(str, SystemPropertiesPropertyResolver.instance());
    }

    /**
     * Replaces properties per the given property resolver.
     * @param str the original string
     * @param resolver the property resolver
     * @return the modified string
     */
    public static String replaceProperties(String str, PropertyResolver resolver) {
        if (str != null && resolver != null) {
            int l_pos = str.indexOf("${", 0);
            while (l_pos != -1) {
                int r_pos = str.indexOf('}', l_pos + 2);
                if (r_pos == -1) {
                    break;
                }
                String key = str.substring(l_pos + 2, r_pos);
                String value = resolver.resolveProperty(key);
                if (value != null) {
                    String begin = str.substring(0, l_pos);
                    str = new StringBuilder()
                        .append(begin)
                        .append(value)
                        .append(str.substring(r_pos + 1, str.length()))
                        .toString();
                    l_pos = str.indexOf("${", begin.length() + value.length());
                } else {
                    l_pos = str.indexOf("${", l_pos + 2);
                }
            }
        }
        return str;
    }

    private Strings() {}

}
