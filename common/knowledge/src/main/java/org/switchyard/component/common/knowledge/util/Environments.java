/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.component.common.knowledge.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.impl.EnvironmentFactory;
import org.kie.marshalling.ObjectMarshallingStrategy;
import org.kie.runtime.Environment;
import org.kie.runtime.EnvironmentName;
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
