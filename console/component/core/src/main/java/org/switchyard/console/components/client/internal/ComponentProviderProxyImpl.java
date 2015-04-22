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
package org.switchyard.console.components.client.internal;

import org.switchyard.console.components.client.extension.ComponentProvider;
import org.switchyard.console.components.client.internal.ComponentExtensionManager.ComponentProviderProxy;
import org.switchyard.console.components.client.ui.ComponentConfigurationPresenter;
import org.switchyard.console.components.client.ui.ComponentConfigurationPresenter.ComponentConfigurationView;

import com.google.web.bindery.event.shared.EventBus;

/**
 * ComponentProviderProxyImpl
 * 
 * Wraps a ComponentProvider annotated with a ComponentExtension.
 * 
 * @see ComponentExtension
 * @see ComponentExtensionManager
 * 
 * @author Rob Cernich
 */
public abstract class ComponentProviderProxyImpl implements ComponentProviderProxy {

    private String _displayName;
    private ComponentProvider _delegate;

    /**
     * Create a new ComponentProviderProxyImpl.
     * 
     * @param displayName the displayName specified in the annotation.
     */
    protected ComponentProviderProxyImpl(String displayName) {
        _displayName = displayName;
    }

    /**
     * @return the displayName specified on the annotated ComponentProvider.
     */
    public String getDisplayName() {
        return _displayName;
    }

    @Override
    public ComponentConfigurationPresenter createConfigurationPresenter(EventBus eventBus,
            ComponentConfigurationView view) {
        return getDelegate().createConfigurationPresenter(eventBus, view);
    }

    @Override
    public ComponentConfigurationView createConfigurationView() {
        return getDelegate().createConfigurationView();
    }

    /**
     * @return a new ComponentProvider.
     */
    protected abstract ComponentProvider instantiate();

    private ComponentProvider getDelegate() {
        if (_delegate == null) {
            _delegate = instantiate();
        }
        return _delegate;
    }
}
