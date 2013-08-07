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
package org.switchyard.component.common.knowledge.util;

import java.net.URL;

import org.kie.api.KieServices;
import org.kie.api.io.KieResources;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.DecisionTableInputType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.ScoreCardConfiguration;
import org.switchyard.common.io.resource.Resource;
import org.switchyard.common.io.resource.ResourceDetail;
import org.switchyard.common.io.resource.ResourceType;
import org.switchyard.common.lang.Strings;
import org.switchyard.component.common.knowledge.config.model.KnowledgeComponentImplementationModel;
import org.switchyard.component.common.knowledge.config.model.ManifestModel;
import org.switchyard.config.model.resource.ResourcesModel;

/**
 * Resource functions.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public final class Resources {

    /**
     * Installs resource types.
     * @param loader the class loader
     */
    public static void installTypes(ClassLoader loader) {
        ResourceType.install(loader);
    }

    /**
     * Adds resources.
     * @param model the model
     * @param loader the class loader
     * @param builder the knowledge builder
     */
    public static void addResources(KnowledgeComponentImplementationModel model, ClassLoader loader, KnowledgeBuilder builder) {
        ManifestModel manifestModel = model.getManifest();
        if (manifestModel != null) {
            ResourcesModel resourcesModel = manifestModel.getResources();
            if (resourcesModel != null) {
                KieResources kieResources = KieServices.Factory.get().getResources();
                for (Resource syResource : resourcesModel.getResources()) {
                    URL locationURL = syResource.getLocationURL(loader);
                    if (locationURL != null) {
                        org.kie.api.io.Resource kieResource = kieResources.newUrlResource(locationURL);
                        if (kieResource != null) {
                            org.kie.api.io.ResourceType kieResourceType = convertResourceType(syResource.getType());
                            if (kieResourceType != null) {
                                org.kie.api.io.ResourceConfiguration kieResourceConfiguration = null;
                                ResourceDetail syResourceDetail = syResource.getDetail();
                                if (syResourceDetail != null) {
                                    if (org.kie.api.io.ResourceType.DTABLE.equals(kieResourceType)) {
                                        DecisionTableConfiguration dtc = KnowledgeBuilderFactory.newDecisionTableConfiguration();
                                        String inputType = getInputType(syResourceDetail);
                                        if (inputType != null) {
                                            dtc.setInputType(DecisionTableInputType.valueOf(inputType));
                                        }
                                        dtc.setWorksheetName(syResourceDetail.getWorksheetName());
                                        kieResourceConfiguration = dtc;
                                    } else if (org.kie.api.io.ResourceType.SCARD.equals(kieResourceType)) {
                                        ScoreCardConfiguration scc = KnowledgeBuilderFactory.newScoreCardConfiguration();
                                        String inputType = getInputType(syResourceDetail);
                                        if (inputType != null) {
                                            if ("XLS".equals(inputType)) {
                                                inputType = ScoreCardConfiguration.SCORECARD_INPUT_TYPE.EXCEL.name();
                                            }
                                            scc.setInputType(ScoreCardConfiguration.SCORECARD_INPUT_TYPE.valueOf(inputType));
                                        }
                                        scc.setWorksheetName(syResourceDetail.getWorksheetName());
                                        scc.setUsingExternalTypes(syResourceDetail.isUsingExternalTypes());
                                        kieResourceConfiguration = scc;
                                    }
                                }
                                builder.add(kieResource, kieResourceType, kieResourceConfiguration);
                            }
                        }
                    }
                }
            }
        }
    }

    private static String getInputType(ResourceDetail syResourceDetail) {
        String inputType = Strings.trimToNull(syResourceDetail.getInputType());
        if (inputType != null) {
            inputType = inputType.toUpperCase();
        }
        return inputType;
    }

    /**
     * Converts a resource type.
     * @param syResourceType the SwitchYard resource type.
     * @return the KIE resource type
     */
    public static org.kie.api.io.ResourceType convertResourceType(ResourceType syResourceType) {
        if (syResourceType != null) {
            String resourceTypeName = syResourceType.getName();
            if ("BPMN".equals(resourceTypeName)) {
                // Drools ResourceType recognizes BPMN2, not BPMN
                resourceTypeName = org.kie.api.io.ResourceType.BPMN2.getName();
            } else if ("XLS".equals(resourceTypeName) || "CSV".equals(resourceTypeName)) {
                // Drools ResourceType recognizes DTABLE, not XLS or CSV
                resourceTypeName = org.kie.api.io.ResourceType.DTABLE.getName();
            }
            return org.kie.api.io.ResourceType.getResourceType(resourceTypeName);
        }
        return null;
    }

    private Resources() {}

}
