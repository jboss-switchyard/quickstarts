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
package org.switchyard.quickstarts.demo.policy.security.saml;

import static org.switchyard.policy.SecurityPolicy.CLIENT_AUTHENTICATION;
import static org.switchyard.policy.SecurityPolicy.CONFIDENTIALITY;

import org.apache.log4j.Logger;
import org.switchyard.annotations.Requires;
import org.switchyard.component.bean.Service;

@Requires(security = {CONFIDENTIALITY, CLIENT_AUTHENTICATION})
@Service(WorkService.class)
public class WorkServiceBean implements WorkService {

    private static final Logger LOGGER = Logger.getLogger(WorkServiceBean.class);

    @Override
    public WorkAck doWork(Work work) {
        String cmd = work.getCommand();
        LOGGER.info(":: WorkService :: Received work command => " + cmd);
        return new WorkAck().setCommand(cmd).setReceived(true);
    }

}
