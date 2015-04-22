package org.jboss.as.console.client.domain.runtime;

import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.Console;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.domain.hosts.ServerPicker;
import org.jboss.as.console.client.domain.model.Host;
import org.jboss.as.console.client.domain.model.ServerInstance;
import org.jboss.as.console.client.shared.SubsystemMetaData;
import org.jboss.as.console.client.shared.model.SubsystemRecord;
import org.jboss.as.console.client.widgets.nav.Predicate;
import org.jboss.ballroom.client.layout.LHSNavTree;
import org.jboss.ballroom.client.layout.LHSNavTreeItem;
import org.jboss.ballroom.client.layout.LHSTreeSection;
import org.jboss.ballroom.client.widgets.forms.ComboBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 11/2/11
 */
class DomainRuntimeNavigation {

    private VerticalPanel stack;
    private VerticalPanel layout;

    private ServerPicker serverPicker;

    private List<Predicate> metricPredicates = new ArrayList<Predicate>();
    private List<Predicate> runtimePredicates = new ArrayList<Predicate>();

    private ScrollPanel scroll;
    private LHSNavTree navigation;
    private LHSTreeSection metricLeaf;
    private LHSTreeSection runtimeLeaf;

    public Widget asWidget()
    {
        layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");

        stack = new VerticalPanel();
        stack.setStyleName("fill-layout-width");


        // ----------------------------------------------------

        serverPicker = new ServerPicker();
        stack.add(serverPicker.asWidget());

        // ----------------------------------------------------

        navigation = new LHSNavTree("domain-runtime");
        navigation.getElement().setAttribute("aria-label", "Profile Tasks");

        //Tree statusTree = new LHSNavTree("domain-runtime");

        LHSTreeSection domainLeaf = new LHSTreeSection("Domain");
        navigation.addItem(domainLeaf);


        //domainLeaf.addItem(new LHSNavTreeItem("Overview", ""));

        LHSNavTreeItem serverInstances= new LHSNavTreeItem(Console.CONSTANTS.common_label_serverInstances(), NameTokens.InstancesPresenter);
        domainLeaf.addItem(serverInstances);
        domainLeaf.addItem(serverInstances);

        domainLeaf.addItem(new LHSNavTreeItem("Manage Deployments", NameTokens.DeploymentsPresenter));


        //DisclosurePanel statusPanel  = new DisclosureStackPanel("Domain Status").asWidget();
        //statusPanel.setContent(statusTree);
        //stack.add(statusPanel);

        // -------------

        metricLeaf = new LHSTreeSection("Server Status");
        navigation.addItem(metricLeaf);

        LHSNavTreeItem datasources = new LHSNavTreeItem("Datasources", NameTokens.DataSourceMetricPresenter);
        LHSNavTreeItem jmsQueues = new LHSNavTreeItem("JMS Destinations", NameTokens.JmsMetricPresenter);
        LHSNavTreeItem web = new LHSNavTreeItem("Web", NameTokens.WebMetricPresenter);
        LHSNavTreeItem tx = new LHSNavTreeItem("Transactions", NameTokens.TXMetrics);
        LHSNavTreeItem jpa = new LHSNavTreeItem("JPA", NameTokens.JPAMetricPresenter);
        LHSNavTreeItem naming = new LHSNavTreeItem("JNDI View", NameTokens.JndiPresenter);


        metricPredicates.add(new Predicate("datasources", datasources));
        metricPredicates.add(new Predicate("messaging", jmsQueues));
        metricPredicates.add(new Predicate("web", web));
        metricPredicates.add(new Predicate("transactions", tx));
        metricPredicates.add(new Predicate("jpa", jpa));
        metricPredicates.add(new Predicate("naming", naming));

        // BEGIN: SwitchYard additions
        metricPredicates.addAll(SubsystemMetaData.getRuntimeMetricsExtensions());
        // END: SwitchYard additions

        // ---

        runtimeLeaf = new LHSTreeSection("Runtime Operations");
        navigation.addItem(runtimeLeaf);

        LHSNavTreeItem osgi = new LHSNavTreeItem("OSGi", NameTokens.OSGiRuntimePresenter);
        runtimePredicates.add(new Predicate("osgi", osgi));

        // BEGIN: SwitchYard additions
        runtimePredicates.addAll(SubsystemMetaData.getRuntimeOperationsExtensions());
        // END: SwitchYard additions

        // ----------------------------------------------------

        navigation.expandTopLevel();

        stack.add(navigation);

        layout.add(stack);

        scroll = new ScrollPanel(layout);

        return scroll;
    }

    public void setHosts(List<Host> hosts) {

        serverPicker.setHosts(hosts);

    }

    public void setServer(List<ServerInstance> server) {

        serverPicker.setServers(server);
    }

    public void setSubsystems(List<SubsystemRecord> subsystems) {


        metricLeaf.removeItems();
        runtimeLeaf.removeItems();

        LHSNavTreeItem jvm = new LHSNavTreeItem("JVM", NameTokens.HostVMMetricPresenter);
        metricLeaf.addItem(jvm);

        // match subsystems
        for(SubsystemRecord subsys : subsystems)
        {

            for(Predicate predicate : metricPredicates)
            {
                if(predicate.matches(subsys.getKey()))
                    metricLeaf.addItem(predicate.getNavItem());
            }

            for(Predicate predicate : runtimePredicates)
            {
                if(predicate.matches(subsys.getKey()))
                    runtimeLeaf.addItem(predicate.getNavItem());
            }
        }

        final LHSNavTreeItem webservices = new LHSNavTreeItem("Webservices", NameTokens.WebServiceRuntimePresenter);
        metricLeaf.addItem(webservices);

        navigation.expandTopLevel();

    }

}
