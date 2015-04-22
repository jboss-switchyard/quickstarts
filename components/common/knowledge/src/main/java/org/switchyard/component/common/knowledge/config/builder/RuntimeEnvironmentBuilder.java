/*
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors.
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
package org.switchyard.component.common.knowledge.config.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;

import org.drools.core.command.CommandService;
import org.drools.core.impl.EnvironmentFactory;
import org.jbpm.runtime.manager.impl.KModuleRegisterableItemsFactory;
import org.jbpm.runtime.manager.impl.SimpleRuntimeEnvironment;
import org.jbpm.runtime.manager.impl.deploy.DeploymentDescriptorImpl;
import org.jbpm.runtime.manager.impl.deploy.DeploymentDescriptorManager;
import org.jbpm.runtime.manager.impl.mapper.InMemoryMapper;
import org.jbpm.runtime.manager.impl.mapper.JPAMapper;
import org.jbpm.services.task.commands.TaskCommandExecutorImpl;
import org.jbpm.services.task.events.TaskEventSupport;
import org.jbpm.services.task.impl.command.CommandBasedTaskService;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.Resource;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.manager.RegisterableItemsFactory;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilderFactory;
import org.kie.api.task.TaskService;
import org.kie.api.task.UserGroupCallback;
import org.kie.internal.runtime.conf.AuditMode;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.kie.internal.runtime.manager.InternalRegisterableItemsFactory;
import org.kie.internal.runtime.manager.Mapper;
import org.kie.internal.runtime.manager.TaskServiceFactory;
import org.switchyard.ServiceDomain;
import org.switchyard.common.type.reflect.Access;
import org.switchyard.common.type.reflect.FieldAccess;
import org.switchyard.component.common.knowledge.config.builder.patch.PatchedLocalTaskServiceFactory;
import org.switchyard.component.common.knowledge.config.builder.patch.PatchedRuntimeEnvironmentBuilder;
import org.switchyard.component.common.knowledge.config.items.CompoundRegisterableItemsFactory;
import org.switchyard.component.common.knowledge.config.items.ExtendedRegisterableItemsFactory;
import org.switchyard.component.common.knowledge.config.manifest.ContainerManifest;
import org.switchyard.component.common.knowledge.config.manifest.Manifest;
import org.switchyard.component.common.knowledge.config.manifest.RemoteManifest;
import org.switchyard.component.common.knowledge.config.manifest.ResourcesManifest;
import org.switchyard.component.common.knowledge.config.model.KnowledgeComponentImplementationModel;
import org.switchyard.component.common.knowledge.serial.SerializerObjectMarshallingStrategy;
import org.switchyard.component.common.knowledge.transaction.TransactionManagerLocator;
import org.switchyard.serial.FormatType;
import org.switchyard.serial.SerializerFactory;

/**
 * RuntimeEnvironmentBuilder.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
public class RuntimeEnvironmentBuilder extends KnowledgeBuilder {

    private final RuntimeEnvironmentBuilderFactory _runtimeEnvironmentBuilderFactory;
    private final boolean _persistent;
    private final EntityManagerFactoryBuilder _entityManagerFactoryBuilder;
    private final ManifestBuilder _manifestBuilder;
    private final PropertiesBuilder _propertiesBuilder;
    private final UserGroupCallbackBuilder _userGroupCallbackBuilder;
    private final RegisterableItemsFactoryBuilder _registerableItemsFactoryBuilder;

    /**
     * Creates a new RuntimeEnvironmentBuilder.
     * @param classLoader classLoader
     * @param serviceDomain serviceDomain
     * @param implementationModel implementationModel
     */
    public RuntimeEnvironmentBuilder(ClassLoader classLoader, ServiceDomain serviceDomain, KnowledgeComponentImplementationModel implementationModel) {
        super(classLoader, serviceDomain);
        //_runtimeEnvironmentBuilderFactory = org.kie.api.runtime.manager.RuntimeEnvironmentBuilder.Factory.get();
        _runtimeEnvironmentBuilderFactory = new PatchedRuntimeEnvironmentBuilder();
        _persistent = implementationModel != null ? implementationModel.isPersistent() : false;
        _entityManagerFactoryBuilder = new EntityManagerFactoryBuilder(serviceDomain, _persistent);
        _manifestBuilder = ManifestBuilder.builder(getClassLoader(), implementationModel);
        _propertiesBuilder = PropertiesBuilder.builder(implementationModel);
        _userGroupCallbackBuilder = UserGroupCallbackBuilder.builder(getClassLoader(), implementationModel);
        _registerableItemsFactoryBuilder = new RegisterableItemsFactoryBuilder(getClassLoader(), serviceDomain, implementationModel);
    }

    /**
     * Builds a RuntimeEnvironment.
     * @return a RuntimeEnvironment
     */
    public RuntimeEnvironment build() {
        final org.kie.api.runtime.manager.RuntimeEnvironmentBuilder jbpmRuntimeEnvironmentBuilder;
        Manifest manifest = _manifestBuilder.build();
        if (manifest instanceof RemoteManifest) {
            RemoteManifest remoteManifest = (RemoteManifest)manifest;
            jbpmRuntimeEnvironmentBuilder = _runtimeEnvironmentBuilderFactory.newDefaultInMemoryBuilder();
            remoteManifest.addToEnvironment(jbpmRuntimeEnvironmentBuilder);
            // we dont' do any other building for remote usage
            return jbpmRuntimeEnvironmentBuilder.get();
        } else if (manifest instanceof ContainerManifest) {
            ContainerManifest containerManifest = (ContainerManifest)manifest;
            String baseName = containerManifest.getBaseName();
            ReleaseId releaseId = containerManifest.getReleaseId();
            String sessionName = containerManifest.getSessionName();
            if (releaseId != null) {
                if (baseName != null || sessionName != null) {
                    jbpmRuntimeEnvironmentBuilder = _runtimeEnvironmentBuilderFactory.newDefaultBuilder(releaseId, baseName, sessionName);
                } else {
                    jbpmRuntimeEnvironmentBuilder = _runtimeEnvironmentBuilderFactory.newDefaultBuilder(releaseId);
                }
                // we can't update classpath containers, so no point adding it to environment below here
                containerManifest.addToEnvironment(jbpmRuntimeEnvironmentBuilder);
            } else if (baseName != null || sessionName != null) {
                jbpmRuntimeEnvironmentBuilder = _runtimeEnvironmentBuilderFactory.newClasspathKmoduleDefaultBuilder(baseName, sessionName);
            } else {
                jbpmRuntimeEnvironmentBuilder = _runtimeEnvironmentBuilderFactory.newClasspathKmoduleDefaultBuilder();
            }
        } else {
            if (_persistent) {
                jbpmRuntimeEnvironmentBuilder = _runtimeEnvironmentBuilderFactory.newDefaultBuilder();
            } else {
                jbpmRuntimeEnvironmentBuilder = _runtimeEnvironmentBuilderFactory.newDefaultInMemoryBuilder();
            }
            if (manifest instanceof ResourcesManifest) {
                ResourcesManifest resourcesManifest = (ResourcesManifest)manifest;
                for (Resource resource : resourcesManifest.buildResources()) {
                    jbpmRuntimeEnvironmentBuilder.addAsset(resource, resource.getResourceType());
                }
            }
        }
        jbpmRuntimeEnvironmentBuilder.classLoader(getClassLoader());
        jbpmRuntimeEnvironmentBuilder.persistence(_persistent);
        // provides a noop EntityManagerFactory if no persistence
        EntityManagerFactory entityManagerFactory = _entityManagerFactoryBuilder.build();
        jbpmRuntimeEnvironmentBuilder.entityManagerFactory(entityManagerFactory);
        jbpmRuntimeEnvironmentBuilder.addEnvironmentEntry(EnvironmentName.ENTITY_MANAGER_FACTORY, entityManagerFactory);
        // provides an ootb UserGroupCallback if fallen-back to
        UserGroupCallback userGroupCallback = _userGroupCallbackBuilder.build();
        jbpmRuntimeEnvironmentBuilder.userGroupCallback(userGroupCallback);
        if (_persistent) {
            UserTransaction ut = TransactionManagerLocator.INSTANCE.getUserTransaction();
            TransactionManager tm = TransactionManagerLocator.INSTANCE.getTransactionManager();
            jbpmRuntimeEnvironmentBuilder.addEnvironmentEntry(EnvironmentName.TRANSACTION, ut);
            jbpmRuntimeEnvironmentBuilder.addEnvironmentEntry(EnvironmentName.TRANSACTION_MANAGER, tm);
        } else {
            // TODO: why, when no persistence, do we have to do all this?
            jbpmRuntimeEnvironmentBuilder.addEnvironmentEntry("IS_JTA_TRANSACTION", Boolean.FALSE);
            TaskEventSupport taskEventSupport = new TaskEventSupport();
            CommandService executor = new TaskCommandExecutorImpl(EnvironmentFactory.newEnvironment(), taskEventSupport);
            jbpmRuntimeEnvironmentBuilder.addEnvironmentEntry(TaskService.class.getName(), new CommandBasedTaskService(executor, taskEventSupport));
        }
        Properties properties = _propertiesBuilder.build();
        for (Object key : properties.keySet()) {
            String name = (String)key;
            String value = properties.getProperty(name);
            jbpmRuntimeEnvironmentBuilder.addConfiguration(name, value); // add to KieSessionConfiguration
            jbpmRuntimeEnvironmentBuilder.addEnvironmentEntry(name, value); // add to Environment (SWITCHYARD-2393)
        }
        // things that need to be done to the original RuntimeEnvironment before the jBPM RuntimeEnvironmentBuilder is built (get->init)
        /*
        Access<SimpleRuntimeEnvironment> simpleREAccess = new FieldAccess<SimpleRuntimeEnvironment>(
                org.jbpm.runtime.manager.impl.RuntimeEnvironmentBuilder.class, "runtimeEnvironment");
        if (simpleREAccess.isReadable()) {
            SimpleRuntimeEnvironment originalRE = simpleREAccess.read(jbpmRuntimeEnvironmentBuilder);
        */
            SimpleRuntimeEnvironment originalRE = ((PatchedRuntimeEnvironmentBuilder)jbpmRuntimeEnvironmentBuilder).getRuntimeEnvironment();
            if (originalRE != null) {
                RegisterableItemsFactory originalRIF = originalRE.getRegisterableItemsFactory();
                if (originalRIF instanceof InternalRegisterableItemsFactory) {
                    ExtendedRegisterableItemsFactory extendedRIF = _registerableItemsFactoryBuilder.build();
                    CompoundRegisterableItemsFactory compoundRIF = new CompoundRegisterableItemsFactory(
                            (InternalRegisterableItemsFactory)originalRIF, extendedRIF);
                    jbpmRuntimeEnvironmentBuilder.registerableItemsFactory(compoundRIF);
                    ExtendedRegisterableItemsFactory.Env.addToEnvironment(jbpmRuntimeEnvironmentBuilder, compoundRIF);
                    if (manifest instanceof ContainerManifest && originalRIF instanceof KModuleRegisterableItemsFactory) {
                        Access<KieContainer> kieContainerAccess = new FieldAccess<KieContainer>(KModuleRegisterableItemsFactory.class, "kieContainer");
                        if (kieContainerAccess.isReadable()) {
                            KieContainer kieContainer = kieContainerAccess.read(originalRIF);
                            ((ContainerManifest)manifest).setKieContainer(kieContainer);
                        }
                    }
                }
                Mapper mapper;
                AuditMode auditMode;
                if (_persistent) {
                    mapper = new JPAMapper(entityManagerFactory);
                    auditMode = AuditMode.JPA;
                } else {
                    mapper = new InMemoryMapper();
                    auditMode = AuditMode.NONE;
                }
                originalRE.setMapper(mapper);
                Environment environmentTemplate = originalRE.getEnvironmentTemplate();
                // set the patched LocalTaskServiceFactory
                environmentTemplate.set(TaskServiceFactory.class.getName(), new PatchedLocalTaskServiceFactory(originalRE));
                // TODO: why, when no persistence, do we have to do all this?
                DeploymentDescriptor deploymentDescriptor = (DeploymentDescriptor)environmentTemplate.get("KieDeploymentDescriptor");
                if (deploymentDescriptor == null) {
                    deploymentDescriptor = new DeploymentDescriptorManager().getDefaultDescriptor();
                    environmentTemplate.set("KieDeploymentDescriptor", deploymentDescriptor);
                }
                ((DeploymentDescriptorImpl)deploymentDescriptor).setAuditMode(auditMode);
            }
        /*
        }
        */
        RuntimeEnvironment runtimeEnvironment = jbpmRuntimeEnvironmentBuilder.get();
        Environment environment = runtimeEnvironment.getEnvironment();
        // our ObjectMarshallingStrategy can be added to the Environment after the jBPM RuntimeEnvironmentBuilder is built (get->init)
        List<ObjectMarshallingStrategy> new_oms = new ArrayList<ObjectMarshallingStrategy>();
        new_oms.add(new SerializerObjectMarshallingStrategy(SerializerFactory.create(FormatType.JSON, null, true)));
        ObjectMarshallingStrategy[] old_oms = (ObjectMarshallingStrategy[])environment.get(EnvironmentName.OBJECT_MARSHALLING_STRATEGIES);
        if (old_oms != null) {
            for (int i=0; i < old_oms.length; i++) {
                if (old_oms[i] != null) {
                    new_oms.add(old_oms[i]);
                }
            }
        }
        environment.set(EnvironmentName.OBJECT_MARSHALLING_STRATEGIES, new_oms.toArray(new ObjectMarshallingStrategy[new_oms.size()]));
        return runtimeEnvironment;
    }

}
