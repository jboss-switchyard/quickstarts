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
package org.switchyard.console.client.model;

import static org.jboss.dmr.client.ModelDescriptionConstants.CHILD_TYPE;
import static org.jboss.dmr.client.ModelDescriptionConstants.DEPLOYMENT;
import static org.jboss.dmr.client.ModelDescriptionConstants.NAME;
import static org.jboss.dmr.client.ModelDescriptionConstants.OP;
import static org.jboss.dmr.client.ModelDescriptionConstants.OP_ADDR;
import static org.jboss.dmr.client.ModelDescriptionConstants.READ_ATTRIBUTE_OPERATION;
import static org.jboss.dmr.client.ModelDescriptionConstants.READ_CHILDREN_RESOURCES_OPERATION;
import static org.jboss.dmr.client.ModelDescriptionConstants.READ_RESOURCE_OPERATION;
import static org.jboss.dmr.client.ModelDescriptionConstants.RESULT;
import static org.jboss.dmr.client.ModelDescriptionConstants.RUNTIME_NAME;
import static org.jboss.dmr.client.ModelDescriptionConstants.SUBSYSTEM;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jboss.as.console.client.core.ApplicationProperties;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.dmr.client.ModelNode;
import org.jboss.dmr.client.Property;
import org.switchyard.console.client.BeanFactory;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * SwitchYardStoreImpl
 * 
 * SwitchYardStore implementation which uses JBoss DMR.
 * 
 * @author Rob Cernich
 */
public class SwitchYardStoreImpl implements SwitchYardStore {

    private DispatchAsync _dispatcher;

    private BeanFactory _factory;

    private ApplicationProperties _bootstrap;

    private boolean _isStandalone;

    /**
     * Create a new SwitchYardStoreImpl.
     * 
     * @param dispatcher
     *            the injected dispatcher.
     * @param factory
     *            the injected bean factory.
     * @param bootstrap
     *            the injected bootstrap context.
     */
    @Inject
    public SwitchYardStoreImpl(DispatchAsync dispatcher, BeanFactory factory, ApplicationProperties bootstrap) {
        this._dispatcher = dispatcher;
        this._factory = factory;
        this._bootstrap = bootstrap;
        this._isStandalone = bootstrap.getProperty(ApplicationProperties.STANDALONE).equals("true");
    }

    @Override
    public void loadDeployments(final AsyncCallback<List<SwitchYardDeployment>> callback) {
        // /:read-children-resources(child-type=deployment)
        final List<SwitchYardDeployment> deployments = new ArrayList<SwitchYardDeployment>();

        ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_CHILDREN_RESOURCES_OPERATION);
        operation.get(CHILD_TYPE).set(DEPLOYMENT);

        _dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                if (response.get(RESULT).isDefined()) {
                    List<ModelNode> payload = response.get(RESULT).asList();

                    for (ModelNode item : payload) {
                        Property property = item.asProperty();
                        ModelNode deploymentItem = property.getValue().asObject();

                        SwitchYardDeployment rec = createSwitchYardDeployment(deploymentItem);
                        if (rec != null) {
                            deployments.add(rec);
                        }
                    }

                }

                callback.onSuccess(deployments);
            }
        });
    }

    @Override
    public void loadModules(final AsyncCallback<List<SwitchYardModule>> callback) {
        // /subsystem=switchyard:read-attribute(name=modules)
        final List<SwitchYardModule> modules = new ArrayList<SwitchYardModule>();

        ModelNode operation = new ModelNode();
        // TODO: use variable for operation names
        operation.get(OP).set(READ_ATTRIBUTE_OPERATION);
        operation.get(OP_ADDR).add(SUBSYSTEM, "switchyard");
        operation.get(NAME).set("modules");

        _dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                if (response.get(RESULT).isDefined()) {
                    List<ModelNode> payload = response.get(RESULT).asList();

                    for (ModelNode item : payload) {
                        Property property = item.asProperty();
                        String name = property.getName();

                        try {
                            SwitchYardModule rec = _factory.switchYardModule().as();
                            rec.setName(name);
                            modules.add(rec);
                        } catch (IllegalArgumentException e) {
                            Log.error("Failed to parse data source representation", e);
                        }
                    }

                }

                callback.onSuccess(modules);
            }
        });
    }

    @Override
    public void loadDeployment(final String deploymentName, final AsyncCallback<SwitchYardDeployment> callback) {
        // /deployment=<deploymentName>:read-resource()

        ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_RESOURCE_OPERATION);
        operation.get(OP_ADDR).set(DEPLOYMENT, deploymentName);

        _dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                SwitchYardDeployment deployment = null;
                ModelNode response = ModelNode.fromBase64(result.getResponseText());
                if (response.get(RESULT).isDefined()) {
                    deployment = createSwitchYardDeployment(response.get(RESULT));
                }

                if (deployment == null) {
                    callback.onFailure(new Exception("Could not load information for deployment: " + deploymentName));
                } else {
                    callback.onSuccess(deployment);
                }
            }
        });
    }

    private SwitchYardDeployment createSwitchYardDeployment(ModelNode item) {
        try {
            SwitchYardDeployment rec = _factory.switchYardDeployment().as();
            rec.setName(item.get(NAME).asString());
            rec.setRuntimeName(item.get(RUNTIME_NAME).asString());
            if (_isStandalone) {
                rec.setEnabled(item.get("enabled").asBoolean());
            }
            if (!_isStandalone) {
                rec.setEnabled(true);
            }
            return rec;
        } catch (IllegalArgumentException e) {
            Log.error("Failed to parse data source representation", e);
            return null;
        }
    }

}
