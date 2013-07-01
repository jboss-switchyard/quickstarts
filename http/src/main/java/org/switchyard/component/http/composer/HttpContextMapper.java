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
package org.switchyard.component.http.composer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.switchyard.Context;
import org.switchyard.Property;
import org.switchyard.component.common.composer.BaseRegexContextMapper;
import org.switchyard.component.common.label.ComponentLabel;
import org.switchyard.component.common.label.EndpointLabel;

/**
 * HttpContextMapper.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> &copy; 2012 Red Hat Inc.
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public class HttpContextMapper extends BaseRegexContextMapper<HttpBindingData> {

    /**
     * The HTTP responce code.
     */
    public static final String HTTP_RESPONSE_STATUS = "http_response_status";

    private static final String[] HTTP_LABELS = new String[]{ComponentLabel.HTTP.label(), EndpointLabel.HTTP.label()};

    /**
     * {@inheritDoc}
     */
    @Override
    public void mapFrom(HttpBindingData source, Context context) throws Exception {
        if (source instanceof HttpResponseBindingData) {
            HttpResponseBindingData response = (HttpResponseBindingData) source;
            context.setProperty(HTTP_RESPONSE_STATUS, response.getStatus()).addLabels(HTTP_LABELS);
        } else {
            HttpRequestBindingData request = (HttpRequestBindingData) source;
            if (request.getRequestInfo() != null) {
                context.setProperty(HttpComposition.HTTP_REQUEST_INFO, request.getRequestInfo()).addLabels(HTTP_LABELS);
            }
        }
        Iterator<Map.Entry<String, List<String>>> entries = source.getHeaders().entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, List<String>> entry = entries.next();
            String name = entry.getKey();
            if (matches(name)) {
                List<String> values = entry.getValue();
                if ((values != null) && (values.size() == 1)) {
                    context.setProperty(name, values.get(0)).addLabels(HTTP_LABELS);
                } else if ((values != null) && (values.size() > 1)) {
                    context.setProperty(name, values).addLabels(HTTP_LABELS);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public void mapTo(Context context, HttpBindingData target) throws Exception {
        Map<String, List<String>> httpHeaders = target.getHeaders();
        for (Property property : context.getProperties()) {
            if (property.hasLabel(EndpointLabel.HTTP.label())) {
                String name = property.getName();
                Object value = property.getValue();
                if (HTTP_RESPONSE_STATUS.equalsIgnoreCase(name) && (target instanceof HttpResponseBindingData)) {
                    HttpResponseBindingData response = (HttpResponseBindingData)target;
                    if (value instanceof String) {
                        response.setStatus(Integer.valueOf((String) value).intValue());
                    } else if (value instanceof Integer) {
                        response.setStatus((Integer) value);
                    }
                } else if (matches(name)) {
                    if (value != null) {
                        if (value instanceof List) {
                            httpHeaders.put(name, (List<String>)value);
                        } else if (value instanceof String) {
                            List<String> list = new ArrayList<String>();
                            list.add(String.valueOf(value));
                            httpHeaders.put(name, list);
                        }
                    }
                }
            }
        }
    }

}
