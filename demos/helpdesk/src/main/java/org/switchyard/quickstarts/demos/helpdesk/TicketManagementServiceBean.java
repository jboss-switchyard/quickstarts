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
package org.switchyard.quickstarts.demos.helpdesk;

import org.apache.log4j.Logger;
import org.switchyard.component.bean.Service;

/**
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
@Service(TicketManagementService.class)
public class TicketManagementServiceBean implements TicketManagementService {

    private static final Logger LOGGER = Logger.getLogger(TicketManagementServiceBean.class);

    @Override
    public TicketAck openTicket(Ticket ticket) {
        log("opening ticket");
        ticket.setStatus("opened");
        return new TicketAck().setId(ticket.getId()).setReceived(true);
    }

    @Override
    public void approveTicket(Ticket ticket) {
        log("approving ticket");
        ticket.setStatus("approved");
    }

    @Override
    public void closeTicket(Ticket ticket) {
        log("closing ticket");
        ticket.setStatus("closed");
    }

    @Override
    public void requestDetails(Ticket ticket) {
        log("requesting details");
        ticket.setStatus("requested");
    }

    @Override
    public void rejectTicket(Ticket ticket) {
        log("rejecting ticket");
        ticket.setStatus("rejected");
    }

    private void log(String msg) {
        LOGGER.info("********** " + msg + " **********");
    }

}
