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
import org.kie.api.builder.KieFileSystem;
import org.kie.api.io.KieResources;
// SWITCHYARD-1755: internal builder class usage still required (public APIs insufficient)
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.DecisionTableInputType;
import org.kie.internal.builder.KnowledgeBuilderFactory;
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
     * @param fileSystem the kie file system
     */
    public static void addResources(KnowledgeComponentImplementationModel model, ClassLoader loader, KieFileSystem fileSystem) {
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
                                kieResource.setResourceType(kieResourceType);
                                ResourceDetail syResourceDetail = syResource.getDetail();
                                if (syResourceDetail != null) {
                                    org.kie.api.io.ResourceConfiguration kieResourceConfiguration = null;
                                    if (org.kie.api.io.ResourceType.DTABLE.equals(kieResourceType)) {
                                        String inputType = getInputType(syResourceDetail, DecisionTableInputType.XLS.toString());
                                        DecisionTableConfiguration dtc = KnowledgeBuilderFactory.newDecisionTableConfiguration();
                                        dtc.setInputType(DecisionTableInputType.valueOf(inputType));
                                        dtc.setWorksheetName(getWorksheetName(syResourceDetail));
                                        //dtc.setUsingExternalTypes(syResourceDetail.isUsingExternalTypes());
                                        kieResourceConfiguration = dtc;
                                    }
                                    /* SWITCHYARD-1662
                                    else if (org.kie.api.io.ResourceType.SCARD.equals(kieResourceType)) {
                                        String inputType = getInputType(syResourceDetail, ScoreCardConfiguration.SCORECARD_INPUT_TYPE.EXCEL.name());
                                        if ("XLS".equals(inputType)) {
                                            inputType = ScoreCardConfiguration.SCORECARD_INPUT_TYPE.EXCEL.name();
                                        }
                                        ScoreCardConfiguration scc = KnowledgeBuilderFactory.newScoreCardConfiguration();
                                        scc.setInputType(ScoreCardConfiguration.SCORECARD_INPUT_TYPE.valueOf(inputType));
                                        scc.setWorksheetName(getWorksheetName(syResourceDetail));
                                        //scc.setUsingExternalTypes(syResourceDetail.isUsingExternalTypes());
                                        kieResourceConfiguration = scc;
                                    }
                                    */
                                    if (kieResourceConfiguration != null) {
                                        kieResource.setConfiguration(kieResourceConfiguration);
                                    }
                                }
                            }
                            fileSystem.write(kieResource);
                        }
                    }
                }
            }
        }
    }

    private static String getInputType(ResourceDetail syResourceDetail, String defaultInputType) {
        String inputType = Strings.trimToNull(syResourceDetail.getInputType());
        if (inputType != null) {
            inputType = inputType.toUpperCase();
        } else {
            inputType = defaultInputType;
        }
        return inputType;
    }

    private static String getWorksheetName(ResourceDetail syResourceDetail) {
        String worksheetName = syResourceDetail.getWorksheetName();
        if (worksheetName == null) {
            worksheetName = "";
        }
        return worksheetName;
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
