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

package org.switchyard.console.client.bridge;

import javax.inject.Inject;

import org.switchyard.console.client.NameTokens;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;

/**
 * BootstrapContext
 * 
 * SwitchYard specific context.
 * 
 * @author Rob Cernich
 */
public class BootstrapContext extends org.jboss.as.console.client.core.BootstrapContext {

    /**
     * Create a new BootstrapContext. Override DOMAIN_API and DEPLOYMENT_API set
     * by imported as7 console source.
     */
    @Inject
    public BootstrapContext() {
        super();
        setProperty(STANDALONE, "true");
        String domainApi = GWT.getHostPageBaseURL() + "app/proxy"; // "http://localhost:9990/domain-api";
        setProperty(DOMAIN_API, domainApi);

        String deploymentApi = GWT.getHostPageBaseURL() + "app/upload";
        setProperty(DEPLOYMENT_API, deploymentApi);

        Log.info("Domain API Endpoint: " + domainApi);
    }

    @Override
    public PlaceRequest getDefaultPlace() {
        return new PlaceRequest(NameTokens.SWITCH_YARD_PRESENTER);
    }

}
