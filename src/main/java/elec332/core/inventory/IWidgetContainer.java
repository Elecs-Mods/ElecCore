package elec332.core.inventory;

import elec332.core.inventory.widget.Widget;

import java.util.List;

/**
 * Created by Elec332 on 31-7-2015.
 */
public interface IWidgetContainer {

    public List<Widget> getWidgets();

    public void addWidget(Widget widget);

}
