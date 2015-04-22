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
package org.switchyard.component.camel.common.composer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.switchyard.Context;
import org.switchyard.Property;
import org.switchyard.Scope;
import org.switchyard.common.camel.ContextPropertyUtil;
import org.switchyard.component.common.composer.BaseRegexContextMapper;
import org.switchyard.component.common.label.ComponentLabel;
import org.switchyard.component.common.label.EndpointLabel;
import org.switchyard.config.model.composer.ContextMapperModel;
import org.switchyard.config.model.composite.BindingModel;

/**
 * CamelContextMapper.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; (C) 2011 Red Hat Inc.
 */
public class CamelContextMapper extends BaseRegexContextMapper<CamelBindingData> {

    private String[] _camelLabels = null;

    private String[] getCamelLabels() {
        if (_camelLabels == null) {
            List<String> list = new ArrayList<String>();
            list.add(ComponentLabel.CAMEL.label());
            ContextMapperModel cm_model = getModel();
            if (cm_model != null) {
                BindingModel b_model = cm_model.getBindingModel();
                if (b_model != null) {
                    String e_label = EndpointLabel.toLabel(b_model.getType());
                    if (e_label != null) {
                        list.add(e_label);
                    }
                }
            }
            _camelLabels = list.toArray(new String[list.size()]);
        }
        return _camelLabels;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mapFrom(CamelBindingData source, Context context) throws Exception {
        Message message = source.getMessage();
        Exchange exchange = message.getExchange();

        for (Map.Entry<String,Object> header : message.getHeaders().entrySet()) {
            String name = header.getKey();
            if (matches(name) && !ContextPropertyUtil.isReservedProperty(name, Scope.MESSAGE)) {
                Object value = header.getValue();
                if (value != null) {
                    context.setProperty(name, value, Scope.MESSAGE).addLabels(getCamelLabels());
                }
            }
        }
        if (exchange != null) {
            for (Map.Entry<String,Object> property : exchange.getProperties().entrySet()) {
                String name = property.getKey();
                if (matches(name) && !ContextPropertyUtil.isReservedProperty(name, Scope.EXCHANGE)) {
                    Object value = property.getValue();
                    if (value != null) {
                        context.setProperty(name, value, Scope.EXCHANGE).addLabels(getCamelLabels());
                    }
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void mapTo(Context context, CamelBindingData target) throws Exception {
        Message message = target.getMessage();
        Exchange exchange = message.getExchange();

        for (Property property : context.getProperties(Scope.MESSAGE)) {
            String name = property.getName();
            if (matches(name) && !ContextPropertyUtil.isReservedProperty(name, Scope.MESSAGE)) {
                Object value = property.getValue();
                if (value != null) {
                    message.setHeader(name, value);
                }
            }
        }
        if (exchange != null) {
            for (Property property : context.getProperties(Scope.EXCHANGE)) {
                String name = property.getName();
                if (matches(name) && !ContextPropertyUtil.isReservedProperty(name, Scope.EXCHANGE)) {
                    Object value = property.getValue();
                    if (value != null) {
                        exchange.setProperty(name, value);
                    }
                }
            }
        }
    }

    @Override
    public void setModel(ContextMapperModel model) {
        super.setModel(model);
        // reinitialize camel labels. prevent accessing the model outside of initialization.
        _camelLabels = null;
        getCamelLabels();
    }

}
