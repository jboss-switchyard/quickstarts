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
package org.switchyard.component.bpm.util;

import java.lang.reflect.Constructor;
import java.util.Properties;

// SWITCHYARD-1755: internal api usage still required (public APIs insufficient)
import org.kie.internal.task.api.UserGroupCallback;
import org.switchyard.common.type.reflect.Construction;
import org.switchyard.component.bpm.BPMMessages;
import org.switchyard.component.bpm.config.model.BPMComponentImplementationModel;
import org.switchyard.component.bpm.config.model.UserGroupCallbackModel;
import org.switchyard.component.bpm.runtime.BPMUserGroupCallback;
import org.switchyard.config.model.property.PropertiesModel;

/**
 * UserGroupCallback functions.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public final class UserGroupCallbacks {

    private static final Class<?>[][] PARAMETER_TYPES = new Class<?>[][]{
        new Class<?>[]{Properties.class},
        new Class<?>[0]
    };

    /**
     * Creates a new user group callback.
     * @param model the model
     * @param loader the loader
     * @return the user group callback
     */
    @SuppressWarnings("unchecked")
    public static UserGroupCallback newUserGroupCallback(BPMComponentImplementationModel model, ClassLoader loader) {
        Class<? extends UserGroupCallback> userGroupCallbackClass = null;
        Properties properties = null;
        UserGroupCallbackModel userGroupCallbackModel = model.getUserGroupCallback();
        if (userGroupCallbackModel != null) {
            userGroupCallbackClass = (Class<? extends UserGroupCallback>)userGroupCallbackModel.getClazz(loader);
            PropertiesModel propertiesModel = userGroupCallbackModel.getProperties();
            properties = propertiesModel != null ? propertiesModel.toProperties() : null;
        }
        if (properties == null) {
            properties = new Properties();
        }
        if (userGroupCallbackClass != null) {
            return newUserGroupCallback(userGroupCallbackClass, properties);
        } else if (properties.size() > 0) {
            return new BPMUserGroupCallback(properties);
        } else {
            return new BPMUserGroupCallback();
        }
    }

    /**
     * Creates a new user group callback.
     * @param userGroupCallbackClass the class
     * @param properties the properties
     * @return the user group callback
     */
    public static UserGroupCallback newUserGroupCallback(Class<? extends UserGroupCallback> userGroupCallbackClass, Properties properties) {
        UserGroupCallback userGroupCallback = null;
        Constructor<? extends UserGroupCallback> constructor = getConstructor(userGroupCallbackClass);
        Class<?>[] parameterTypes = constructor != null ? constructor.getParameterTypes() : new Class<?>[0];
        try {
            if (parameterTypes.length == 0) {
                userGroupCallback = Construction.construct(userGroupCallbackClass);
            } else if (parameterTypes.length == 1) {
                userGroupCallback = Construction.construct(userGroupCallbackClass, parameterTypes, new Object[]{properties});
            }
        } catch (Throwable t) {
            throw BPMMessages.MESSAGES.couldNotInstantiateUserGroupCallbackClass(userGroupCallbackClass.getName());
        }
        return userGroupCallback;
    }

    private static Constructor<? extends UserGroupCallback> getConstructor(Class<? extends UserGroupCallback> userGroupCallbackClass) {
        Constructor<? extends UserGroupCallback> constructor = null;
        for (Class<?>[] parameterTypes : PARAMETER_TYPES) {
            try {
                constructor = userGroupCallbackClass.getConstructor(parameterTypes);
                if (constructor != null) {
                    break;
                }
            } catch (Throwable t) {
                // keep checkstyle happy ("at least one statement")
                t.getMessage();
            }
        }
        return constructor;
    }

    private UserGroupCallbacks() {}

}
