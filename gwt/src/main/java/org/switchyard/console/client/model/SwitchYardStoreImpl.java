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

import static org.jboss.dmr.client.ModelDescriptionConstants.NAME;
import static org.jboss.dmr.client.ModelDescriptionConstants.OP;
import static org.jboss.dmr.client.ModelDescriptionConstants.OP_ADDR;
import static org.jboss.dmr.client.ModelDescriptionConstants.RESULT;
import static org.jboss.dmr.client.ModelDescriptionConstants.SUBSYSTEM;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.jboss.as.console.client.core.ApplicationProperties;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.dmr.client.ModelNode;
import org.switchyard.console.client.BeanFactory;
import org.switchyard.console.components.client.model.Component;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.autobean.shared.AutoBeanCodex;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * SwitchYardStoreImpl
 * 
 * SwitchYardStore implementation which uses JBoss DMR.
 * 
 * @author Rob Cernich
 */
public class SwitchYardStoreImpl implements SwitchYardStore {

    // management api
    private static final String APPLICATION_NAME = "application-name";
    private static final String GET_VERSION = "get-version";
    private static final String LIST_APPLICATIONS = "list-applications";
    private static final String LIST_COMPONENTS = "list-components";
    private static final String LIST_SERVICES = "list-services";
    private static final String READ_APPLICATION = "read-application";
    private static final String READ_COMPONENT = "read-component";
    private static final String READ_SERVICE = "read-service";
    private static final String SERVICE_NAME = "service-name";
    private static final String SWITCHYARD = "switchyard";

    private final DispatchAsync _dispatcher;

    private final BeanFactory _factory;

    private final ApplicationProperties _bootstrap;

    private final boolean _isStandalone;

