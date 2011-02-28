/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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
     * @param <M> the type of Model being merged
     * @param fromModel merge from this model, optionally overriding anything in toModel
     * @param toModel merge into a copy of this model
     * @param fromOverridesTo whether fromModel attributes/values should override those in toModel
     * @return the newly merged model
     */
    public static <M extends Model> M merge(M fromModel, M toModel, boolean fromOverridesTo) {
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
        return merged_model;
    }

}
