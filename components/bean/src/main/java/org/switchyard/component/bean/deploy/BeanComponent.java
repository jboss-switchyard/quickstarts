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
package org.switchyard.component.bean.deploy;

import org.switchyard.ServiceDomain;
import org.switchyard.deploy.Activator;
import org.switchyard.deploy.BaseComponent;

/**
 * An implementation of Bean component.
 *
 * @author Magesh Kumar B <mageshbk@jboss.com> (C) 2011 Red Hat Inc.
 */
public class BeanComponent extends BaseComponent {

    /**
     * Default constructor.
     */
    public BeanComponent() {
        super(BeanComponentActivator.BEAN_TYPE);
        setName("BeanComponent");
    }

    /* (non-Javadoc)
     * @see org.switchyard.deploy.Component#createActivator(org.switchyard.ServiceDomain)
     */
    @Override
    public Activator createActivator(ServiceDomain domain) {
        BeanComponentActivator activator = new BeanComponentActivator();
        activator.setServiceDomain(domain);
        return activator;
    }

}