    /**
     * Create a new SwitchYardStoreImpl.
     * 
     * @param dispatcher the injected dispatcher.
     * @param factory the injected bean factory.
     * @param bootstrap the injected bootstrap context.
     */
    @Inject
    public SwitchYardStoreImpl(DispatchAsync dispatcher, BeanFactory factory, ApplicationProperties bootstrap) {
        this._dispatcher = dispatcher;
        this._factory = factory;
        this._bootstrap = bootstrap;
        this._isStandalone = bootstrap.getProperty(ApplicationProperties.STANDALONE).equals("true");
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void loadSystemDetails(final AsyncCallback<SystemDetails> callback) {
        // /subsystem=switchyard:get-version()
        final ModelNode operation = new ModelNode();
        operation.get(OP_ADDR, SUBSYSTEM).set(SWITCHYARD);
        operation.get(OP).set(GET_VERSION);

        _dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                final ModelNode response = ModelNode.fromBase64(result.getResponseText()).get(RESULT);
                SystemDetails systemDetails = null;
                if (response.isDefined()) {
                    systemDetails = createSystemDetails(response);
                }

                callback.onSuccess(systemDetails);
            }
        });
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void loadApplications(final AsyncCallback<List<Application>> callback) {
        // /subsystem=switchyard:list-applications()
        final List<Application> applications = new ArrayList<Application>();

        final ModelNode operation = new ModelNode();
        operation.get(OP_ADDR, SUBSYSTEM).set(SWITCHYARD);
        operation.get(OP).set(LIST_APPLICATIONS);

        _dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                final ModelNode response = ModelNode.fromBase64(result.getResponseText()).get(RESULT);
                if (response.isDefined()) {
                    for (final ModelNode applicationNode : response.asList()) {
                        try {
                            final Application application = _factory.application().as();
                            application.setName(applicationNode.asString());
                            applications.add(application);
                        } catch (IllegalArgumentException e) {
                            Log.error("Failed to parse data source representation", e);
                        }
                    }

                }

                callback.onSuccess(applications);
            }
        });
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void loadApplication(final String applicationName, final AsyncCallback<Application> callback) {
        // /subsystem=switchyard:read-application(name=applicationName)

        final ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_APPLICATION);
        operation.get(OP_ADDR, SUBSYSTEM).set(SWITCHYARD);
        operation.get(NAME).set(applicationName);

        _dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                final ModelNode response = ModelNode.fromBase64(result.getResponseText());
                if (response.hasDefined(RESULT)) {
                    final Application application = createApplication(response.get(RESULT).asList().get(0));
                    if (application != null) {
                        callback.onSuccess(application);
                        return;
                    }
                }
                callback.onFailure(new Exception("Could not load information for application: " + applicationName));
            }
        });
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void loadComponents(final AsyncCallback<List<Component>> callback) {
        // /subsystem=switchyard:list-components
        final List<Component> components = new ArrayList<Component>();

        final ModelNode operation = new ModelNode();
        operation.get(OP).set(LIST_COMPONENTS);
        operation.get(OP_ADDR).add(SUBSYSTEM, "switchyard");

        _dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                final ModelNode response = ModelNode.fromBase64(result.getResponseText()).get(RESULT);
                if (response.isDefined()) {
                    for (final ModelNode componentNode : response.asList()) {
                        try {
                            final Component component = _factory.component().as();
                            component.setName(componentNode.asString());
                            components.add(component);
                        } catch (IllegalArgumentException e) {
                            Log.error("Failed to parse data source representation", e);
                        }
                    }

                }

                callback.onSuccess(components);
            }
        });
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void loadComponent(final String componentName, final AsyncCallback<Component> callback) {
        // /subsystem=switchyard:read-component(name=componentName)

        final ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_COMPONENT);
        operation.get(OP_ADDR, SUBSYSTEM).set(SWITCHYARD);
        operation.get(NAME).set(componentName);

        _dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                final ModelNode response = ModelNode.fromBase64(result.getResponseText());
                if (response.hasDefined(RESULT)) {
                    final Component component = createComponent(response.get(RESULT).asList().get(0));
                    if (component != null) {
                        callback.onSuccess(component);
                        return;
                    }
                }
                callback.onFailure(new Exception("Could not load information for component: " + componentName));
            }
        });
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void loadServices(final AsyncCallback<List<Service>> callback) {
        // /subsystem=switchyard:list-services()
        final List<Service> services = new ArrayList<Service>();

        final ModelNode operation = new ModelNode();
        operation.get(OP_ADDR, SUBSYSTEM).set(SWITCHYARD);
        operation.get(OP).set(LIST_SERVICES);

        _dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                final ModelNode response = ModelNode.fromBase64(result.getResponseText()).get(RESULT);
                if (response.isDefined()) {
                    for (final ModelNode serviceNode : response.asList()) {
                        final Service service = createServiceStub(serviceNode);
                        if (service != null) {
                            services.add(service);
                        }
                    }

                }

                callback.onSuccess(services);
            }
        });
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void loadService(final String serviceName, final String applicationName,
            final AsyncCallback<Service> callback) {
        // /subsystem=switchyard:read-service(service-name=serviceName,
        // application-name=applicationName)

        final ModelNode operation = new ModelNode();
        operation.get(OP).set(READ_SERVICE);
        operation.get(OP_ADDR, SUBSYSTEM).set(SWITCHYARD);
        operation.get(SERVICE_NAME).set(serviceName);
        operation.get(APPLICATION_NAME).set(applicationName);

        _dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                final ModelNode response = ModelNode.fromBase64(result.getResponseText());
                if (response.hasDefined(RESULT)) {
                    final Service service = createService(response.get(RESULT).asList().get(0));
                    if (service != null) {
                        callback.onSuccess(service);
                        return;
                    }
                }
                callback.onFailure(new Exception("Could not load information for service: " + serviceName
                        + " from application: " + applicationName));
            }
        });
    }

    private SystemDetails createSystemDetails(final ModelNode systemDetailsNode) {
        try {
            return AutoBeanCodex.decode(_factory, SystemDetails.class, systemDetailsNode.toJSONString(true)).as();
        } catch (Exception e) {
            Log.error("Failed to parse data source representation", e);
            return null;
        }
    }

    private Application createApplication(final ModelNode applicationNode) {
        try {
            return AutoBeanCodex.decode(_factory, Application.class, applicationNode.toJSONString(true)).as();
        } catch (Exception e) {
            Log.error("Failed to parse data source representation", e);
            return null;
        }
    }

    private Component createComponent(final ModelNode componentNode) {
        try {
            return AutoBeanCodex.decode(_factory, Component.class, componentNode.toJSONString(true)).as();
        } catch (Exception e) {
            Log.error("Failed to parse data source representation", e);
            return null;
        }
    }

    private Service createServiceStub(final ModelNode serviceNode) {
        try {
            return AutoBeanCodex.decode(_factory, Service.class, serviceNode.toJSONString(true)).as();
        } catch (Exception e) {
            Log.error("Failed to parse data source representation", e);
            return null;
        }
    }

    private Service createService(final ModelNode serviceNode) {
        try {
            return AutoBeanCodex.decode(_factory, Service.class, serviceNode.toJSONString(true)).as();
        } catch (Exception e) {
            Log.error("Failed to parse data source representation", e);
            return null;
        }
    }

}
