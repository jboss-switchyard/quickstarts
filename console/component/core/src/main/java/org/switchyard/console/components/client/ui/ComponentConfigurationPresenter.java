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
