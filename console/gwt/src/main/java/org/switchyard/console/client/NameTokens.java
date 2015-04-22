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

    /** The SwitchYard subsystem name. */
    public static final String SUBSYSTEM = "switchyard"; //$NON-NLS-1$

    /** The subpath for the SwitchYard system configuration view. */
    public static final String SYSTEM_CONFIG_PRESENTER = "switchyard"; //$NON-NLS-1$
    /** The subpath for the SwitchYard applications view. */
    public static final String APPLICATIONS_PRESENTER = "sy-apps"; //$NON-NLS-1$
    /** The subpath for the SwitchYard artifacts view. */
    public static final String ARTIFACTS_PRESENTER = "sy-artifacts"; //$NON-NLS-1$
    /** The subpath for the SwitchYard services view. */
    public static final String SERVICES_PRESENTER = "sy-services"; //$NON-NLS-1$
    /** The subpath for the SwitchYard references view. */
    public static final String REFERENCES_PRESENTER = "sy-references"; //$NON-NLS-1$
    /** The subpath for the SwitchYard message metrics view. */
    public static final String METRICS_PRESENTER = "sy-metrics"; //$NON-NLS-1$
    /** The subpath for the SwitchYard runtime view. */
    public static final String RUNTIME_PRESENTER = "switchyard-runtime"; //$NON-NLS-1$
    /** The subpath for the SwitchYard runtime operations view. */
    public static final String RUNTIME_OPERATIONS_PRESENTER = APPLICATIONS_PRESENTER;

    /** The parameter name used for component name. */
    public static final String COMPONENT_NAME_PARAM = "component"; //$NON-NLS-1$
    /** The parameter name used for application name. */
    public static final String APPLICATION_NAME_PARAM = "application"; //$NON-NLS-1$
    /** The parameter name used for artifact reference key. */
    public static final String ARTIFACT_REFERENCE_KEY_PARAM = "artifactKey"; //$NON-NLS-1$
    /** The parameter name used for service name. */
    public static final String SERVICE_NAME_PARAM = "service"; //$NON-NLS-1$
    /** The parameter name used for reference name. */
    public static final String REFERENCE_NAME_PARAM = "reference"; //$NON-NLS-1$

    /** The display text for the SwitchYard system configuration navigator item. */
    public static final String SYSTEM_CONFIG_TEXT = Singleton.MESSAGES.label_runtimeDetails();
    /** The display text for the SwitchYard applications navigator item. */
    public static final String APPLICATIONS_TEXT = Singleton.MESSAGES.label_applications();
    /** The display text for the SwitchYard artifact references navigator item. */
    public static final String ARTIFACT_REFERENCES_TEXT = Singleton.MESSAGES.label_artifacts();
    /** The display text for the SwitchYard services navigator item. */
    public static final String SERVICES_TEXT = Singleton.MESSAGES.label_services();
    /** The display text for the SwitchYard services navigator item. */
    public static final String REFERENCES_TEXT = Singleton.MESSAGES.label_references();
    /** The display text for the SwitchYard runtime navigator item. */
    public static final String RUNTIME_TEXT = "SwitchYard"; //$NON-NLS-1$

    /** The "category" ID for the subsystems tree. */
    public static final String SUBSYSTEM_TREE_CATEGORY = "profiles"; //$NON-NLS-1$
    /** The "category" ID for the domain runtime tree. */
    public static final String SUBSYSTEM_DOMAIN_TREE_CATEGORY = "domain-runtime"; //$NON-NLS-1$
    /** The "category" ID for the standalone runtime tree. */
    public static final String SUBSYSTEM_STANDALONE_TREE_CATEGORY = "standalone-runtime"; //$NON-NLS-1$

    /**
     * @param name a string representation of a javax.xml.namespace.QName
     * @return the QName components, {namespace,local}
     */
    public static String[] parseQName(String name) {
        if (name == null) {
            return new String[] {"", "" }; //$NON-NLS-1$ //$NON-NLS-2$
        }
        int namespaceEnd = name.indexOf('}');
        if (namespaceEnd > 0) {
            return new String[] {name.substring(1, namespaceEnd), name.substring(namespaceEnd + 1) };
        }
        return new String[] {"", name }; //$NON-NLS-1$
    }

}
