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

import org.apache.camel.Exchange;
import org.apache.camel.component.cxf.common.message.CxfConstants;
import org.switchyard.component.camel.composer.CamelBindingData;
import org.switchyard.component.common.composer.MessageComposer;
import org.switchyard.component.common.rest.RsMethodUtil;

/**
 * A cxfrs Camel Processor that uses HTTP client api. Sets the method based
 * on body contents. This is useful for simple urls of the form
 *
 * cxfrs://<host>:<port>/[<context-path>]/<path-to-method>
 *
 * If there is no body content, the HTTP method is assumed to be GET and POST otherwise.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2012 Red Hat Inc.
 */
public class CxfRsHttpProcessor extends DefaultProcessor {

    /**
     * Create a CxfRsHttpProcessor that handles cxfrs http outbound routes.
     *
     * @param composer the message composer to be used
     * @param exchange the switchayrd exchange
     */
    public CxfRsHttpProcessor(MessageComposer<CamelBindingData> composer, org.switchyard.Exchange exchange) {
        super(composer, exchange);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(Exchange camelExchange) throws Exception {
        if (getExchange().getMessage().getContent() != null) {
            camelExchange.getIn().setHeader(Exchange.HTTP_METHOD, RsMethodUtil.HTTP_POST_METHOD);
        } else {
            camelExchange.getIn().setHeader(Exchange.HTTP_METHOD, RsMethodUtil.HTTP_GET_METHOD);
        }
        camelExchange.getIn().setHeader(CxfConstants.CAMEL_CXF_RS_RESPONSE_CLASS, String.class);
        camelExchange.getIn().setHeader(CxfConstants.CAMEL_CXF_RS_USING_HTTP_API, Boolean.TRUE);
        super.process(camelExchange);
    }
}
