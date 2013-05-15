package org.switchyard.console.client.ui.common;

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
 * Modified version from AS7 core.
 * 
 * @author Heiko Braun
 */
public class PlainColumnView implements Sampler {

    private Column[] _columns = null;
    private FlexTable _grid;
    private String _title;
    private int _rowOffset = 1;

    private List<StackedBar> _stacks = new LinkedList<StackedBar>();

    // default width and height
    private int _width = 100;
    private Style.Unit _unit = Style.Unit.PCT;
    private HelpSystem.AddressCallback _address = null;
    private StaticHelpPanel _staticHelp;
    private Map<Column, Integer> _columnIndexes = new HashMap<Column, Integer>();

    /**
     * Create a new PlainColumnView.
     * 
     * @param title the title
     */
    public PlainColumnView(String title) {
        this._title = title;
    }

    /**
     * @param columns the columns to be displayed
     * @return this
     */
    public PlainColumnView setColumns(Column... columns) {
        this._columns = columns;
        return this;
    }

    /**
     * @param width the width of the view
     * @param unit the units for the width
     * @return this
     */
    public PlainColumnView setWidth(int width, Style.Unit unit) {
        this._width = width;
        this._unit = unit;
        return this;
    }

    @Override
    public Widget asWidget() {

        VerticalPanel layout = new VerticalPanel();
        layout.setStyleName("fill-layout-width");

        layout.add(new HTML("<div class='metric-table-title'>" + _title + "</div>"));

        _grid = new FlexTable();
        _grid.getElement().setAttribute("width", _width + _unit.getType() + "");

        // header columns
        _grid.setHTML(0, 0, "Metric");
        _grid.setHTML(0, 1, "Actual");
        _grid.setHTML(0, 2, "&nbsp;");

        _grid.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_RIGHT);

        // actual values
        int row = _rowOffset;
        for (Column c : _columns) {
            _grid.setHTML(row, 0, "<div class='metric-table-label'>" + c.getLabel() + ":</div>");
            _grid.setHTML(row, 1, "");

            _stacks.add(new StackedBar());

            if (c.getComparisonColumn() != null) {
                StackedBar stack = _stacks.get(row - _rowOffset);
                _grid.setWidget(row, 2, stack.asWidget());
                stack.setRatio(0, 0);
            } else {
                _grid.setText(row, 2, "");
            }

            _grid.getCellFormatter().setHorizontalAlignment(row, 1, HasHorizontalAlignment.ALIGN_RIGHT);

            if (!c.isVisible()) {
                _grid.getRowFormatter().setVisible(row, false);
            }
            if (c.isBaseline()) {
                _grid.getRowFormatter().addStyleName(row, "metric-table-baseline");
            }

            _columnIndexes.put(c, row - _rowOffset);

            row++;
        }

        _grid.getCellFormatter().setStyleName(0, 0, "metric-table-header");
        _grid.getCellFormatter().setStyleName(0, 1, "metric-table-header");
        _grid.getCellFormatter().setStyleName(0, 2, "metric-table-header");
        _grid.getCellFormatter().setWidth(0, 2, "50%");

        if (null == _staticHelp && _address != null) {
            MetricHelpPanel helpPanel = new MetricHelpPanel(_address, this._columns);
            // helpPanel.setAligned(true);
            layout.add(helpPanel.asWidget());
        } else if (_staticHelp != null) {
            layout.add(_staticHelp.asWidget());
        }

        layout.add(_grid);

        return layout;
    }

    @Override
    public void addSample(Metric metric) {
        int row = _rowOffset;
        int baselineIndex = getBaseLineIndex();

        // check if they match
        if (baselineIndex > metric.numSamples()) {
            throw new RuntimeException("Illegal baseline index " + baselineIndex + " on number of samples "
                    + metric.numSamples());
        }

        Long baseline = baselineIndex >= 0 ? Long.valueOf(metric.get(baselineIndex)) : -1;

        for (Column c : _columns) {
            int dataIndex = row - _rowOffset;
            String actualValue = metric.get(dataIndex);

            if (null == actualValue) {
                throw new RuntimeException("Metric value at index " + dataIndex + " is null");
            }

            _grid.setText(row, 1, actualValue);

            if (c.getComparisonColumn() != null) {
                _stacks.get(dataIndex).setRatio(getComparisonValue(metric, c.getComparisonColumn()),
                        Double.valueOf(actualValue));
            }
            row++;
        }

    }

    /**
     * @return the baseline column index
     */
    public int getBaseLineIndex() {
        int i = 0;
        boolean didMatch = false;
        for (Column c : _columns) {
            if (c.isBaseline()) {
                didMatch = true;
                break;
            }
            i++;
        }

        return didMatch ? i : -1;
    }

    @Override
    public void clearSamples() {
        int row = _rowOffset;

        for (Column c : _columns) {
            int dataIndex = row - _rowOffset;

            // clear the 'Actual' value
            _grid.setText(row, 1, "");

            // cleanup stackbar if used
            if (c.getComparisonColumn() != null) {
                _stacks.get(dataIndex).setRatio(0, 0);
            } else if (c.getComparisonColumn() != null) {
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

    /**
     * @param helpPanel the help panel
     * @return this
     */
    public PlainColumnView setStaticHelp(StaticHelpPanel helpPanel) {
        this._staticHelp = helpPanel;
        return this;
    }

    private long getComparisonValue(Metric metric, Column comparisonColumn) {
        return Long.parseLong(metric.get(_columnIndexes.get(comparisonColumn)));
    }
}
