/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.admin.base;

import java.util.EventObject;

import org.switchyard.admin.ComponentReference;
import org.switchyard.admin.Service;
import org.switchyard.deploy.event.ApplicationDeployedEvent;
import org.switchyard.deploy.event.ApplicationUndeployedEvent;
import org.switchyard.deploy.internal.AbstractDeployment;
import org.switchyard.event.EventObserver;
import org.switchyard.runtime.event.ExchangeCompletionEvent;

/**
 * SwitchYardBuilder
 * 
 * {@link EventObserver} implementation which builds a
 * {@link org.switchyard.admin.SwitchYard} model using notifications from 
 * {@link AbstractDeployment} events.
 * 
 * @author Rob Cernich
 */
public class SwitchYardBuilder implements EventObserver {

    private BaseSwitchYard _switchYard;

    /**
     * Create a new SwitchYardBuilder.
     * 
     * @param switchYard the {@link BaseSwitchYard} instance used to process
     *            deployment notifications.
     */
    public SwitchYardBuilder(BaseSwitchYard switchYard) {
        _switchYard = switchYard;
    }
    
    @Override
    public void notify(EventObject event) {
        if (event instanceof ApplicationDeployedEvent) {
            applicationDeployed((ApplicationDeployedEvent)event);
        } else if (event instanceof ApplicationUndeployedEvent) {
            applicationUndeployed((ApplicationUndeployedEvent)event);
        } else if (event instanceof ExchangeCompletionEvent) {
            exchangeCompleted((ExchangeCompletionEvent)event);
        }
    }
    
    void applicationDeployed(ApplicationDeployedEvent event) {
        AbstractDeployment deployment = event.getDeployment();
        if (deployment.getName() != null) {
            _switchYard.addApplication(new BaseApplication(deployment.getName(), deployment.getConfig()));
        }
    }

    void applicationUndeployed(ApplicationUndeployedEvent event) {
        AbstractDeployment deployment = event.getDeployment();
        if (deployment.getName() != null) {
            _switchYard.removeApplication(deployment.getName());
        }
    }
    
    void exchangeCompleted(ExchangeCompletionEvent event) {
        // Recording metrics at multiple levels at this point instead of
        // aggregating them.
        
        // 1 - the aggregate switchyard stats
        _switchYard.getMessageMetrics().recordMetrics(event.getExchange());
        
        for (Service service : _switchYard.getServices()) {
            // 2 - service stats
            if (service.getName().equals(event.getExchange().getServiceName())) {
                BaseComponentService cs = (BaseComponentService)service.getPromotedService();
                cs.getMessageMetrics().recordMetrics(event.getExchange());
            }
            // 3 - reference stats
            for (ComponentReference reference : service.getPromotedService().getReferences()) {
                if (reference.getName().equals(event.getExchange().getServiceName())) {
                    ((BaseComponentReference)reference).getMessageMetrics().recordMetrics(event.getExchange());
                }
            }
        }
    }
}
