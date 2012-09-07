/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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

package org.switchyard.component.camel.processor;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.switchyard.component.camel.CamelConstants;
import org.switchyard.component.camel.composer.CamelBindingData;
import org.switchyard.component.common.composer.MessageComposer;
import org.switchyard.component.common.rest.RsMethod;
import org.switchyard.component.common.rest.RsMethodUtil;

/**
 * A cxfrs Camel Processor that uses HTTP client api. The HTTP method is set directly
 * from SwitchYard exchange. This is useful for urls of the form
 *
 * cxfrs://<host>:<port>/[<context-path>]?rescourceClasses=...
 *
 * The resourceClasses parameter will be used to determine the apporiate path, method and response type.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class CxfRsHttpDynamicProcessor extends DefaultProcessor {

    private Map<String, RsMethod> _resourcePaths;

    /**
     * Create a CxfRsHttpDynamicProcessor that handles cxfrs http outbound routes that are dynamic.
     *
     * @param composer the message composer to be used
     * @param exchange the switchayrd exchange
     * @param uri the camel cxfrs endpoint uri
     */
    public CxfRsHttpDynamicProcessor(MessageComposer<CamelBindingData> composer, org.switchyard.Exchange exchange, String uri) {
        super(composer, exchange);
        String resourceClasses = uri.split(CamelConstants.RESOURCE_CLASSES)[1].split("&")[0];
        _resourcePaths = RsMethodUtil.parseResources(resourceClasses);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(Exchange camelExchange) throws Exception {
        final RsMethod restMethod = _resourcePaths.get(getExchange().getContract().getProviderOperation().toString());
        if (restMethod == null) {
            throw new RuntimeException("Could not map " + getExchange().getContract().getProviderOperation().getName() + " to any REST method.");
        }
        camelExchange.getIn().setHeader(CxfConstants.CAMEL_CXF_RS_USING_HTTP_API, Boolean.TRUE);
        camelExchange.getIn().setHeader(CxfConstants.CAMEL_CXF_RS_RESPONSE_CLASS, restMethod.getResponseType());
        camelExchange.getIn().setHeader(Exchange.HTTP_METHOD, restMethod.getMethod());

        String path = RsMethodUtil.getPath(restMethod, getExchange());
        camelExchange.getIn().setHeader(Exchange.HTTP_PATH, path);
        super.process(camelExchange);
    }
}
