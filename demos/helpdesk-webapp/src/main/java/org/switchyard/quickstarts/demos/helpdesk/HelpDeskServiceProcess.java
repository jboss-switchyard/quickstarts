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
package org.switchyard.quickstarts.demos.helpdesk;

//import org.jbpm.process.audit.JPAWorkingMemoryDbLogger;
//import org.switchyard.component.common.knowledge.annotation.Listener;
import org.jbpm.process.workitem.wsht.MinaHTWorkItemHandler;
import org.switchyard.component.bpm.annotation.BPM;
import org.switchyard.component.bpm.annotation.StartProcess;
import org.switchyard.component.bpm.annotation.WorkItemHandler;
import org.switchyard.component.bpm.work.SwitchYardServiceWorkItemHandler;
import org.switchyard.component.common.knowledge.KnowledgeConstants;
import org.switchyard.component.common.knowledge.annotation.Manifest;
import org.switchyard.component.common.knowledge.annotation.Mapping;
import org.switchyard.component.common.knowledge.annotation.Resource;

/**
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
@BPM(value=HelpDeskService.class,
    persistent=true,
    //sessionId=1,
    //listeners={@Listener(JPAWorkingMemoryDbLogger.class)},
    manifest=@Manifest(resources=@Resource(location="/META-INF/HelpDeskService.bpmn", type="BPMN2")),
    workItemHandlers={@WorkItemHandler(SwitchYardServiceWorkItemHandler.class), @WorkItemHandler(MinaHTWorkItemHandler.class)})
public interface HelpDeskServiceProcess extends HelpDeskService {

    @Override
    @StartProcess(
        inputs={@Mapping(expression="message.content", variable="ticket")},
        outputs={@Mapping(expression="ticketAck", variable=KnowledgeConstants.CONTENT_OUTPUT)}
    )
    public TicketAck openTicket(Ticket ticket);

}
