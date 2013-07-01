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
package org.switchyard.component.common.knowledge.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.core.impl.EnvironmentFactory;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.switchyard.component.common.knowledge.environment.SerializerObjectMarshallingStrategy;
import org.switchyard.serial.FormatType;
import org.switchyard.serial.SerializerFactory;

/**
 * Environment functions.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2012 Red Hat Inc.
 */
public final class Environments {

    /**
     * Gets an Environment.
     * @param overrides any overrides
     * @return the Environment
     */
    public static Environment getEnvironment(Map<String, Object> overrides) {
        Environment env = EnvironmentFactory.newEnvironment();
        // NOTE: not necessary any more, per mproctor
        //env.set(EnvironmentName.GLOBALS, new MapGlobalResolver());
        // set the object marshalling strategies
        List<ObjectMarshallingStrategy> new_oms = new ArrayList<ObjectMarshallingStrategy>();
        new_oms.add(new SerializerObjectMarshallingStrategy(SerializerFactory.create(FormatType.JSON, null, true)));
        ObjectMarshallingStrategy[] old_oms = (ObjectMarshallingStrategy[])env.get(EnvironmentName.OBJECT_MARSHALLING_STRATEGIES);
        if (old_oms != null) {
            for (int i=0; i < old_oms.length; i++) {
                if (old_oms[i] != null) {
                    new_oms.add(old_oms[i]);
                }
            }
        }
        env.set(EnvironmentName.OBJECT_MARSHALLING_STRATEGIES, new_oms.toArray(new ObjectMarshallingStrategy[new_oms.size()]));
        // apply any overrides
        if (overrides != null) {
            for (Map.Entry<String, Object> entry : overrides.entrySet()) {
                env.set(entry.getKey(), entry.getValue());
            }
        }
        return env;
    }

    private Environments() {}

}
