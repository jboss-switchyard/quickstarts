/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.switchyard.component.common.knowledge.config.builder;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.kie.api.KieServices;
import org.kie.api.io.KieResources;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
// SWITCHYARD-1755: internal builder class usage still required (public APIs insufficient)
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.DecisionTableInputType;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.switchyard.common.io.resource.ResourceDetail;
import org.switchyard.common.lang.Strings;
import org.switchyard.component.common.knowledge.config.model.KnowledgeComponentImplementationModel;
import org.switchyard.component.common.knowledge.config.model.ManifestModel;
import org.switchyard.config.model.resource.ResourceModel;
import org.switchyard.config.model.resource.ResourcesModel;

/**
 * ResourceBuilder.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
public class ResourceBuilder extends KnowledgeBuilder {

    private final KieResources _kieResources;
    private URL _url;
    private ResourceType _resourceType;
    private ResourceConfiguration _resourceConfiguration;

    /**
     * Creates a new ResourceBuilder.
     * @param classLoader classLoader
     * @param resourceModel resourceModel
     */
    public ResourceBuilder(ClassLoader classLoader, ResourceModel resourceModel) {
        super(classLoader);
        _kieResources = KieServices.Factory.get().getResources();
        if (resourceModel != null) {
            _url = resourceModel.getLocationURL(getClassLoader());
            _resourceType = convertResourceType(resourceModel.getType());
            ResourceDetail syResourceDetail = resourceModel.getDetail();
            if (syResourceDetail != null) {
                if (ResourceType.DTABLE.equals(_resourceType)) {
                    String inputType = getInputType(syResourceDetail, DecisionTableInputType.XLS.toString());
                    DecisionTableConfiguration dtc = KnowledgeBuilderFactory.newDecisionTableConfiguration();
                    dtc.setInputType(DecisionTableInputType.valueOf(inputType));
                    dtc.setWorksheetName(getWorksheetName(syResourceDetail));
                    //dtc.setUsingExternalTypes(syResourceDetail.isUsingExternalTypes());
                    _resourceConfiguration = dtc;
                }
                /* SWITCHYARD-1662
                else if (ResourceType.SCARD.equals(_resourceType)) {
                    String inputType = getInputType(syResourceDetail, ScoreCardConfiguration.SCORECARD_INPUT_TYPE.EXCEL.name());
                    if ("XLS".equals(inputType)) {
                        inputType = ScoreCardConfiguration.SCORECARD_INPUT_TYPE.EXCEL.name();
                    }
                    ScoreCardConfiguration scc = KnowledgeBuilderFactory.newScoreCardConfiguration();
                    scc.setInputType(ScoreCardConfiguration.SCORECARD_INPUT_TYPE.valueOf(inputType));
                    scc.setWorksheetName(getWorksheetName(syResourceDetail));
                    //scc.setUsingExternalTypes(syResourceDetail.isUsingExternalTypes());
                    _resourceConfiguration = scc;
                }
                */
            }
        }
    }

    /**
     * Builds a Resource.
     * @return a Resource
     */
    public Resource build() {
        Resource resource = null;
        if (_url != null) {
            resource = _kieResources.newUrlResource(_url);
            if (resource != null) {
                if (_resourceType != null) {
                    resource.setResourceType(_resourceType);
                }
                if (_resourceConfiguration != null) {
                    resource.setConfiguration(_resourceConfiguration);
                }
            }
        }
        return resource;
    }

    private String getInputType(ResourceDetail syResourceDetail, String defaultInputType) {
        String inputType = Strings.trimToNull(syResourceDetail.getInputType());
        if (inputType != null) {
            inputType = inputType.toUpperCase();
        } else {
            inputType = defaultInputType;
        }
        return inputType;
    }

    private String getWorksheetName(ResourceDetail syResourceDetail) {
        String worksheetName = syResourceDetail.getWorksheetName();
        if (worksheetName == null) {
            worksheetName = "";
        }
        return worksheetName;
    }

    private ResourceType convertResourceType(org.switchyard.common.io.resource.ResourceType syResourceType) {
        if (syResourceType != null) {
            String resourceTypeName = syResourceType.getName();
            if ("BPMN".equals(resourceTypeName)) {
                // Drools ResourceType recognizes BPMN2, not BPMN
                resourceTypeName = ResourceType.BPMN2.getName();
            } else if ("XLS".equals(resourceTypeName) || "CSV".equals(resourceTypeName)) {
                // Drools ResourceType recognizes DTABLE, not XLS or CSV
                resourceTypeName = ResourceType.DTABLE.getName();
            }
            return ResourceType.getResourceType(resourceTypeName);
        }
        return null;
    }

    /**
     * Creates ResourceBuilders.
     * @param classLoader classLoader
     * @param implementationModel implementationModel
     * @return ResourceBuilders
     */
    public static List<ResourceBuilder> builders(ClassLoader classLoader, KnowledgeComponentImplementationModel implementationModel) {
        List<ResourceBuilder> builders = new ArrayList<ResourceBuilder>();
        if (implementationModel != null) {
            ManifestModel manifestModel = implementationModel.getManifest();
            if (manifestModel != null) {
                ResourcesModel resourcesModel = manifestModel.getResources();
                builders.addAll(builders(classLoader, resourcesModel));
            }
        }
        return builders;
    }

    /**
     * Creates ResourceBuilders.
     * @param classLoader classLoader
     * @param resourcesModel resourcesModel
     * @return ResourceBuilders
     */
    public static List<ResourceBuilder> builders(ClassLoader classLoader, ResourcesModel resourcesModel) {
        List<ResourceBuilder> builders = new ArrayList<ResourceBuilder>();
        if (resourcesModel != null) {
            for (ResourceModel resourceModel : resourcesModel.getResources()) {
                builders.add(new ResourceBuilder(classLoader, resourceModel));
            }
        }
        return builders;
    }

}
