package elec332.core.inventory.window;

import elec332.core.inventory.widget.IWidget;

import java.util.List;

/**
 * Created by Elec332 on 31-7-2015.
 */
public interface IWidgetContainer {

    public List<IWidget> getWidgets();

    public <W extends IWidget> W addWidget(W widget);

}
