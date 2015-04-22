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

package org.jboss.as.console.client;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtplatform.mvp.client.DelayedBindRegistry;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import org.jboss.as.console.client.core.BootstrapContext;
import org.jboss.as.console.client.core.LoadingPanel;
import org.jboss.as.console.client.core.UIConstants;
import org.jboss.as.console.client.core.UIDebugConstants;
import org.jboss.as.console.client.core.UIMessages;
import org.jboss.as.console.client.core.bootstrap.BootstrapProcess;
import org.jboss.as.console.client.core.bootstrap.ChoseProcessor;
import org.jboss.as.console.client.core.bootstrap.EagerLoadProfiles;
import org.jboss.as.console.client.core.bootstrap.ExecutionMode;
import org.jboss.as.console.client.core.bootstrap.LoadMainApp;
import org.jboss.as.console.client.core.bootstrap.RegisterSubsystems;
import org.jboss.as.console.client.core.bootstrap.RemoveLoadingPanel;
import org.jboss.as.console.client.core.gin.Composite;
import org.jboss.as.console.client.core.gin.GinjectorSingleton;
import org.jboss.as.console.client.core.message.Message;
import org.jboss.as.console.client.core.message.MessageCenter;
import org.jboss.as.console.client.plugins.SubsystemRegistry;
import org.jboss.as.console.client.shared.help.HelpSystem;

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

    public final static Composite MODULES = GWT.<GinjectorSingleton>create(GinjectorSingleton.class).getCoreUI();
    public final static UIConstants CONSTANTS = GWT.create(UIConstants.class);
    public final static UIDebugConstants DEBUG_CONSTANTS = GWT.create(UIDebugConstants.class);
    public final static UIMessages MESSAGES = GWT.create(UIMessages.class);

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

    public void onModuleLoad2() {

        // load console css bundle
        ConsoleResources.INSTANCE.css().ensureInjected();

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
                final BootstrapProcess bootstrap = new BootstrapProcess();

                bootstrap.addHook(new ExecutionMode(MODULES.getBootstrapContext(), MODULES.getDispatchAsync()));
                bootstrap.addHook(new RegisterSubsystems(MODULES.getSubsystemRegistry()));
                bootstrap.addHook(new ChoseProcessor(MODULES.getBootstrapContext()));
                bootstrap.addHook(new EagerLoadProfiles(MODULES.getProfileStore(), MODULES.getCurrentSelectedProfile()));
                bootstrap.addHook(new RemoveLoadingPanel(loadingPanel));
                bootstrap.addHook(new LoadMainApp(MODULES.getBootstrapContext(), MODULES.getPlaceManager(), MODULES.getTokenFormatter()));

                // viz can be loaded in background ...
                //bootstrap.addHook(new LoadGoogleViz());

                bootstrap.execute(new AsyncCallback<Boolean>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        error("Bootstrap failed", caught.getMessage());
                    }

                    @Override
                    public void onSuccess(Boolean wasSuccessfull) {
                        if(!wasSuccessfull)
                        {
                            // currently we only deal with authentication errors
                            RootLayoutPanel.get().remove(loadingPanel);

                            String cause = "";
                            if(MODULES.getBootstrapContext().getLastError()!=null)
                                cause = MODULES.getBootstrapContext().getLastError().getMessage();

                            HTMLPanel explanation = new HTMLPanel("<center><div style='padding-top:150px;'><h2>The web console could not be loaded.</h2>"+cause+"</div></center>");
                            RootLayoutPanel.get().add(explanation);
                        }

                    }
                });
            }

        });
    }

    public static void info(String message) {
        getMessageCenter().notify(
                new Message(message, Message.Severity.Info)
        );
    }

    public static void error(String message) {
        getMessageCenter().notify(
                new Message(message, Message.Severity.Error)
        );
    }

    public static void error(String message, String detail) {
        getMessageCenter().notify(
                new Message(message, detail, Message.Severity.Error)
        );
    }

    public static void warning(String message) {
        getMessageCenter().notify(
                new Message(message, Message.Severity.Warning)
        );
    }

    public static void warning(String message, boolean sticky) {
        Message msg = sticky ?
                new Message(message, Message.Severity.Warning, EnumSet.of(Message.Option.Sticky)) :
                new Message(message, Message.Severity.Warning);

        getMessageCenter().notify(msg);
    }

    public static void warning(String message, String detail, boolean sticky) {
        Message msg = sticky ?
                new Message(message, detail, Message.Severity.Warning, EnumSet.of(Message.Option.Sticky)) :
                new Message(message, detail, Message.Severity.Warning);


        getMessageCenter().notify(msg);
    }

    public static void warning(String message, String detail) {
        getMessageCenter().notify(
                new Message(message, detail, Message.Severity.Warning)
        );
    }

    public static void schedule(final Command cmd)
    {
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                cmd.execute();
            }
        });
    }

    public static EventBus getEventBus() {
        return MODULES.getEventBus();
    }

    public static MessageCenter getMessageCenter() {
        return MODULES.getMessageCenter();
    }

    public static PlaceManager getPlaceManager() {
        return MODULES.getPlaceManager();
    }

    public static BootstrapContext getBootstrapContext()
    {
        return MODULES.getBootstrapContext();
    }

    public static HelpSystem getHelpSystem() {
        return MODULES.getHelpSystem();
    }


    @Deprecated
    public static native boolean visAPILoaded() /*-{
        if ($wnd['google'] && $wnd.google['load']) {
            return true;
        }
        return false;
    }-*/;


    public static SubsystemRegistry getSubsystemRegistry() {
        return MODULES.getSubsystemRegistry();
    }
}
