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

package org.switchyard.console.client.ui.application;

import org.switchyard.console.client.model.Application;
import org.switchyard.console.client.ui.common.TransformersList;

import com.google.gwt.user.client.ui.Widget;

/**
 * ApplicationTransformationsEditor
 * 
 * Editor widget for SwitchYard application transform details.
 * 
 * @author Rob Cernich
 */
public class ApplicationTransformationsEditor {

    private ApplicationPresenter _presenter;
    private TransformersList _transformersList;

    /**
     * Create a new ApplicationTransformationsEditor.
     * 
     * @param presenter the associated presenter.
     */
    public ApplicationTransformationsEditor(ApplicationPresenter presenter) {
        _presenter = presenter;
    }

    /**
     * @return this editor as a Widget.
     */
    public Widget asWidget() {
        _transformersList = new TransformersList();

        return _transformersList.asWidget();
    }

    /**
     * @param application the application being edited by this editor.
     */
    public void setApplication(Application application) {
        _transformersList.setData(application.getTransformers());
    }

}
