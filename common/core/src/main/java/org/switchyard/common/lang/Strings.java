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
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.switchyard.common.property.CompoundPropertyResolver;
import org.switchyard.common.property.PropertyResolver;
import org.switchyard.common.property.SystemAndTestPropertyResolver;
import org.switchyard.common.property.SystemPropertyResolver;
import org.switchyard.common.property.TestPropertyResolver;

/**
 * Common String Utilities.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public final class Strings {

    /** Default regex replacements for cleansing Strings. */
    public static final Map<String, String> DEFAULT_CLEANSE_REGEX_REPLACEMENTS;
    static {
        Map<String, String> map = new LinkedHashMap<String, String>();
        map.put("[\\W_]", "-");
        DEFAULT_CLEANSE_REGEX_REPLACEMENTS = Collections.unmodifiableMap(map);
    }

    private static final Pattern DOUBLE_DOLLAR_LEFT_CURLY_PATTERN = Pattern.compile("\\$\\$\\{");
    private static final Pattern INNER_DOLLAR_CURLIES_PATTERN = Pattern.compile("\\$\\{[[^\\$\\{]&&[^\\}]]*\\}");
    private static final Pattern SINGLE_COLON_PATTERN = Pattern.compile("[^:+]:[^:+]");
    private static final String PENCIL = String.valueOf((char)0x270F); // an actual pencil dingbat, used as a placeholder
    private static final String DOLLAR = "\\$";
    private static final String LEFT_CURLY = "{";
    private static final String RIGHT_CURLY = "}";
    private static final String PENCIL_LEFT_CURLY = PENCIL + LEFT_CURLY;

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
     * Repeats the specified String given number of times.
     * @param str String to repeat.
     * @param repeatCount Number of repetitions
     * @return The repeated String.
     */
    public static String repeat(String str, int repeatCount) {
        String repeated = "";
        if (str.isEmpty() || repeatCount <= 0) {
            return repeated;
        }

        do {
            repeated += str;
        } while (--repeatCount > 0);
        return repeated;
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
     * Replaces ONLY system properties.
     * @param str the original string
     * @return the modified string
     */
    public static String replaceSystemProperties(String str) {
        return replaceProperties(str, SystemPropertyResolver.INSTANCE);
    }

    /**
     * Replaces ONLY test properties.
     * @param str the original string
     * @return the modified string
     */
    public static String replaceTestProperties(String str) {
        return replaceProperties(str, TestPropertyResolver.INSTANCE);
    }

    /**
     * Replaces BOTH system and test properties.
     * @param str the original string
     * @return the modified string
     */
    public static String replaceSystemAndTestProperties(String str) {
        return replaceProperties(str, SystemAndTestPropertyResolver.INSTANCE);
    }

    /**
     * Replaces properties per the given property resolvers.
     * @param str the original string
     * @param resolvers the property resolvers
     * @return the modified string
     */
    public static String replaceProperties(String str, PropertyResolver... resolvers) {
        if (str != null) {
            PropertyResolver resolver = CompoundPropertyResolver.compact(resolvers);
            boolean penciled = false;
            while (true) {
                Matcher ddlc_mat = DOUBLE_DOLLAR_LEFT_CURLY_PATTERN.matcher(str);
                if (ddlc_mat.find()) {
                    str = ddlc_mat.replaceAll(PENCIL_LEFT_CURLY);
                    penciled = true;
                }
                Matcher idc_mat = INNER_DOLLAR_CURLIES_PATTERN.matcher(str);
                if (!idc_mat.find()) {
                    break;
                }
                int l_pos = idc_mat.start();
                int r_pos = idc_mat.end() - 1;
                String prop_key = str.substring(l_pos + 2, r_pos);
                String real_key;
                String def_val;
                Matcher sc_mat = SINGLE_COLON_PATTERN.matcher(prop_key);
                if (sc_mat.find()) {
                    int sc_pos = sc_mat.start() + 1;
                    real_key = prop_key.substring(0, sc_pos);
                    def_val = prop_key.substring(sc_pos + 1, prop_key.length());
                } else {
                    real_key = prop_key;
                    def_val = null;
                }
                Object obj_val = resolver.resolveProperty(real_key);
                if (obj_val == null) {
                    obj_val = def_val;
                }
                String str_val;
                if (obj_val != null) {
                    str_val = obj_val.toString();
                } else {
                    str_val = PENCIL_LEFT_CURLY + real_key + RIGHT_CURLY;
                    penciled = true;
                }
                str = new StringBuilder()
                    .append(str.substring(0, l_pos))
                    .append(str_val)
                    .append(str.substring(r_pos + 1, str.length()))
                    .toString();
            }
            if (penciled) {
                str = str.replaceAll(PENCIL, DOLLAR);
            }
        }
        return str;
    }

    /**
     * Replaces properties per the given property resolvers.
     * @param str the original string
     * @param resolvers the property resolvers
     * @return the modified string
     */
    public static String replaceProperties(String str, Collection<PropertyResolver> resolvers) {
        return replaceProperties(str, CompoundPropertyResolver.compact(resolvers));
    }

    private Strings() {}

}
