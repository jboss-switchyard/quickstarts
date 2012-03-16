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

import static org.switchyard.common.version.QueryType.PROJECT_ARTIFACT_ID;
import static org.switchyard.common.version.QueryType.PROJECT_GROUP_ID;

/**
 * Queries.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2012 Red Hat Inc.
 */
public final class Queries {

    private Queries() {}

    /**
     * Project Queries.
     */
    public static final class Projects {

        private Projects() {}

        /** The PROJECT_GROUP_ID/"org.switchyard" Query. */
        public static final Query SWITCHYARD = new Query(PROJECT_GROUP_ID, "org.switchyard");

        /** Well-known SwitchYard Project Queries. */
        public static final Query[]
                SWITCHYARD_ADMIN = create(SWITCHYARD, "switchyard-admin"),
                SWITCHYARD_API = create(SWITCHYARD, "switchyard-api"),
                SWITCHYARD_COMMON = create(SWITCHYARD, "switchyard-common"),
                SWITCHYARD_COMPONENT_BEAN = create(SWITCHYARD, "switchyard-component-bean"),
                SWITCHYARD_COMPONENT_BPEL = create(SWITCHYARD, "switchyard-component-bpel"),
                SWITCHYARD_COMPONENT_BPM = create(SWITCHYARD, "switchyard-component-bpm"),
                SWITCHYARD_COMPONENT_CAMEL = create(SWITCHYARD, "switchyard-component-camel"),
                SWITCHYARD_COMPONENT_CLOJURE = create(SWITCHYARD, "switchyard-component-clojure"),
                SWITCHYARD_COMPONENT_COMMON_RULES = create(SWITCHYARD, "switchyard-component-common-rules"),
                SWITCHYARD_COMPONENT_HORNETQ = create(SWITCHYARD, "switchyard-component-hornetq"),
                SWITCHYARD_COMPONENT_RULES = create(SWITCHYARD, "switchyard-component-rules"),
                SWITCHYARD_COMPONENT_SOAP = create(SWITCHYARD, "switchyard-component-soap"),
                SWITCHYARD_CONFIG = create(SWITCHYARD, "switchyard-config"),
                SWITCHYARD_RUNTIME = create(SWITCHYARD, "switchyard-runtime"),
                SWITCHYARD_TRANSFORM = create(SWITCHYARD, "switchyard-transform"),
                SWITCHYARD_VALIDATE = create(SWITCHYARD, "switchyard-validate");

        /**
         * Creates a new Project Query[].
         * @param groupId the Project groupId
         * @param artifactId the Project artifactId
         * @return the Project Query[]
         */
        public static Query[] create(String groupId, String artifactId) {
            return create(new Query(PROJECT_GROUP_ID, groupId), artifactId);
        }

        /**
         * Creates a new Project Query[].
         * @param groupQuery the Project group Query
         * @param artifactId the Project artifactId
         * @return the Project Query[]
         * @throws IllegalArgumentException if the groupQuery is not of type PROJECT_GROUP_ID
         */
        public static Query[] create(Query groupQuery, String artifactId) throws IllegalArgumentException {
            if (!groupQuery.getType().equals(PROJECT_GROUP_ID)) {
                throw new IllegalArgumentException(groupQuery.getType().name() + " != " + PROJECT_GROUP_ID.name());
            }
            return new Query[] {groupQuery, new Query(PROJECT_ARTIFACT_ID, artifactId)};
        }

    }

}
