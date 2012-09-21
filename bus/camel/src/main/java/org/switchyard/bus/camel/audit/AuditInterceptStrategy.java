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
package org.switchyard.bus.camel.audit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.camel.CamelContext;
import org.apache.camel.Processor;
import org.apache.camel.model.ProcessDefinition;
import org.apache.camel.model.ProcessorDefinition;
import org.apache.camel.processor.Pipeline;
import org.apache.camel.spi.InterceptStrategy;
import org.apache.log4j.Logger;
import org.switchyard.bus.camel.processors.Processors;

/**
 * Intercept strategy which looks for {@link Auditor} implementations and call
 * them before/after SwitchYard exchange bus processors.
 */
public class AuditInterceptStrategy implements InterceptStrategy {

    private Logger _logger = Logger.getLogger(AuditInterceptStrategy.class);

    @Override
    public Processor wrapProcessorInInterceptors(CamelContext context,
        ProcessorDefinition<?> definition, Processor target,
        Processor nextTarget) throws Exception {

        if (!(definition instanceof ProcessDefinition)) {
            if (_logger.isTraceEnabled()) {
                _logger.trace("Ignore " + definition + " as it's not intent to be handled with custom auditors");
            }
            return target;
        }
        ProcessDefinition process = (ProcessDefinition) definition;

        Map<String, Auditor> auditors = context.getRegistry().lookupByType(Auditor.class);
        if (auditors != null) {
            List<Auditor> wrappers = new ArrayList<Auditor>();
            for (Entry<String, Auditor> entry : auditors.entrySet()) {
                if (matches(entry.getValue(), process)) {
                    if (_logger.isTraceEnabled()) {
                        _logger.trace("Found matching auditor " + entry.getKey() + " for processing step " + process.getRef());
                    }
                    wrappers.add(entry.getValue());
                }
            }

            if (wrappers.size() > 0) {
                List<Processor> processors = new ArrayList<Processor>();
                CompositeAuditor auditor = new CompositeAuditor(wrappers);
                Processors step = Processors.valueOf(process.getRef());
                processors.add(new BeforeProcessor(step, auditor));
                processors.add(target);
                processors.add(new AfterProcessor(step, auditor));
                return new Pipeline(context, processors);
            }
        }
        if (_logger.isTraceEnabled()) {
            _logger.trace("No custom or matching auditors were found, using original processors");
        }

        return target;
    }

    /**
     * Utility method used to verify if auditor instance matching a given processRef element.
     * 
     * @param auditor Auditor to verify.
     * @param process Definition of processRef element in Camel DSL.
     * @return True if given auditor have no annotation or annotation is empty.
     * False is returned when processor reference do not match any of @Auditor values.
     */
    private boolean matches(Auditor auditor, ProcessDefinition process) {
        Audit annotation = auditor.getClass().getAnnotation(Audit.class);
        if (annotation == null || annotation.value().length == 0) {
            return true;
        }

        String ref = process.getRef();
        for (Processors processor : annotation.value()) {
            if (processor.name().equals(ref)) {
                return true;
            }
        }
        return false;
    }

}
