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

/**
 * QueryType.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2012 Red Hat Inc.
 */
public enum QueryType {

    /** project groupId. */
    PROJECT_GROUP_ID,
    /** project artifactId. */
    PROJECT_ARTIFACT_ID,
    /** project packaging. */
    PROJECT_PACKAGING,
    /** project name. */
    PROJECT_NAME,
    /** project description. */
    PROJECT_DESCRIPTION,
    /** project url. */
    PROJECT_URL,
    /** project version. */
    PROJECT_VERSION,

    /** specification title. */
    SPECIFICATION_TITLE,
    /** specification vendor. */
    SPECIFICATION_VENDOR,
    /** specification version. */
    SPECIFICATION_VERSION,

    /** implementation title. */
    IMPLEMENTATION_TITLE,
    /** implementation vendor. */
    IMPLEMENTATION_VENDOR,
    /** implementation vendorId. */
    IMPLEMENTATION_VENDOR_ID,
    /** implementation url. */
    IMPLEMENTATION_URL,
    /** implementation version. */
    IMPLEMENTATION_VERSION;

}
