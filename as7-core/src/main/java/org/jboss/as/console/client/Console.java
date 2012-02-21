/*
* JBoss, Home of Professional Open Source
* Copyright 2011-2012 Red Hat Inc. and/or its affiliates and other contributors
* as indicated by the @author tags. All rights reserved.
* See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* This copyrighted material is made available to anyone wishing to use,
* modify, copy, or redistribute it subject to the terms and conditions
* of the GNU Lesser General Public License, v. 2.1.
* This program is distributed in the hope that it will be useful, but WITHOUT A
* WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
* You should have received a copy of the GNU Lesser General Public License,
* v.2.1 along with this distribution; if not, write to the Free Software
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
* MA 02110-1301, USA.
*/

package org.jboss.as.console.client;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.DelayedBindRegistry;
import org.jboss.as.console.client.core.LoadingPanel;
import org.jboss.as.console.client.core.UIConstants;
import org.jboss.as.console.client.core.UIDebugConstants;
import org.jboss.as.console.client.core.UIMessages;
import org.jboss.as.console.client.core.bootstrap.BootstrapProcess;
import org.jboss.as.console.client.core.bootstrap.ChoseProcessor;
import org.jboss.as.console.client.core.bootstrap.EagerLoadProfiles;
import org.jboss.as.console.client.core.bootstrap.ExecutionMode;
import org.jboss.as.console.client.core.bootstrap.LoadMainApp;
import org.jboss.as.console.client.core.bootstrap.RemoveLoadingPanel;
import org.jboss.as.console.client.core.gin.CoreUI;
import org.jboss.as.console.client.core.gin.GinjectorSingleton;
import org.jboss.as.console.client.core.message.Message;

import java.util.EnumSet;

/**
* Main application entry point.
* Executes a two phased init process:
* <ol>
* <li>Identify management model (standalone vs. domain)
* <li>Load main application
* </ol>
*
* @author Heiko Braun
*/
public class Console implements EntryPoint {

    /** The MODULES. */
    public final static CoreUI MODULES = GWT.<GinjectorSingleton>create(GinjectorSingleton.class).getCoreUI();
    /** The CONSTANTS. */
    public final static UIConstants CONSTANTS = GWT.create(UIConstants.class);
    /** The MESSAGES. */
    public final static UIDebugConstants DEBUG_CONSTANTS = GWT.create(UIDebugConstants.class);
    public final static UIMessages MESSAGES = GWT.create(UIMessages.class);

    @Override
    public void onModuleLoad() {
        // Defer all application initialisation code to onModuleLoad2() so that the
        // UncaughtExceptionHandler can catch any unexpected exceptions.
        Log.setUncaughtExceptionHandler();

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                onModuleLoad2();
            }
        });
    }

    /**
     */
    public void onModuleLoad2() {

        // display the loading panel
        final Widget loadingPanel = new LoadingPanel().asWidget();
        RootLayoutPanel.get().add(loadingPanel);

        GWT.runAsync(new RunAsyncCallback() {
            public void onFailure(Throwable caught) {
                Window.alert("Failed to load application components!");
            }

            public void onSuccess() {
                DelayedBindRegistry.bind(MODULES);

                // ordered bootstrap
                BootstrapProcess bootstrap = new BootstrapProcess();

                bootstrap.addHook(new ExecutionMode(MODULES.getBootstrapContext(), MODULES.getDispatchAsync()));
                bootstrap.addHook(new ChoseProcessor(MODULES.getBootstrapContext()));
                bootstrap.addHook(new EagerLoadProfiles());
                bootstrap.addHook(new RemoveLoadingPanel(loadingPanel));
                bootstrap.addHook(new LoadMainApp(MODULES.getBootstrapContext(), MODULES.getPlaceManager(), MODULES.getTokenFormatter()));

                // viz can be loaded in background ...
                //bootstrap.addHook(new LoadGoogleViz());

                bootstrap.execute();
            }

        });
    }

    /**
     * @param message the message
     */
    public static void info(String message) {
        MODULES.getMessageCenter().notify(
                new Message(message, Message.Severity.Info)
        );
    }

    /**
     * @param message the message
     */
    public static void error(String message) {
        MODULES.getMessageCenter().notify(
                new Message(message, Message.Severity.Error)
        );
    }

    /**
     * @param message the message
     * @param detail details
     */
    public static void error(String message, String detail) {
        MODULES.getMessageCenter().notify(
                new Message(message, detail, Message.Severity.Error)
        );
    }

    /**
     * @param message the message
     */
    public static void warning(String message) {
        MODULES.getMessageCenter().notify(
                new Message(message, Message.Severity.Warning)
        );
    }

    /**
     * @param message the message
     * @param sticky is it sticky
     */
    public static void warning(String message, boolean sticky) {
        Message msg = sticky ?
                new Message(message, Message.Severity.Warning, EnumSet.of(Message.Option.Sticky)) :
                new Message(message, Message.Severity.Warning);

        MODULES.getMessageCenter().notify(msg);
    }

    /**
     * @param message the message
     * @param detail details
     * @param sticky is it sticky
     */
    public static void warning(String message, String detail, boolean sticky) {
        Message msg = sticky ?
                new Message(message, detail, Message.Severity.Warning, EnumSet.of(Message.Option.Sticky)) :
                new Message(message, detail, Message.Severity.Warning);


        MODULES.getMessageCenter().notify(msg);
    }

    /**
     * @param message the message
     * @param detail details
     */
    public static void warning(String message, String detail) {
        MODULES.getMessageCenter().notify(
                new Message(message, detail, Message.Severity.Warning)
        );
    }

    /**
     * @param cmd the command
     */
    public static void schedule(final Command cmd)
    {
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                cmd.execute();
            }
        });
    }

    /**
     * @return true if vis api is loaded.
     */
    public static native boolean visAPILoaded() /*-{
if ($wnd['google'] && $wnd.google['load']) {
return true;
}
return false;
}-*/;

}
