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

package org.switchyard.console.client;

/**
 * NameTokens
 * 
 * SwitchYard specific path tokens.
 * 
 * @author Rob Cernich
 */
public final class NameTokens {

    private NameTokens() {
    }

    /** The subpath for the SwitchYard system configuration view. */
    public static final String SYSTEM_CONFIG_PRESENTER = "switchyard";
    /** The subpath for the SwitchYard applications view. */
    public static final String APPLICATIONS_PRESENTER = "sy-apps";
    /** The subpath for the SwitchYard artifacts view. */
    public static final String ARTIFACTS_PRESENTER = "sy-artifacts";
    /** The subpath for the SwitchYard services view. */
    public static final String SERVICES_PRESENTER = "sy-services";
    /** The subpath for the SwitchYard runtime view. */
    public static final String RUNTIME_PRESENTER = "sy-runtime";

    /** The parameter name used for component name. */
    public static final String COMPONENT_NAME_PARAM = "component";
    /** The parameter name used for application name. */
    public static final String APPLICATION_NAME_PARAM = "application";
    /** The parameter name used for artifact reference key. */
    public static final String ARTIFACT_REFERENCE_KEY_PARAM = "artifactKey";
    /** The parameter name used for service name. */
    public static final String SERVICE_NAME_PARAM = "service";

    /** The display text for the SwitchYard system configuration navigator item. */
    public static final String SYSTEM_CONFIG_TEXT = "Runtime Details";
    /** The display text for the SwitchYard applications navigator item. */
    public static final String APPLICATIONS_TEXT = "Applications";
    /** The display text for the SwitchYard artifact references navigator item. */
    public static final String ARTIFACT_REFERENCES_TEXT = "Artifact References";
    /** The display text for the SwitchYard services navigator item. */
    public static final String SERVICES_TEXT = "Services";
    /** The display text for the SwitchYard runtime navigator item. */
    public static final String RUNTIME_TEXT = "SwitchYard";

    /** The "category" ID for the subsystems tree. */
    public static final String SUBSYSTEM_TREE_CATEGORY = "profiles";
    /** The "category" ID for the domain runtime tree. */
    public static final String SUBSYSTEM_DOMAIN_TREE_CATEGORY = "domain-runtime";
    /** The "category" ID for the standalone runtime tree. */
    public static final String SUBSYSTEM_STANDALONE_TREE_CATEGORY = "standalone-runtime";

    /**
     * @param name a string representation of a javax.xml.namespace.QName
     * @return the QName components, {namespace,local}
     */
    public static String[] parseQName(String name) {
        if (name == null) {
            return new String[] {"", "" };
        }
        int namespaceEnd = name.indexOf('}');
        if (namespaceEnd > 0) {
            return new String[] {name.substring(1, namespaceEnd), name.substring(namespaceEnd + 1) };
        }
        return new String[] {"", name };
    }

}
