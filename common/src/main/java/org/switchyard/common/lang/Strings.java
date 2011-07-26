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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Common String Utilities.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public final class Strings {

    private Strings() {}

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

}
