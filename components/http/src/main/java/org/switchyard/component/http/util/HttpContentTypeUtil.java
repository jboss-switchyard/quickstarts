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
