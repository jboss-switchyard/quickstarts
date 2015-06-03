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
package org.switchyard.quickstarts.camel.bus;

import javax.inject.Named;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;
import org.switchyard.bus.camel.audit.Audit;
import org.switchyard.bus.camel.audit.Auditor;
import org.switchyard.bus.camel.processors.Processors;

/**
 * Simple auditor which is called before/after provider handler.
 */
@Audit(Processors.PROVIDER_CALLBACK)
@Named("selective auditor")
public class SelectiveAuditor implements Auditor {

    private Logger _logger = Logger.getLogger(SelectiveAuditor.class);

    @Override
    public void beforeCall(Processors processor, Exchange exchange) {
        _logger.info("Calling provider with message " + exchange.getIn(String.class));
    }

    @Override
    public void afterCall(Processors processor, Exchange exchange) {
        _logger.info("Provider respond with message " + exchange.getIn(String.class));
    }

}
