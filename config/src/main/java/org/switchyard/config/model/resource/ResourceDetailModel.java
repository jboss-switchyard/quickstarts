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
package org.switchyard.config.model.resource;

import org.switchyard.common.io.resource.ResourceDetail;
import org.switchyard.config.model.Model;

/**
 * A detail model for a Resource.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public interface ResourceDetailModel extends ResourceDetail, Model {

    /**
     * The resourceDetail XML element.
     */
    public static final String RESOURCE_DETAIL = "resourceDetail";

    /**
     * Sets the input type.
     * @param inputType the input type
     * @return this ResourceDetailModel (useful for chaining)
     */
    public ResourceDetailModel setInputType(String inputType);

    /**
     * Sets the worksheet name.
     * @param worksheetName the worksheet name
     * @return this ResourceDetailModel (useful for chaining)
     */
    public ResourceDetailModel setWorksheetName(String worksheetName);

    /**
     * Sets whether to use external types.
     * @param usingExternalTypes if using external types
     * @return this ResourceDetailModel (useful for chaining)
     */
    public ResourceDetailModel setUsingExternalTypes(boolean usingExternalTypes);

}
