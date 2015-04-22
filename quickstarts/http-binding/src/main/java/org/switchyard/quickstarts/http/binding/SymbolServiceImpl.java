/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
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
package org.switchyard.quickstarts.http.binding;

import javax.inject.Inject;

import org.switchyard.Context;
import org.switchyard.Property;
import org.switchyard.Scope;
import org.switchyard.component.bean.Service;
import org.switchyard.component.common.label.EndpointLabel;
import org.switchyard.component.http.composer.HttpComposition;
import org.switchyard.component.http.composer.HttpRequestInfo;

/**
 * A SymbolService implementation.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
@Service(SymbolService.class)
public class SymbolServiceImpl implements SymbolService {

    @Inject
    private Context context;

    public String getSymbol(String companyName) {
        String symbol = "";
        if (companyName.equals("headers")) {
            StringBuffer headers = new StringBuffer();
            for (Property property : context.getProperties(Scope.MESSAGE)) {
                if (property.hasLabel(EndpointLabel.HTTP.label()) && (property.getValue() instanceof String)) {
                    headers.append(property.getName());
                    headers.append("=");
                    headers.append(property.getValue());
                }
            }
            return headers.toString();
        }
        if (companyName.equals("requestInfo")) {
            Property prop = context.getProperty(HttpComposition.HTTP_REQUEST_INFO);
            return ((HttpRequestInfo) prop.getValue()).toString();
        }

        // Note the property becomes lower cased when executed on AS7
        Property prop = context.getProperty("content-type");
        if (prop == null) {
            prop = context.getProperty("Content-type");
        }
        if (prop == null) {
            prop = context.getProperty("Content-Type");
        }
        String contentType = (prop == null) ? null : (String) prop.getValue();
        if (contentType != null) {
            if (contentType.contains("text/plain")) {
                if (companyName.equalsIgnoreCase("vineyard")) {
                    symbol = "WINE";
                }
            }
        }
        // TODO: Currently not possible to set property on return path for CDI Beans
        /*if (symbol.equals("")) {
            context.setProperty(HttpContextMapper.HTTP_RESPONSE_STATUS, 404).addLabels(new String[]{EndpointLabel.HTTP.label()});
        }*/
        return symbol;
    }
}
