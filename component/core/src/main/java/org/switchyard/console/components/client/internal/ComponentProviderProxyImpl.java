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
package org.switchyard.console.components.client.internal;

import org.switchyard.console.components.client.extension.ComponentProvider;
import org.switchyard.console.components.client.internal.ComponentExtensionManager.ComponentProviderProxy;
import org.switchyard.console.components.client.ui.ComponentConfigurationPresenter;
import org.switchyard.console.components.client.ui.ComponentConfigurationPresenter.ComponentConfigurationView;

import com.google.gwt.event.shared.EventBus;

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
