package elec332.core.inventory.widget;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by Elec332 on 23-8-2015.
 */
public class WidgetEnumChange<E extends Enum> extends WidgetButton {

    public WidgetEnumChange(int x, int y, int width, int height, Class<E> clazz) {
        super(x, y, 0, 0, width, height);
        this.enumClass = clazz;
        this.index = 0;
        this.list = Lists.newArrayList();
        setDisplayString(getEnum().toString());
    }

    private final Class<E> enumClass;
    private List<IEnumChangedEvent<WidgetEnumChange<E>>> list;
    private int index;

    public WidgetEnumChange<E> addButtonEvent(IEnumChangedEvent<WidgetEnumChange<E>> event){
        this.list.add(event);
        return this;
    }

    public E getEnum(){
        return enumClass.getEnumConstants()[index];
    }

    public void setEnum(E e){
        boolean b = true;
        for (int i = 0; i < enumClass.getEnumConstants().length; i++) {
            E en = enumClass.getEnumConstants()[i];
            if (e == en) {
                setDisplayString(en.toString());
                this.index = i;
                b = false;
            }
        }
        distributeEvents();
        if (b)
            throw new IllegalArgumentException();
    }

    @Override
    public void onButtonClicked() {
        super.onButtonClicked();
        index++;
        if (index >= enumClass.getEnumConstants().length){
            index = 0;
        }
        setDisplayString(getEnum().toString());
        distributeEvents();
    }

    private void distributeEvents(){
        for (IEnumChangedEvent<WidgetEnumChange<E>> event : list)
            event.onEnumChanged(this);
    }

    public interface IEnumChangedEvent<E extends WidgetEnumChange>{

        public void onEnumChanged(E widget);

    }

}
