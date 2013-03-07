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
package org.switchyard.component.camel.common.composer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.switchyard.Context;
import org.switchyard.Property;
import org.switchyard.Scope;
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
        Scope scope;
        if (exchange.getIn() == message) {
            scope = Scope.IN;
        } else {
            scope = Scope.OUT;
        }
        for (Map.Entry<String,Object> header : message.getHeaders().entrySet()) {
            String name = header.getKey();
            if (matches(name)) {
                Object value = header.getValue();
                if (value != null) {
                    context.setProperty(name, value, scope).addLabels(getCamelLabels());
                }
            }
        }
        if (exchange != null) {
            for (Map.Entry<String,Object> property : exchange.getProperties().entrySet()) {
                String name = property.getKey();
                if (matches(name)) {
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
        Scope scope;
        if (exchange.getIn() == message) {
            scope = Scope.IN;
        } else {
            scope = Scope.OUT;
        }
        for (Property property : context.getProperties(scope)) {
            String name = property.getName();
            if (matches(name)) {
                Object value = property.getValue();
                if (value != null) {
                    message.setHeader(name, value);
                }
            }
        }
        if (exchange != null) {
            for (Property property : context.getProperties(Scope.EXCHANGE)) {
                String name = property.getName();
                if (matches(name)) {
                    Object value = property.getValue();
                    if (value != null) {
                        exchange.setProperty(name, value);
                    }
                }
            }
        }
    }

}
