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

package org.switchyard.console.client.ui.design;

import org.jboss.as.console.client.core.DisposableViewImpl;
import org.jboss.as.console.client.widgets.RHSContentPanel;
import org.switchyard.console.client.model.SwitchYardDeployment;

import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * PackageView
 * 
 * View for SwitchYard deployment package details.
 * 
 * @author Rob Cernich
 */
public class PackageView extends DisposableViewImpl implements PackagePresenter.MyView {

    private PackagePresenter _presenter;

    private PackageEditor _moduleEditor;

    @Override
    public Widget createWidget() {

        LayoutPanel layout = new RHSContentPanel("Deployment Configuration");

        _moduleEditor = new PackageEditor(_presenter);
        layout.add(_moduleEditor.asWidget());

        return layout;
    }

    @Override
    public void setPresenter(PackagePresenter presenter) {
        this._presenter = presenter;
    }

    @Override
    public void setDeployment(SwitchYardDeployment deployment) {
        _moduleEditor.setDeployment(deployment);
    }

}
