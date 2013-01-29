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
package org.switchyard.console.components.client.ui;

import org.switchyard.console.components.client.model.Component;

import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.View;

/**
 * ComponentConfigurationPresenter
 * 
 * This class defines the API for the component presenter in the core console
 * UI. Component extensions must extend this class for their presenter logic.
 * 
 * @author Rob Cernich
 */
public abstract class ComponentConfigurationPresenter extends
        PresenterWidget<ComponentConfigurationPresenter.ComponentConfigurationView> {

    /**
     * ComponentConfigurationView
     * 
     * This class defines the view API for the component view in the core
     * console UI. Component extensions must extend this class for their view
     * logic.
     * 
     * @author Rob Cernich
     */
    public interface ComponentConfigurationView extends View {

        /**
         * @param presenter the presenter managing the view.
         */
        public void setPresenter(ComponentConfigurationPresenter presenter);

        /**
         * @param component the component being edited/viewed.
         */
        public void setComponent(Component component);
    }

    /**
     * Create a new ComponentConfigurationPresenter.
     * 
     * @param eventBus the EventBus.
     * @param view the view.
     */
    protected ComponentConfigurationPresenter(final EventBus eventBus, final ComponentConfigurationView view) {
        super(false, eventBus, view);
    }

    /**
     * @param component the component being edited/viewed
     */
    public void setComponent(Component component) {
        getView().setComponent(component);
    }

}
