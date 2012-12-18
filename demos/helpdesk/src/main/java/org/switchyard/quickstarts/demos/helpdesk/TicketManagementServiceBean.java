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
