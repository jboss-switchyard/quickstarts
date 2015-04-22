package org.jboss.as.console.client.shared.runtime.plain;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jboss.as.console.client.shared.help.HelpSystem;
import org.jboss.as.console.client.shared.help.MetricHelpPanel;
import org.jboss.as.console.client.shared.help.StaticHelpPanel;
import org.jboss.as.console.client.shared.runtime.Metric;
import org.jboss.as.console.client.shared.runtime.Sampler;
import org.jboss.as.console.client.shared.runtime.charts.Column;
import org.jboss.as.console.client.shared.runtime.charts.StackedBar;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * @author Heiko Braun
 * @date 10/25/11
 */
public class PlainColumnView implements Sampler {

    private Column[] columns = null;
    private FlexTable grid;
    private String title;
    private int ROW_OFFSET = 1;

    private List<StackedBar> stacks = new LinkedList<StackedBar>();

    // default width and height
    private int width = 100;
    private Style.Unit unit = Style.Unit.PCT;
    private HelpSystem.AddressCallback address = null;
    private StaticHelpPanel staticHelp;
    private Map<Column, Integer> columnIndexes = new HashMap<Column, Integer>();

    @Deprecated
    public PlainColumnView(String title) {
        this.title = title;
    }

    public PlainColumnView(String title, HelpSystem.AddressCallback address) {
        this.title = title;
        this.address = address;
    }

    public PlainColumnView setColumns(Column... columns) {
        this.columns = columns;
        return this;
    }

    public PlainColumnView setWidth(int width, Style.Unit unit) {
        this.width = width;
        this.unit = unit;
        return this;
    }

    @Override
    public Widget asWidget() {


        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");

        layout.add(new HTML("<div class='metric-table-title'>"+title+"</div>"));

        grid = new FlexTable();
        grid.getElement().setAttribute("width", width+unit.getType()+"");

        // header columns
        grid.setHTML(0, 0, "Metric");
        grid.setHTML(0, 1, "Actual");
        grid.setHTML(0, 2, "&nbsp;");

        grid.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_RIGHT);

        // actual values
        int row = ROW_OFFSET;
        for(Column c : columns)
        {
            grid.setHTML(row, 0, "<div class='metric-table-label'>"+c.getLabel() + ":</div>");
            grid.setHTML(row, 1, "");

            stacks.add(new StackedBar());

            if(c.getComparisonColumn()!=null)
            {
                StackedBar stack = stacks.get(row - ROW_OFFSET);
                grid.setWidget(row, 2, stack.asWidget());
                stack.setRatio(0,0);
            }
            else
                grid.setText(row, 2, "");

            grid.getCellFormatter().setHorizontalAlignment(row, 1, HasHorizontalAlignment.ALIGN_RIGHT);

            if(!c.isVisible())
                grid.getRowFormatter().setVisible(row, false);
            if(c.isBaseline())
                grid.getRowFormatter().addStyleName(row, "metric-table-baseline");

            columnIndexes.put(c, row - ROW_OFFSET);

            row++;
        }

        grid.getCellFormatter().setStyleName(0,0,"metric-table-header");
        grid.getCellFormatter().setStyleName(0,1,"metric-table-header");
        grid.getCellFormatter().setStyleName(0,2,"metric-table-header");
        grid.getCellFormatter().setWidth(0, 2, "50%");


        if(null==staticHelp && address!=null)
        {
            MetricHelpPanel helpPanel = new MetricHelpPanel(address, this.columns);
            //helpPanel.setAligned(true);
            layout.add(helpPanel.asWidget());
        }
        else if(staticHelp!=null)
        {
            layout.add(staticHelp.asWidget());
        }

        layout.add(grid);

       return layout;
    }

    @Override
    public void addSample(Metric metric) {
        int row=ROW_OFFSET;
        int baselineIndex = getBaseLineIndex();

        // check if they match
        if(baselineIndex>metric.numSamples())
            throw new RuntimeException("Illegal baseline index "+baselineIndex+" on number of samples "+metric.numSamples());

        Long baseline = baselineIndex >= 0 ?
                Long.valueOf(metric.get(baselineIndex)) : -1;

        for(Column c : columns)
        {
            int dataIndex = row - ROW_OFFSET;
            String actualValue = metric.get(dataIndex);

            if(null==actualValue)
                throw new RuntimeException("Metric value at index "+dataIndex+" is null");

            grid.setText(row, 1, actualValue );

            if(c.getComparisonColumn()!=null)
            {
                stacks.get(dataIndex).setRatio(getComparisonValue(metric, c.getComparisonColumn()), Double.valueOf(actualValue));
            }
            row++;
        }

    }

    public int getBaseLineIndex() {
        int i=0;
        boolean didMatch = false;
        for(Column c : columns)
        {
            if(c.isBaseline())
            {
                didMatch=true;
                break;
            }
            i++;
        }

        return didMatch ? i : -1;
    }

    @Override
    public void clearSamples() {
        int row=ROW_OFFSET;

        for(Column c : columns)
        {
            int dataIndex = row - ROW_OFFSET;

            // clear the 'Actual' value
            grid.setText(row, 1, "");

            // cleanup stackbar if used
            if(c.getComparisonColumn()!=null )
            {
                stacks.get(dataIndex).setRatio(0,0);
            }
            else if(c.getComparisonColumn()!=null )
            {
                throw new RuntimeException("Comparison column specified, but no baseline set!");
            }

            row++;
        }
    }

    @Override
    public long numSamples() {
        return 1;
    }

    @Override
    public void recycle() {

    }

    public PlainColumnView setStaticHelp(StaticHelpPanel helpPanel) {
        this.staticHelp = helpPanel;
        return this;
    }
    
    private long getComparisonValue(Metric metric, Column comparisonColumn) {
        return Long.parseLong(metric.get(columnIndexes.get(comparisonColumn)));
    }
}
