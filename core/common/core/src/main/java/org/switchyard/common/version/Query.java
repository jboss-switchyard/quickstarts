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
package org.switchyard.common.version;

import java.net.URL;
import java.util.regex.Pattern;

/**
 * Represents a Query to a VersionFactory.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2012 Red Hat Inc.
 */
public final class Query {

    private final QueryType _type;
    private final Pattern _pattern;

    /**
     * Creates a new Query.
     * @param type the QueryType
     * @param pattern the Pattern
     */
    public Query(QueryType type, Pattern pattern) {
        _type = type;
        _pattern = pattern;
    }

    /**
     * Creates a new Query.
     * @param type the QueryType
     * @param regex the regular expression
     */
    public Query(QueryType type, String regex) {
        this(type, Pattern.compile(regex));
    }

    /**
     * Gets the QueryType.
     * @return the QueryType
     */
    public QueryType getType() {
        return _type;
    }

    /**
     * Gets the Pattern.
     * @return the Pattern
     */
    public Pattern getPattern() {
        return _pattern;
    }

    /**
     * Sees if a Version matches this Query.
     * @param version the Version
     * @return if it matches
     */
    public boolean matches(Version version) {
        final String input;
        switch (getType()) {
            case PROJECT_GROUP_ID:
                input = version.getProject().getGroupId();
                break;
            case PROJECT_ARTIFACT_ID:
                input = version.getProject().getArtifactId();
                break;
            case PROJECT_PACKAGING:
                input = version.getProject().getPackaging();
                break;
            case PROJECT_NAME:
                input = version.getProject().getName();
                break;
            case PROJECT_DESCRIPTION:
                input = version.getProject().getDescription();
                break;
            case PROJECT_URL:
                final URL pUrl = version.getProject().getURL();
                input = pUrl != null ? pUrl.toString() : null;
                break;
            case PROJECT_VERSION:
                input = version.getProject().getVersion();
                break;
            case SPECIFICATION_TITLE:
                input = version.getSpecification().getTitle();
                break;
            case SPECIFICATION_VENDOR:
                input = version.getSpecification().getVendor();
                break;
            case SPECIFICATION_VERSION:
                input = version.getSpecification().getVersion();
                break;
            case IMPLEMENTATION_TITLE:
                input = version.getImplementation().getTitle();
                break;
            case IMPLEMENTATION_VENDOR:
                input = version.getImplementation().getVendor();
                break;
            case IMPLEMENTATION_VENDOR_ID:
                input = version.getImplementation().getVendorId();
                break;
            case IMPLEMENTATION_URL:
                final URL iUrl = version.getImplementation().getURL();
                input = iUrl != null ? iUrl.toString() : null;
                break;
            case IMPLEMENTATION_VERSION:
                input = version.getImplementation().getVersion();
                break;
            default:
                input = null;
        }
        return (input != null) ? getPattern().matcher(input).matches() : false;
    }

}
