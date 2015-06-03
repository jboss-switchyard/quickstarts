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

import com.google.web.bindery.event.shared.EventBus;

/**
 * BaseComponentConfigurationPresenter
 * 
 * A base implementation for ComponentConfigurationPresenter.
 * 
 * @author Rob Cernich
 */
public class BaseComponentConfigurationPresenter extends ComponentConfigurationPresenter {

    /**
     * Create a new BaseComponentConfigurationPresenter.
     * 
     * @param eventBus the EventBus.
     * @param view the view.
     */
    public BaseComponentConfigurationPresenter(EventBus eventBus, ComponentConfigurationView view) {
        super(eventBus, view);
    }

    @Override
    protected void onBind() {
        super.onBind();
        getView().setPresenter(this);
    }

    @Override
    protected void onUnbind() {
        getView().setPresenter(null);
        super.onUnbind();
    }

    @Override
    protected void onReset() {
        super.onReset();
        // nothing to do
    }

}
