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
package org.switchyard.console.client.ui.artifacts;

import java.util.List;

import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.as.console.client.shared.viewframework.builder.SimpleLayout;
import org.switchyard.console.client.Singleton;
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
        _applicationsList = new ApplicationsList(Singleton.MESSAGES.label_applicationsUsingArtifacts());

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
                .setTitle(Singleton.MESSAGES.label_switchYardArtifactReferences())
                .setHeadline(Singleton.MESSAGES.label_artifactReferences())
                .setDescription(
                        Singleton.MESSAGES.description_artifactReferences())
                .addContent(Singleton.MESSAGES.label_artifactReferences(), _artifactReferencesList.asWidget())
                .addContent(Singleton.MESSAGES.label_referencingApplications(), _applicationsList.asWidget());

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
