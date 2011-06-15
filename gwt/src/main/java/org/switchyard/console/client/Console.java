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

package org.switchyard.console.client;

import org.jboss.as.console.client.core.UIConstants;
import org.jboss.as.console.client.core.UIMessages;
import org.switchyard.console.client.gin.SwitchYardGinjector;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.gwtplatform.mvp.client.DelayedBindRegistry;

/**
 * Main application entry point. Executes a two phased init process:
 * <ol>
 * <li>Identify management model (standalone vs. domain)
 * <li>Load main application
 * </ol>
 * 
 * Copied from org.jboss.as.console.client.Console
 * 
 * @author Heiko Braun
 * 
 */
public class Console implements EntryPoint {

    /** The SwitchYard module Ginjector. */
    public final static SwitchYardGinjector MODULES = GWT.create(SwitchYardGinjector.class);

    /** UIConstants from as7 console module. */
    public final static UIConstants CONSTANTS = GWT.create(UIConstants.class);

    /** UIMessages from as7 console module. */
    public final static UIMessages MESSAGES = GWT.create(UIMessages.class);

    @Override
    public void onModuleLoad() {
        // Defer all application initialisation code to onModuleLoad2() so that
        // the
        // UncaughtExceptionHandler can catch any unexpected exceptions.
        Log.setUncaughtExceptionHandler();

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                onModuleLoad2();
            }
        });
    }

    private void onModuleLoad2() {

        final Image loadingImage = new Image("images/loading_lite.gif");
        loadingImage.getElement().setAttribute("style", "margin-top:200px;margin-left:auto;margin-right:auto;");

        RootLayoutPanel.get().add(loadingImage);

        GWT.runAsync(new RunAsyncCallback() {
            public void onFailure(Throwable caught) {
                Window.alert("Code download failed");
            }

            public void onSuccess() {
                DelayedBindRegistry.bind(MODULES);
                bootstrap();

                RootLayoutPanel.get().remove(loadingImage);
            }
        });
    }

    private void bootstrap() {
        loadMainApp();
    }

    private void loadMainApp() {
        MODULES.getPlaceManager().revealDefaultPlace();
    }

}
