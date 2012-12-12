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
import static org.jboss.dmr.client.ModelDescriptionConstants.NAME;
import static org.jboss.dmr.client.ModelDescriptionConstants.OP;
import static org.jboss.dmr.client.ModelDescriptionConstants.OP_ADDR;
import static org.jboss.dmr.client.ModelDescriptionConstants.READ_CHILDREN_NAMES_OPERATION;
import static org.jboss.dmr.client.ModelDescriptionConstants.READ_RESOURCE_OPERATION;
import static org.jboss.dmr.client.ModelDescriptionConstants.RECURSIVE;
import static org.jboss.dmr.client.ModelDescriptionConstants.RESULT;
import static org.jboss.dmr.client.ModelDescriptionConstants.SUBSYSTEM;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jboss.as.console.client.core.ApplicationProperties;
import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.runtime.RuntimeBaseAddress;
import org.jboss.as.console.client.shared.subsys.Baseadress;
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
    private static final String LIST_SERVICES = "list-services";
    private static final String MODULE = "module";
    private static final String READ_APPLICATION = "read-application";
    private static final String READ_SERVICE = "read-service";
    private static final String SERVICE_NAME = "service-name";
    private static final String SHOW_METRICS = "show-metrics";
    private static final String SWITCHYARD = "switchyard";

    private final DispatchAsync _dispatcher;

    private final BeanFactory _factory;

    private final ApplicationProperties _bootstrap;

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
    }

    @Override
    public BeanFactory getBeanFactory() {
        return _factory;
    }

    @Override
    public void loadSystemDetails(final AsyncCallback<SystemDetails> callback) {
        // /subsystem=switchyard:get-version()
        final ModelNode operation = new ModelNode();
        final ModelNode address = Baseadress.get();
        address.add(SUBSYSTEM, SWITCHYARD);
        operation.get(OP_ADDR).set(address);
        operation.get(OP).set(GET_VERSION);

        _dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                final ModelNode response = result.get().get(RESULT);
                SystemDetails systemDetails = null;
                if (response.isDefined()) {
                    systemDetails = createSystemDetails(response);
                }

                callback.onSuccess(systemDetails);
            }
        });
    }

    @Override
    public void loadApplications(final AsyncCallback<List<Application>> callback) {
        // /subsystem=switchyard:list-applications()
        final List<Application> applications = new ArrayList<Application>();

        final ModelNode operation = new ModelNode();
        final ModelNode address = RuntimeBaseAddress.get();
        address.add(SUBSYSTEM, SWITCHYARD);
        operation.get(OP_ADDR).set(address);
        operation.get(OP).set(LIST_APPLICATIONS);

        _dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                final ModelNode response = result.get().get(RESULT);
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

    @Override
    public void loadApplication(final String applicationName, final AsyncCallback<Application> callback) {
        // /subsystem=switchyard:read-application(name=applicationName)

        final ModelNode operation = new ModelNode();
        final ModelNode address = RuntimeBaseAddress.get();
        operation.get(OP).set(READ_APPLICATION);
        address.add(SUBSYSTEM, SWITCHYARD);
        operation.get(OP_ADDR).set(address);
        operation.get(NAME).set(applicationName);

        _dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                final ModelNode response = result.get();
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

    @Override
    public void loadComponents(final AsyncCallback<List<Component>> callback) {
        // /subsystem=switchyard:read-children-names(child-type=module)
        final List<Component> components = new ArrayList<Component>();

        final ModelNode operation = new ModelNode();
        final ModelNode address = Baseadress.get();
        operation.get(OP).set(READ_CHILDREN_NAMES_OPERATION);
        operation.get(CHILD_TYPE).set(MODULE);
        address.add(SUBSYSTEM, SWITCHYARD);
        operation.get(OP_ADDR).set(address);

        _dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                final ModelNode response = result.get().get(RESULT);
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

    @Override
    public void loadComponent(final String componentName, final AsyncCallback<Component> callback) {
        // /subsystem=switchyard/module=componentName:read-resource(recursive=true)

        final ModelNode operation = new ModelNode();
        final ModelNode address = Baseadress.get();
        operation.get(OP).set(READ_RESOURCE_OPERATION);
        operation.get(RECURSIVE).set(true);
        address.add(SUBSYSTEM, SWITCHYARD);
        address.add(MODULE, componentName);
        operation.get(OP_ADDR).set(address);
        operation.get(NAME).set(componentName);

        _dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                final ModelNode response = result.get();
                if (response.hasDefined(RESULT)) {
                    final Component component = createComponent(response.get(RESULT));
                    if (component != null) {
                        // HACK
                        component.setName(componentName);
                        callback.onSuccess(component);
                        return;
                    }
                }
                callback.onFailure(new Exception("Could not load information for component: " + componentName));
            }
        });
    }

    @Override
    public void loadServices(final AsyncCallback<List<Service>> callback) {
        // /subsystem=switchyard:list-services()
        final List<Service> services = new ArrayList<Service>();

        final ModelNode operation = new ModelNode();
        final ModelNode address = RuntimeBaseAddress.get();
        address.add(SUBSYSTEM, SWITCHYARD);
        operation.get(OP_ADDR).set(address);
        operation.get(OP).set(LIST_SERVICES);

        _dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                final ModelNode response = result.get().get(RESULT);
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

    @Override
    public void loadService(final String serviceName, final String applicationName,
            final AsyncCallback<Service> callback) {
        // /subsystem=switchyard:read-service(service-name=serviceName,
        // application-name=applicationName)

        final ModelNode operation = new ModelNode();
        final ModelNode address = RuntimeBaseAddress.get();
        operation.get(OP).set(READ_SERVICE);
        address.add(SUBSYSTEM, SWITCHYARD);
        operation.get(OP_ADDR).set(address);
        operation.get(SERVICE_NAME).set(serviceName);
        operation.get(APPLICATION_NAME).set(applicationName);

        _dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                final ModelNode response = result.get();
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

    @Override
    public void loadServiceMetrics(final String serviceName, final AsyncCallback<ServiceMetrics> callback) {
        // /subsystem=switchyard:show-metrics(service-name=serviceName)

        final ModelNode operation = new ModelNode();
        final ModelNode address = RuntimeBaseAddress.get();
        operation.get(OP).set(SHOW_METRICS);
        address.add(SUBSYSTEM, SWITCHYARD);
        operation.get(OP_ADDR).set(address);
        operation.get(SERVICE_NAME).set(serviceName);

        _dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                final ModelNode response = result.get();
                if (response.hasDefined(RESULT)) {
                    final List<ServiceMetrics> metrics = createServiceMetrics(response.get(RESULT));
                    if (metrics != null && metrics.size() > 0) {
                        callback.onSuccess(metrics.get(0));
                        return;
                    }
                }
                callback.onFailure(new Exception("Could not load metrics for service: " + serviceName));
            }
        });
    }

    @Override
    public void loadAllServiceMetrics(final AsyncCallback<List<ServiceMetrics>> callback) {
        // /subsystem=switchyard:show-metrics(service-name=*)

        final ModelNode operation = new ModelNode();
        final ModelNode address = RuntimeBaseAddress.get();
        operation.get(OP).set(SHOW_METRICS);
        address.add(SUBSYSTEM, SWITCHYARD);
        operation.get(OP_ADDR).set(address);
        operation.get(SERVICE_NAME).set("*");

        _dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                final ModelNode response = result.get();
                if (response.hasDefined(RESULT)) {
                    final List<ServiceMetrics> metrics = createServiceMetrics(response.get(RESULT));
                    if (metrics != null) {
                        callback.onSuccess(metrics);
                        return;
                    }
                }
                callback.onFailure(new Exception("Could not load all service metrics."));
            }
        });
    }

    @Override
    public void loadSystemMetrics(final AsyncCallback<MessageMetrics> callback) {
        // /subsystem=switchyard:show-metrics()

        final ModelNode operation = new ModelNode();
        final ModelNode address = RuntimeBaseAddress.get();
        operation.get(OP).set(SHOW_METRICS);
        address.add(SUBSYSTEM, SWITCHYARD);
        operation.get(OP_ADDR).set(address);

        _dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                final ModelNode response = result.get();
                if (response.hasDefined(RESULT)) {
                    final MessageMetrics metrics = createMessageMetrics(response.get(RESULT).asList().get(0));
                    if (metrics != null) {
                        callback.onSuccess(metrics);
                        return;
                    }
                }
                callback.onFailure(new Exception("Could not load metrics for system"));
            }
        });
    }

    @Override
    public void loadArtifactReferences(final AsyncCallback<List<ArtifactReference>> callback) {
        // /subsystem=switchyard:read-application()

        final ModelNode operation = new ModelNode();
        final ModelNode address = Baseadress.get();
        operation.get(OP).set(READ_APPLICATION);
        address.add(SUBSYSTEM, SWITCHYARD);
        operation.get(OP_ADDR).set(address);

        _dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                final ModelNode response = result.get();
                if (response.hasDefined(RESULT)) {
                    Map<String, ArtifactReference> references = new HashMap<String, ArtifactReference>();
                    for (ModelNode node : response.get(RESULT).asList()) {
                        Application application = createApplication(node);
                        if (application == null) {
                            continue;
                        }
                        List<ArtifactReference> artifacts = application.getArtifacts();
                        if (artifacts == null) {
                            continue;
                        }
                        for (ArtifactReference artifact : artifacts) {
                            if (references.containsKey(artifact.key())) {
                                artifact = references.get(artifact.key());
                            } else {
                                ArtifactReference copy = getBeanFactory().artifactReference().as();
                                copy.setName(artifact.getName());
                                copy.setUrl(artifact.getUrl());
                                copy.setApplications(new ArrayList<Application>());
                                artifact = copy;
                                references.put(artifact.key(), artifact);
                            }
                            artifact.getApplications().add(application);
                        }
                    }
                    callback.onSuccess(new ArrayList<ArtifactReference>(references.values()));
                    return;
                }
                callback.onFailure(new Exception("Could not load artifact references."));
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

    private List<ServiceMetrics> createServiceMetrics(final ModelNode metricsNode) {
        final List<ModelNode> items = metricsNode.asList();
        final List<ServiceMetrics> metrics = new ArrayList<ServiceMetrics>(items.size());
        for (ModelNode item : items) {
            try {
                metrics.add(AutoBeanCodex.decode(_factory, ServiceMetrics.class, item.toJSONString(true)).as());
            } catch (Exception e) {
                Log.error("Failed to parse data source representation", e);
            }
        }
        return metrics;
    }

    private MessageMetrics createMessageMetrics(final ModelNode metricsNode) {
        try {
            return AutoBeanCodex.decode(_factory, MessageMetrics.class, metricsNode.toJSONString(true)).as();
        } catch (Exception e) {
            Log.error("Failed to parse data source representation", e);
            return null;
        }
    }

}
