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

package org.switchyard.console.client.ui.reference;

import java.util.List;

import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.as.console.client.shared.viewframework.builder.SimpleLayout;
import org.switchyard.console.client.Singleton;
import org.switchyard.console.client.model.Reference;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;

/**
 * ReferenceView
 * 
 * View for SwitchYard reference configuration.
 */
public class ReferenceView extends DisposableViewImpl implements ReferencePresenter.MyView {

    private ReferencePresenter _presenter;
    private ReferencesList _referencesList;
    private ReferenceEditor _referenceEditor;
    private Reference _selectedReference;

    /**
     * Create a new ReferenceView.
     */
    public ReferenceView() {
        super();
        _referencesList = new ReferencesList();
        _referencesList.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                // prevent infinite recursion
                if (_referencesList.getSelection() != _selectedReference) {
                    _presenter.onReferenceSelected(_referencesList.getSelection());
                }
            }
        });

        _referenceEditor = new ReferenceEditor();
    }

    @Override
    public Widget createWidget() {
        SimpleLayout layout = new SimpleLayout()
                .setPlain(true)
                .setTitle(Singleton.MESSAGES.label_switchYardReferences())
                .setHeadline(Singleton.MESSAGES.label_references())
                .setDescription(
                        Singleton.MESSAGES.description_switchYardReferences())
                .addContent(Singleton.MESSAGES.label_references(), _referencesList.asWidget())
                .addContent(Singleton.MESSAGES.label_referenceDetails(), _referenceEditor.asWidget());

        return layout.build();
    }

    @Override
    public void setPresenter(ReferencePresenter presenter) {
        _presenter = presenter;
        _referenceEditor.setPresenter(presenter);
    }

    @Override
    public void setReferencesList(List<Reference> references) {
        _referencesList.setData(references);
    }

    @Override
    public void setReference(Reference reference) {
        _selectedReference = reference;
        _referencesList.setSelection(reference);
        _referenceEditor.setReference(reference);
    }

}
