/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
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
package org.switchyard.component.soap.config.model.v1;

import static org.switchyard.component.soap.config.model.InterceptorModel.INTERCEPTOR;
import static org.switchyard.component.soap.config.model.SOAPBindingModel.DEFAULT_NAMESPACE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.switchyard.component.soap.config.model.InterceptorModel;
import org.switchyard.component.soap.config.model.InterceptorsModel;
import org.switchyard.config.Configuration;
import org.switchyard.config.model.BaseModel;
import org.switchyard.config.model.Descriptor;

/**
 * A version 1 InterceptorsModel.
 *
 * @author David Ward &lt;<a href="mailto:dward@jboss.org">dward@jboss.org</a>&gt; &copy; 2013 Red Hat Inc.
 */
public class V1InterceptorsModel extends BaseModel implements InterceptorsModel {

    private List<InterceptorModel> _interceptors = new ArrayList<InterceptorModel>();

    /**
     * Creates a new InterceptorsModel.
     * @param name inInterceptors or outInterceptors
     */
    public V1InterceptorsModel(String name) {
        super(new QName(DEFAULT_NAMESPACE, name));
        setModelChildrenOrder(INTERCEPTOR);
    }

    /**
     * Creates a new InterceptorsModel with the specified configuration and descriptor.
     * @param config the configuration
     * @param desc the descriptor
     */
    public V1InterceptorsModel(Configuration config, Descriptor desc) {
        super(config, desc);
        for (Configuration interceptor_config : config.getChildren(INTERCEPTOR)) {
            InterceptorModel interceptor = (InterceptorModel)readModel(interceptor_config);
            if (interceptor != null) {
                _interceptors.add(interceptor);
            }
        }
        setModelChildrenOrder(INTERCEPTOR);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized List<InterceptorModel> getInterceptors() {
        return Collections.unmodifiableList(_interceptors);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InterceptorsModel addInterceptor(InterceptorModel interceptor) {
        addChildModel(interceptor);
        _interceptors.add(interceptor);
        return this;
    }

}
