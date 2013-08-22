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
package org.switchyard.console.client.model;

import static org.jboss.dmr.client.ModelDescriptionConstants.CHILD_TYPE;
import static org.jboss.dmr.client.ModelDescriptionConstants.FAILED;
import static org.jboss.dmr.client.ModelDescriptionConstants.NAME;
import static org.jboss.dmr.client.ModelDescriptionConstants.OP;
import static org.jboss.dmr.client.ModelDescriptionConstants.OP_ADDR;
import static org.jboss.dmr.client.ModelDescriptionConstants.READ_CHILDREN_NAMES_OPERATION;
import static org.jboss.dmr.client.ModelDescriptionConstants.READ_RESOURCE_OPERATION;
import static org.jboss.dmr.client.ModelDescriptionConstants.RECURSIVE;
import static org.jboss.dmr.client.ModelDescriptionConstants.RESULT;
import static org.jboss.dmr.client.ModelDescriptionConstants.SUBSYSTEM;
import static org.jboss.dmr.client.ModelDescriptionConstants.TYPE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.jboss.as.console.client.shared.dispatch.DispatchAsync;
import org.jboss.as.console.client.shared.dispatch.impl.DMRAction;
import org.jboss.as.console.client.shared.dispatch.impl.DMRResponse;
import org.jboss.as.console.client.shared.properties.PropertyRecord;
import org.jboss.as.console.client.shared.runtime.RuntimeBaseAddress;
import org.jboss.as.console.client.shared.subsys.Baseadress;
import org.jboss.as.console.client.widgets.forms.ApplicationMetaData;
import org.jboss.as.console.client.widgets.forms.EntityAdapter;
import org.jboss.as.console.client.widgets.forms.Mutator;
import org.jboss.as.console.client.widgets.forms.PropertyBinding;
import org.jboss.dmr.client.ModelNode;
import org.switchyard.console.client.BeanFactory;
import org.switchyard.console.client.NameTokens;
import org.switchyard.console.client.Singleton;
import org.switchyard.console.components.client.model.Component;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

/**
 * SwitchYardStoreImpl
 * 
 * SwitchYardStore implementation which uses JBoss DMR.
 * 
 * @author Rob Cernich
 */
public class SwitchYardStoreImpl implements SwitchYardStore {

    // management api
    private static final String APPLICATION_NAME = "application-name"; //$NON-NLS-1$
    private static final String GET_VERSION = "get-version"; //$NON-NLS-1$
    private static final String LIST_APPLICATIONS = "list-applications"; //$NON-NLS-1$
    private static final String LIST_SERVICES = "list-services"; //$NON-NLS-1$
    private static final String LIST_REFERENCES = "list-references"; //$NON-NLS-1$
    private static final String MODULE = "module"; //$NON-NLS-1$
    private static final String PROPERTY = "property"; //$NON-NLS-1$
    private static final String READ_APPLICATION = "read-application"; //$NON-NLS-1$
    private static final String READ_SERVICE = "read-service"; //$NON-NLS-1$
    private static final String READ_REFERENCE = "read-reference"; //$NON-NLS-1$
    private static final String SERVICE_NAME = "service-name"; //$NON-NLS-1$
    private static final String SET_APPLICATION_PROPERTY = "set-application-property"; //$NON-NLS-1$
    private static final String SHOW_METRICS = "show-metrics"; //$NON-NLS-1$
    private static final String RESET_METRICS = "reset-metrics"; //$NON-NLS-1$
    private static final String STOP_GATEWAY = "stop-gateway"; //$NON-NLS-1$
    private static final String START_GATEWAY = "start-gateway"; //$NON-NLS-1$
    private static final String SWITCHYARD = NameTokens.SUBSYSTEM;
    private static final String THROTTLING = "throttling"; //$NON-NLS-1$
    private static final String UPDATE_THROTTLING = "update-throttling"; //$NON-NLS-1$
    private static final String SERVICE = "service"; //$NON-NLS-1$
    private static final String REFERENCE = "reference"; //$NON-NLS-1$
    private static final String ALL_WILDCARD = "*"; //$NON-NLS-1$
    private static final String PARSE_ERROR_MESSAGE = "Failed to parse data source representation"; //$NON-NLS-1$

    private final DispatchAsync _dispatcher;
    private final BeanFactory _factory;
    private final ApplicationMetaData _metadata;

