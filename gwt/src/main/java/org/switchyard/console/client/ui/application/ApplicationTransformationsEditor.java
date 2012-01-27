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
