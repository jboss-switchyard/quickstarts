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
package org.switchyard.config.model;

import org.switchyard.config.Configuration;
import org.switchyard.config.Configurations;

/**
 * Utility class with helper methods dealing with Models.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public final class Models {

    private Models() {}

    /**
     * Merges two models into a new model.
     * Note: The act of merging results in fromModel and toModel to have their configurations normalized and children ordered!
     * @param <M> the type of Model being merged
     * @param fromModel merge from this model, overriding anything in toModel
     * @param toModel merge into a copy of this model
     * @return the newly merged model
     */
    public static <M extends Model> M merge(M fromModel, M toModel) {
        return merge(fromModel, toModel, true);
    }

    /**
     * Merges two models into a new model.
     * Note: The act of merging results in fromModel and toModel to have their configurations normalized and children ordered!
     * @param <M> the type of Model being merged
     * @param fromModel merge from this model, optionally overriding anything in toModel
     * @param toModel merge into a copy of this model
     * @param fromOverridesTo whether fromModel attributes/values should override those in toModel
     * @return the newly merged model
     */
    public static <M extends Model> M merge(M fromModel, M toModel, boolean fromOverridesTo) {
        return merge(fromModel, toModel, fromOverridesTo, false);
    }

    /**
     * Merges two models into a new model.
     * Note: The act of merging results in fromModel and toModel to have their configurations normalized and children ordered!
     * @param <M> the type of Model being merged
     * @param fromModel merge from this model, optionally overriding anything in toModel
     * @param toModel merge into a copy of this model
     * @param fromOverridesTo whether fromModel attributes/values should override those in toModel
     * @param validate whether the newly merged model should be validated before it is returned
     * @return the newly merged model
     */
    public static <M extends Model> M merge(M fromModel, M toModel, boolean fromOverridesTo, boolean validate) {
        String from_model_cn = fromModel.getClass().getName();
        String to_model_cn = toModel.getClass().getName();
        if (!from_model_cn.equals(to_model_cn)) {
            throw new IllegalArgumentException(from_model_cn + " != " + to_model_cn);
        }
        Configuration from_model_config = fromModel.getModelConfiguration();
        Configuration to_model_config = toModel.getModelConfiguration();
        Configuration merged_model_config = Configurations.merge(from_model_config, to_model_config, fromOverridesTo);
        @SuppressWarnings("unchecked")
        M merged_model = (M)Descriptor.getMarshaller(toModel).read(merged_model_config);
        merged_model.orderModelChildren();
        if (validate) {
            merged_model.assertModelValid();
        }
        return merged_model;
    }

}
