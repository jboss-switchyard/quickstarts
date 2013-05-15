/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
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

package org.switchyard.console.client.ui.reference;

import java.util.List;

import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.as.console.client.shared.viewframework.builder.SimpleLayout;
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
                .setTitle("SwitchYard References")
                .setHeadline("References")
                .setDescription(
                        "Displays a list of deployed SwitchYard references.  Select a reference to see more details.")
                .addContent("References", _referencesList.asWidget())
                .addContent("Reference Details", _referenceEditor.asWidget());

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
