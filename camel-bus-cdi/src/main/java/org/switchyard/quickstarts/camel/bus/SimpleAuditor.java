/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.switchyard.quickstarts.camel.bus;

import javax.inject.Named;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;
import org.switchyard.bus.camel.audit.Auditor;
import org.switchyard.bus.camel.processors.Processors;

/**
 * Auditor surrounding all SY processors.
 */
@Named("simple auditor")
public class SimpleAuditor implements Auditor {

    private Logger _logger = Logger.getLogger(SimpleAuditor.class);

    @Override
    public void beforeCall(Processors processor, Exchange exchange) {
        exchange.setProperty("time", System.currentTimeMillis());
    }

    @Override
    public void afterCall(Processors processor, Exchange exchange) {
        long time = System.currentTimeMillis() - exchange.getProperty("time", 0, Long.class);
        _logger.info("Step " + processor.name() + " took " + time + "ms");
    }

}