    /**
     * Create a new SwitchYardStoreImpl.
     * 
     * @param dispatcher the injected dispatcher.
     * @param factory the injected bean factory.
     * @param metadata the injected application metadata.
     */
    @Inject
    public SwitchYardStoreImpl(DispatchAsync dispatcher, BeanFactory factory, ApplicationMetaData metadata) {
        this._dispatcher = dispatcher;
        this._factory = factory;
        this._metadata = metadata;
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
                            Log.error(PARSE_ERROR_MESSAGE, e);
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
                callback.onFailure(new Exception(Singleton.MESSAGES.error_applicationLoad(applicationName)));
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
                            Log.error(PARSE_ERROR_MESSAGE, e);
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
                callback.onFailure(new Exception(Singleton.MESSAGES.error_componentLoad(componentName)));
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
                callback.onFailure(new Exception(Singleton.MESSAGES.error_serviceLoad(serviceName, applicationName)));
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
                callback.onFailure(new Exception(Singleton.MESSAGES.error_serviceMetricsLoad(serviceName)));
            }
        });
    }

    @Override
    public void loadAllServiceMetrics(final AsyncCallback<List<ServiceMetrics>> callback) {
        // /subsystem=switchyard:show-metrics(service-name=*, type=service)

        final ModelNode operation = new ModelNode();
        final ModelNode address = RuntimeBaseAddress.get();
        operation.get(OP).set(SHOW_METRICS);
        address.add(SUBSYSTEM, SWITCHYARD);
        operation.get(OP_ADDR).set(address);
        operation.get(SERVICE_NAME).set(ALL_WILDCARD);
        operation.get(TYPE).set(SERVICE);

        _dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                final ModelNode response = result.get();
                if (response.hasDefined(FAILED)) {
                    callback.onFailure(new Exception(Singleton.MESSAGES.error_allServiceMetricsLoad()));
                } else if (response.hasDefined(RESULT)) {
                    final List<ServiceMetrics> metrics = createServiceMetrics(response.get(RESULT));
                    callback.onSuccess(metrics);
                } else {
                    callback.onSuccess(null);
                }
            }
        });
    }

    @Override
    public void loadAllReferenceMetrics(final AsyncCallback<List<ServiceMetrics>> callback) {
        // /subsystem=switchyard:show-metrics(service-name=*, type=reference)

        final ModelNode operation = new ModelNode();
        final ModelNode address = RuntimeBaseAddress.get();
        operation.get(OP).set(SHOW_METRICS);
        address.add(SUBSYSTEM, SWITCHYARD);
        operation.get(OP_ADDR).set(address);
        operation.get(SERVICE_NAME).set(ALL_WILDCARD);
        operation.get(TYPE).set(REFERENCE);

        _dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                final ModelNode response = result.get();
                if (response.hasDefined(FAILED)) {
                    callback.onFailure(new Exception(Singleton.MESSAGES.error_allReferenceMetricsLoad()));
                } else if (response.hasDefined(RESULT)) {
                    final List<ServiceMetrics> metrics = createServiceMetrics(response.get(RESULT));
                    callback.onSuccess(metrics);
                } else {
                    callback.onSuccess(null);
                }
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
                callback.onFailure(new Exception(Singleton.MESSAGES.error_systemMetricsLoad()));
            }
        });
    }

    @Override
    public void loadReferences(final AsyncCallback<List<Reference>> callback) {
        // /subsystem=switchyard:list-references()
        final List<Reference> references = new ArrayList<Reference>();

        final ModelNode operation = new ModelNode();
        final ModelNode address = RuntimeBaseAddress.get();
        address.add(SUBSYSTEM, SWITCHYARD);
        operation.get(OP_ADDR).set(address);
        operation.get(OP).set(LIST_REFERENCES);

        _dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                final ModelNode response = result.get().get(RESULT);
                if (response.isDefined()) {
                    for (final ModelNode referenceNode : response.asList()) {
                        final Reference reference = createReferenceStub(referenceNode);
                        if (reference != null) {
                            references.add(reference);
                        }
                    }

                }

                callback.onSuccess(references);
            }
        });
    }

    @Override
    public void loadReference(final String referenceName, final String applicationName,
            final AsyncCallback<Reference> callback) {
        // /subsystem=switchyard:read-reference(reference-name=referenceName,
        // application-name=applicationName)

        final ModelNode operation = new ModelNode();
        final ModelNode address = RuntimeBaseAddress.get();
        operation.get(OP).set(READ_REFERENCE);
        address.add(SUBSYSTEM, SWITCHYARD);
        operation.get(OP_ADDR).set(address);
        operation.get(SERVICE_NAME).set(referenceName);
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
                    final Reference reference = createReference(response.get(RESULT).asList().get(0));
                    if (reference != null) {
                        callback.onSuccess(reference);
                        return;
                    }
                }
                callback.onFailure(new Exception(Singleton.MESSAGES.error_referenceLoad(referenceName, applicationName)));
            }
        });
    }

    @Override
    public void loadArtifactReferences(final AsyncCallback<List<ArtifactReference>> callback) {
        // /subsystem=switchyard:read-application()

        final ModelNode operation = new ModelNode();
        final ModelNode address = RuntimeBaseAddress.get();
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
                if (response.hasDefined(FAILED)) {
                    callback.onFailure(new Exception(Singleton.MESSAGES.error_artifactsLoad()));
                } else if (response.hasDefined(RESULT)) {
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
                } else {
                    callback.onSuccess(null);
                }
            }
        });
    }

    @Override
    public void setApplicationProperty(final String applicationName, final PropertyRecord prop,
            final AsyncCallback<Void> callback) {
        // /subsystem=switchyard:set-application-property(name=applicationName,
        // property=prop)

        final ModelNode operation = new ModelNode();
        final ModelNode address = RuntimeBaseAddress.get();
        operation.get(OP).set(SET_APPLICATION_PROPERTY);
        address.add(SUBSYSTEM, SWITCHYARD);
        operation.get(OP_ADDR).set(address);
        operation.get(NAME).set(applicationName);
        operation.get(PROPERTY).add(prop.getKey(), prop.getValue());

        _dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                final ModelNode response = result.get();
                if (!response.hasDefined(FAILED)) {
                    callback.onSuccess(null);
                    return;
                }
                callback.onFailure(new Exception(Singleton.MESSAGES.error_setProperty(prop.getKey())));
            }
        });
    }

    @Override
    public void resetSystemMetrics(final AsyncCallback<Void> callback) {
        // /subsystem=switchyard:reset-metrics()

        final ModelNode operation = new ModelNode();
        final ModelNode address = RuntimeBaseAddress.get();
        operation.get(OP).set(RESET_METRICS);
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
                if (!response.hasDefined(FAILED)) {
                    callback.onSuccess(null);
                    return;
                }
                callback.onFailure(new Exception(Singleton.MESSAGES.error_resetSystemMetrics(response
                        .getFailureDescription())));
            }
        });
    }

    @Override
    public void resetMetrics(final String name, final String applicationName, final AsyncCallback<Void> callback) {
        // /subsystem=switchyard:reset-metrics(name=name,
        // application-name=applicationName)

        final ModelNode operation = new ModelNode();
        final ModelNode address = RuntimeBaseAddress.get();
        operation.get(OP).set(RESET_METRICS);
        address.add(SUBSYSTEM, SWITCHYARD);
        operation.get(OP_ADDR).set(address);
        operation.get(NAME).set(name);
        operation.get(APPLICATION_NAME).set(applicationName);

        _dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                final ModelNode response = result.get();
                if (!response.hasDefined(FAILED)) {
                    callback.onSuccess(null);
                    return;
                }
                callback.onFailure(new Exception(Singleton.MESSAGES.error_resetObjectMetrics(name,
                        response.getFailureDescription())));
            }
        });
    }

    @Override
    public void stopGateway(final String name, final String serviceName, final String applicationName,
            final AsyncCallback<Void> callback) {
        // /subsystem=switchyard:stop-gateway(name=name,
        // service-name=serviceName, application-name=applicationName)

        final ModelNode operation = new ModelNode();
        final ModelNode address = RuntimeBaseAddress.get();
        operation.get(OP).set(STOP_GATEWAY);
        address.add(SUBSYSTEM, SWITCHYARD);
        operation.get(OP_ADDR).set(address);
        operation.get(NAME).set(name);
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
                if (!response.hasDefined(FAILED)) {
                    callback.onSuccess(null);
                    return;
                }
                callback.onFailure(new Exception(Singleton.MESSAGES.error_stopGateway(name,
                        response.getFailureDescription())));
            }
        });
    }

    @Override
    public void startGateway(final String name, final String serviceName, final String applicationName,
            final AsyncCallback<Void> callback) {
        // /subsystem=switchyard:start-gateway(name=name,
        // service-name=serviceName, application-name=applicationName)

        final ModelNode operation = new ModelNode();
        final ModelNode address = RuntimeBaseAddress.get();
        operation.get(OP).set(START_GATEWAY);
        address.add(SUBSYSTEM, SWITCHYARD);
        operation.get(OP_ADDR).set(address);
        operation.get(NAME).set(name);
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
                if (!response.hasDefined(FAILED)) {
                    callback.onSuccess(null);
                    return;
                }
                callback.onFailure(new Exception(Singleton.MESSAGES.error_startGateway(name,
                        response.getFailureDescription())));
            }
        });
    }

    @Override
    public void updateThrottling(final Service service, final Throttling throttling, final AsyncCallback<Void> callback) {
        // /subsystem=switchyard:update-throttling(service-name=name, application-name=applicationName, throttling=throttling)

        final EntityAdapter<Throttling> entityAdapter = new EntityAdapter<Throttling>(Throttling.class, _metadata);
        final ModelNode operation = new ModelNode();
        final ModelNode address = RuntimeBaseAddress.get();
        operation.get(OP).set(UPDATE_THROTTLING);
        address.add(SUBSYSTEM, SWITCHYARD);
        operation.get(OP_ADDR).set(address);
        operation.get(SERVICE_NAME).set(service.getName());
        operation.get(APPLICATION_NAME).set(service.getApplication());
        operation.get(THROTTLING).set(entityAdapter.fromEntity(throttling));

        _dispatcher.execute(new DMRAction(operation), new AsyncCallback<DMRResponse>() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(DMRResponse result) {
                final ModelNode response = result.get();
                if (!response.hasDefined(FAILED)) {
                    callback.onSuccess(null);
                    return;
                }
                callback.onFailure(new Exception(Singleton.MESSAGES.error_updateThrottling(service.localName(),
                        response.getFailureDescription())));
            }
        });
    }

    @Override
    public <T> T processChangeSet(final Class<T> type, final T original, final Map<String, Object> changeSet,
            final boolean merge) {
        final List<PropertyBinding> properties = _metadata.getBeanMetaData(type).getProperties();
        final T newEntity = (T) _metadata.getFactory(type).create();
        @SuppressWarnings("unchecked")
        final Mutator<T> mutator = _metadata.getMutator(type);

        for (PropertyBinding property : properties) {
            final String javaName = property.getJavaName();
            Object propertyValue = mutator.getValue(original, javaName);
            Object changed = changeSet.get(javaName);
            if (changed != null && !changed.equals(propertyValue)) {
                mutator.setValue(newEntity, javaName, changed);
            } else if (merge) {
                mutator.setValue(newEntity, javaName, propertyValue);
            }
        }
        return newEntity;
    }

    private SystemDetails createSystemDetails(final ModelNode systemDetailsNode) {
        try {
            return AutoBeanCodex.decode(_factory, SystemDetails.class, systemDetailsNode.toJSONString(true)).as();
        } catch (Exception e) {
            Log.error(PARSE_ERROR_MESSAGE, e);
            return null;
        }
    }

    private Application createApplication(final ModelNode applicationNode) {
        try {
            return AutoBeanCodex.decode(_factory, Application.class, applicationNode.toJSONString(true)).as();
        } catch (Exception e) {
            Log.error(PARSE_ERROR_MESSAGE, e);
            return null;
        }
    }

    private Component createComponent(final ModelNode componentNode) {
        try {
            return AutoBeanCodex.decode(_factory, Component.class, componentNode.toJSONString(true)).as();
        } catch (Exception e) {
            Log.error(PARSE_ERROR_MESSAGE, e);
            return null;
        }
    }

    private Service createServiceStub(final ModelNode serviceNode) {
        try {
            return AutoBeanCodex.decode(_factory, Service.class, serviceNode.toJSONString(true)).as();
        } catch (Exception e) {
            Log.error(PARSE_ERROR_MESSAGE, e);
            return null;
        }
    }

    private Service createService(final ModelNode serviceNode) {
        try {
            return AutoBeanCodex.decode(_factory, Service.class, serviceNode.toJSONString(true)).as();
        } catch (Exception e) {
            Log.error(PARSE_ERROR_MESSAGE, e);
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
                Log.error(PARSE_ERROR_MESSAGE, e);
            }
        }
        return metrics;
    }

    private MessageMetrics createMessageMetrics(final ModelNode metricsNode) {
        try {
            return AutoBeanCodex.decode(_factory, MessageMetrics.class, metricsNode.toJSONString(true)).as();
        } catch (Exception e) {
            Log.error(PARSE_ERROR_MESSAGE, e);
            return null;
        }
    }

    private Reference createReferenceStub(final ModelNode referenceNode) {
        try {
            return AutoBeanCodex.decode(_factory, Reference.class, referenceNode.toJSONString(true)).as();
        } catch (Exception e) {
            Log.error(PARSE_ERROR_MESSAGE, e);
            return null;
        }
    }

    private Reference createReference(final ModelNode referenceNode) {
        try {
            return AutoBeanCodex.decode(_factory, Reference.class, referenceNode.toJSONString(true)).as();
        } catch (Exception e) {
            Log.error(PARSE_ERROR_MESSAGE, e);
            return null;
        }
    }

}
