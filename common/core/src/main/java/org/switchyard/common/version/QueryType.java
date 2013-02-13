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
