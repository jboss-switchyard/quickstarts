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

import java.lang.reflect.Constructor;
import java.util.Properties;

import org.jbpm.runtime.manager.impl.identity.UserDataServiceProvider;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.kie.api.task.UserGroupCallback;
import org.switchyard.common.io.pull.PropertiesPuller;
import org.switchyard.common.io.pull.PropertiesPuller.PropertiesType;
import org.switchyard.common.io.pull.Puller.PathType;
import org.switchyard.common.type.reflect.Construction;
import org.switchyard.component.common.knowledge.CommonKnowledgeMessages;
import org.switchyard.component.common.knowledge.config.model.KnowledgeComponentImplementationModel;
import org.switchyard.component.common.knowledge.config.model.UserGroupCallbackModel;
import org.switchyard.component.common.knowledge.task.PropertiesUserGroupCallback;

/**
 * UserGroupCallbackBuilder.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2014 Red Hat Inc.
 */
public class UserGroupCallbackBuilder extends KnowledgeBuilder {

    private static final String USER_CALLBACK_IMPL = System.getProperty("org.jbpm.ht.callback");
    private static final String DEFAULT_PROPERTIES_LOCATION = System.getProperty("jbpm.user.group.mapping", System.getProperty("jboss.server.config.dir", "target/classes") + "/roles.properties");

    private static final Class<?>[][] PARAMETER_TYPES = new Class<?>[][]{
        new Class<?>[]{Properties.class},
        new Class<?>[0]
    };

    private Class<? extends UserGroupCallback> _userGroupCallbackClass;
    private final PropertiesBuilder _propertiesBuilder;

    /**
     * Creates a UserGroupCallbackBuilder.
     * @param classLoader classLoader
     * @param userGroupCallbackModel userGroupCallbackModel
     */
    @SuppressWarnings("unchecked")
    public UserGroupCallbackBuilder(ClassLoader classLoader, UserGroupCallbackModel userGroupCallbackModel) {
        super(classLoader);
        _propertiesBuilder = new PropertiesBuilder();
        String location = DEFAULT_PROPERTIES_LOCATION;
        if (location.startsWith("classpath:")) {
            location = location.replaceFirst("classpath:", "");
        } else if (location.startsWith("file:")) {
            location = location.replaceFirst("file:", "");
        }
        PropertiesType propertiesType = PropertiesType.PROPERTIES;
        if (location != null && location.endsWith(".xml")) {
            propertiesType = PropertiesType.XML;
        }
        Properties defaultProperties = new PropertiesPuller(propertiesType).pullPath(location, getClassLoader(), PathType.values());
        _propertiesBuilder.setDefaultProperties(defaultProperties);
        if (userGroupCallbackModel != null) {
            _userGroupCallbackClass = (Class<? extends UserGroupCallback>)userGroupCallbackModel.getClazz(getClassLoader());
            _propertiesBuilder.setModelProperties(userGroupCallbackModel.getProperties());
        }
    }

    /**
     * Builds a UserGroupCallback.
     * @return a UserGroupCallback
     */
    public UserGroupCallback build() {
        UserGroupCallback callback = null;
        Properties properties = _propertiesBuilder.build();
        if (_userGroupCallbackClass != null) {
            callback = construct(properties);
        }
        if (callback == null && USER_CALLBACK_IMPL != null) {
            try {
                if ("props".equalsIgnoreCase(USER_CALLBACK_IMPL)) {
                    callback = new JBossUserGroupCallbackImpl(properties);
                } else {
                    callback = UserDataServiceProvider.getUserGroupCallback();
                }
            } catch (Throwable t) {
                // we might get here if on Karaf vs. JBoss
                // keep checkstyle happy ("at least one statement")
                t.getMessage();
            }
        }
        if (callback == null) {
            callback = new PropertiesUserGroupCallback(properties);
        }
        return callback;
    }

    private UserGroupCallback construct(Properties properties) {
        UserGroupCallback userGroupCallback = null;
        Constructor<? extends UserGroupCallback> constructor = null;
        for (Class<?>[] parameterTypes : PARAMETER_TYPES) {
            try {
                constructor = _userGroupCallbackClass.getConstructor(parameterTypes);
                if (constructor != null) {
                    break;
                }
            } catch (Throwable t) {
                // keep checkstyle happy ("at least one statement")
                t.getMessage();
            }
        }
        Class<?>[] parameterTypes = constructor != null ? constructor.getParameterTypes() : new Class<?>[0];
        try {
            if (parameterTypes.length == 0) {
                userGroupCallback = Construction.construct(_userGroupCallbackClass);
            } else if (parameterTypes.length == 1) {
                userGroupCallback = Construction.construct(_userGroupCallbackClass, parameterTypes, new Object[]{properties});
            }
        } catch (Throwable t) {
            throw CommonKnowledgeMessages.MESSAGES.couldNotInstantiateUserGroupCallbackClass(_userGroupCallbackClass.getName());
        }
        return userGroupCallback;
    }

    /**
     * Creates a UserGroupCallbackBuilder.
     * @param classLoader classLoader
     * @param implementationModel implementationModel
     * @return a UserGroupCallbackBuilder
     */
    public static UserGroupCallbackBuilder builder(ClassLoader classLoader, KnowledgeComponentImplementationModel implementationModel) {
        UserGroupCallbackModel userGroupCallbackModel = implementationModel != null ? implementationModel.getUserGroupCallback() : null;
        return new UserGroupCallbackBuilder(classLoader, userGroupCallbackModel);
    }

}
