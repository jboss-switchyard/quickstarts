/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.component.http.util;

import java.util.regex.Pattern;

/**
 * HTTP Content Type utility methods.
 * 
 * @author <a href="mailto:tom.fennelly@jboss.com">tom.fennelly@jboss.com</a>
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public final class HttpContentTypeUtil {

    private static Pattern[] textMimePatterns;

    static {
        String[] textTypes = {"text/.*", "application/xml", "application/.*\\+xml"};

        textMimePatterns = new Pattern[textTypes.length];
        for (int i = 0; i < textMimePatterns.length; i++) {
            textMimePatterns[i] = Pattern.compile(textTypes[i]);
        }
    }

    private HttpContentTypeUtil() {
    }

    /**
     * Test if content type is of text types.
     * @param contentType teh content type to test
     * @return true if it is a text content type
     */
    public static boolean isTextMimetype(String contentType) {
        if (contentType == null) {
            return false;
        }

        final int separator = contentType.indexOf(';');
        if (separator >= 0) {
            contentType = contentType.substring(0, separator);
        }
        for (int i = 0; i < textMimePatterns.length; i++) {
            if (textMimePatterns[i].matcher(contentType).matches()) {
                return true;
            }
        }

        return false;
    }
}
