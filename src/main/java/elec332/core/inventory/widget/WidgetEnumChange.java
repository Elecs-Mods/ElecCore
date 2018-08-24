package elec332.core.inventory.widget;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Elec332 on 23-8-2015.
 */
public class WidgetEnumChange<E extends Enum> extends WidgetButton {

    public WidgetEnumChange(int x, int y, int width, int height, Class<E> clazz, Predicate<E> predicate) {
        this(x, y, width, height, clazz);
        this.predicate = predicate;
        checkPredicate();
    }

    public WidgetEnumChange(int x, int y, int width, int height, Class<E> clazz) {
        super(x, y, 0, 0, width, height);
        this.enumClass = clazz;
        this.index = 0;
        this.list = Lists.newArrayList();
        setDisplayString(getEnum().toString());
        predicate = new Predicate<E>() {
            @Override
            public boolean apply(@Nullable E input) {
                return true;
            }
        };
    }

    private final Class<E> enumClass;
    private List<IEnumChangedEvent<WidgetEnumChange<E>>> list;
    private int index;
    private Predicate<E> predicate;

    public WidgetEnumChange<E> addButtonEvent(IEnumChangedEvent<WidgetEnumChange<E>> event) {
        this.list.add(event);
        return this;
    }

    public E getEnum() {
        return enumClass.getEnumConstants()[index];
    }

    public void setEnum(E e) {
        if (!predicate.apply(e)) {
            return;
        }
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
        if (b) {
            throw new IllegalArgumentException();
        }
    }

    public WidgetEnumChange<E> setPredicate(Predicate<E> predicate) {
        this.predicate = predicate;
        checkPredicate();
        return this;
    }

    @Override
    public void onButtonClicked(int mouseButton) {
        nextIdx();
        while (!predicate.apply(getEnum())) {
            nextIdx();
        }
        setDisplayString(getEnum().toString());
        super.onButtonClicked(mouseButton);
        distributeEvents();
    }

    private void nextIdx() {
        index++;
        if (index >= enumClass.getEnumConstants().length) {
            index = 0;
        }
    }

    private void checkPredicate() {
        boolean b = false;
        for (E e : enumClass.getEnumConstants()) {
            if (predicate.apply(e)) {
                b = true;
                break;
            }
        }
        if (!b) {
            throw new IllegalStateException();
        }
    }

    private void distributeEvents() {
        for (IEnumChangedEvent<WidgetEnumChange<E>> event : list) {
            event.onEnumChanged(this);
        }
    }

    public interface IEnumChangedEvent<E extends WidgetEnumChange> {

        public void onEnumChanged(E widget);

    }

}
