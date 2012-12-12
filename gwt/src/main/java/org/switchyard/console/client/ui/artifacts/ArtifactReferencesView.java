/* 
 * JBoss, Home of Professional Open Source 
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
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
package org.switchyard.console.client.ui.artifacts;

import java.util.List;

import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.as.console.client.shared.viewframework.builder.SimpleLayout;
import org.switchyard.console.client.model.ArtifactReference;
import org.switchyard.console.client.ui.application.ApplicationsList;
import org.switchyard.console.client.ui.artifacts.ArtifactPresenter.MyView;

import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SelectionChangeEvent.Handler;

/**
 * ArtifactReferencesView
 * 
 * <p/>
 * Displays artifacts and the applications referencing them.
 * 
 * @author Rob Cernich
 */
public class ArtifactReferencesView extends DisposableViewImpl implements MyView {

    private ArtifactPresenter _presenter;
    private ArtifactReferencesList _artifactReferencesList;
    private ApplicationsList _applicationsList;
    private ArtifactReference _selectedArtifact;

    @Override
    public Widget createWidget() {
        _artifactReferencesList = new ArtifactReferencesList();
        _applicationsList = new ApplicationsList("Applications Using Artifact");

        _artifactReferencesList.addSelectionChangeHandler(new Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                if (_artifactReferencesList.getSelection() != _selectedArtifact) {
                    _selectedArtifact = _artifactReferencesList.getSelection();
                    _applicationsList.setData(_selectedArtifact == null ? null : _selectedArtifact.getApplications());
                }
            }
        });

        _applicationsList.addSelectionChangeHandler(new Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                _presenter.onApplicationSelected(_applicationsList.getSelection());
            }
        });

        SimpleLayout layout = new SimpleLayout()
                .setPlain(true)
                .setTitle("SwitchYard Artifact References")
                .setHeadline("Artifact References")
                .setDescription(
                        "Displays all artifacts referenced throughout the system, along with the applications referencing a specific artifact.")
                .addContent("Artifact References", _artifactReferencesList.asWidget())
                .addContent("Referencing Applications", _applicationsList.asWidget());

        return layout.build();
    }

    @Override
    public void setPresenter(ArtifactPresenter presenter) {
        _presenter = presenter;
    }

    @Override
    public void setArtifacts(List<ArtifactReference> artifacts) {
        _artifactReferencesList.setData(artifacts);
    }

    @Override
    public void setSelectedArtifact(String artifactKey) {
        if (artifactKey == null) {
            _artifactReferencesList.setSelection(null);
            return;
        }

        List<ArtifactReference> artifacts = _artifactReferencesList.getData();
        if (artifacts == null) {
            return;
        }
        for (ArtifactReference artifact : artifacts) {
            if (artifactKey.equals(artifact.key())) {
                _artifactReferencesList.setSelection(artifact);
                return;
            }
        }
    }

}
