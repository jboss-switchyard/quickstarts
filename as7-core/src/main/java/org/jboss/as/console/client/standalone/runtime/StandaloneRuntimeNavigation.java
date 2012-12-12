package org.jboss.as.console.client.standalone.runtime;

import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.as.console.client.core.NameTokens;
import org.jboss.as.console.client.shared.SubsystemMetaData;
import org.jboss.as.console.client.shared.model.SubsystemRecord;
import org.jboss.as.console.client.widgets.nav.Predicate;
import org.jboss.ballroom.client.layout.LHSNavTree;
import org.jboss.ballroom.client.layout.LHSNavTreeItem;
import org.jboss.ballroom.client.widgets.stack.DisclosureStackPanel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Heiko Braun
 * @date 11/2/11
 */
public class StandaloneRuntimeNavigation {

    private VerticalPanel stack;
    private VerticalPanel layout;
    private LHSNavTree subsysTree;
    private List<SubsystemRecord> subsystems;


    private List<Predicate> metricPredicates = new ArrayList<Predicate>();
    private List<Predicate> runtimePredicates = new ArrayList<Predicate>();

    private LHSNavTree metricTree;
    private LHSNavTree runtimeOpTree;

    private ScrollPanel scroll;

    public Widget asWidget()
    {
        layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");

        stack = new VerticalPanel();
        stack.setStyleName("fill-layout-width");

        // ----------------------------------------------------

        subsysTree = new LHSNavTree("standalone-runtime");


        // ----------------------------------------------------

        LHSNavTree statusTree = new LHSNavTree("standalone-runtime");



        LHSNavTreeItem server = new LHSNavTreeItem("Configuration", NameTokens.StandaloneServerPresenter);
        LHSNavTreeItem jvmItem = new LHSNavTreeItem("JVM", NameTokens.VirtualMachine);
        statusTree.addItem(server);
        statusTree.addItem(jvmItem);

        DisclosurePanel serverPanel  = new DisclosureStackPanel("Server Status", true).asWidget();
        serverPanel.setContent(statusTree);
        stack.add(serverPanel);


        // -------------

        metricTree = new LHSNavTree("standalone-runtime");

        LHSNavTreeItem datasources = new LHSNavTreeItem("Datasources", "ds-metrics");
        LHSNavTreeItem jmsQueues = new LHSNavTreeItem("JMS Destinations", "jms-metrics");
        LHSNavTreeItem web = new LHSNavTreeItem("Web", "web-metrics");
        LHSNavTreeItem tx = new LHSNavTreeItem("Transactions", "tx-metrics");
        LHSNavTreeItem jpa = new LHSNavTreeItem("JPA", NameTokens.JPAMetricPresenter);

        metricPredicates.add(new Predicate("datasources", datasources));
        metricPredicates.add(new Predicate("messaging", jmsQueues));
        metricPredicates.add(new Predicate("web", web));
        metricPredicates.add(new Predicate("transactions", tx));
        metricPredicates.add(new Predicate("jpa", jpa));

        metricPredicates.addAll(SubsystemMetaData.getRuntimeMetricsExtensions());

        DisclosurePanel metricPanel  = new DisclosureStackPanel("Subsystem Metrics").asWidget();
        metricPanel.setContent(metricTree);
        stack.add(metricPanel);

        // ---

        runtimeOpTree = new LHSNavTree("standalone-runtime");

        LHSNavTreeItem osgi = new LHSNavTreeItem("OSGi", NameTokens.OSGiRuntimePresenter);

        runtimePredicates.add(new Predicate("osgi", osgi));

        runtimePredicates.addAll(SubsystemMetaData.getRuntimeOperationsExtensions());

        DisclosurePanel runtimeOpPanel  = new DisclosureStackPanel("Runtime Operations").asWidget();
        runtimeOpPanel.setContent(runtimeOpTree);
        stack.add(runtimeOpPanel);

        // ----------------------------------------------------

        Tree deploymentTree = new LHSNavTree("standalone-runtime");
        DisclosurePanel deploymentPanel  = new DisclosureStackPanel("Deployments").asWidget();
        deploymentPanel.setContent(deploymentTree);

        deploymentTree.addItem(new LHSNavTreeItem("Manage Deployments", NameTokens.DeploymentListPresenter));
        deploymentTree.addItem(new LHSNavTreeItem("Webservices", NameTokens.WebServiceRuntimePresenter));

        stack.add(deploymentPanel);

        // ---

        layout.add(stack);

        scroll = new ScrollPanel(layout);

        return scroll;
    }

    public void setSubsystems(List<SubsystemRecord> subsystems) {


        metricTree.removeItems();
        runtimeOpTree.removeItems();

        // match subsystems
        for(SubsystemRecord subsys : subsystems)
        {

            //System.out.println(subsys.getKey());

            for(Predicate predicate : metricPredicates)
            {
                if(predicate.matches(subsys.getKey()))
                    metricTree.addItem(predicate.getNavItem());
            }

            for(Predicate predicate : runtimePredicates)
            {
                if(predicate.matches(subsys.getKey()))
                    runtimeOpTree.addItem(predicate.getNavItem());
            }
        }

    }
}
