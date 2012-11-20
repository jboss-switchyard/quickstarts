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
package org.switchyard.quickstarts.rules.camel.cbr;

import org.switchyard.component.common.knowledge.annotation.Manifest;
import org.switchyard.component.common.knowledge.annotation.Mapping;
import org.switchyard.component.common.knowledge.annotation.Resource;
import org.switchyard.component.rules.annotation.Execute;
import org.switchyard.component.rules.annotation.Rules;

/**
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
@Rules(
        value=DestinationService.class,
        //listeners={@Listener(DebugWorkingMemoryEventListener.class), @Listener(DebugAgendaEventListener.class)},
        //loggers=@Logger(type=LoggerType.CONSOLE),
        manifest=@Manifest(resources=@Resource(location="/META-INF/DestinationServiceRules.drl", type="DRL")))
public interface DestinationServiceRules extends DestinationService {

    @Override
    @Execute(
        globals=@Mapping(expression="exchange", variable="exchange"),
        inputs=@Mapping(expression="message.content.widget")
    )
    public void determineDestination(Box box);

}
