package elec332.core.inventory.widget;

import java.util.function.Consumer;
import java.util.function.Predicate;

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
        this.listener = t -> {
        };
        setDisplayString(getEnum().toString());
        predicate = input -> true;
    }

    private final Class<E> enumClass;
    private Consumer<E> listener;
    private int index;
    private Predicate<E> predicate;

    public WidgetEnumChange<E> onValueChanged(Consumer<E> event) {
        this.listener = this.listener.andThen(event);
        return this;
    }

    public E getEnum() {
        return getEnumValues()[index];
    }

    public void setEnum(E e) {
        if (!predicate.test(e)) {
            return;
        }
        boolean b = true;
        for (int i = 0; i < getEnumValues().length; i++) {
            E en = getEnumValues()[i];
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
        while (!predicate.test(getEnum())) {
            nextIdx();
        }
        setDisplayString(getEnum().toString());
        super.onButtonClicked(mouseButton);
        distributeEvents();
    }

    private E[] getEnumValues() {
        return enumClass.getEnumConstants(); //Forge can dynamically add more values at runtime, so don't cache it
    }

    private void nextIdx() {
        index++;
        if (index >= getEnumValues().length) {
            index = 0;
        }
    }

    private void checkPredicate() {
        boolean b = false;
        for (E e : getEnumValues()) {
            if (predicate.test(e)) {
                b = true;
                break;
            }
        }
        if (!b) {
            throw new IllegalStateException();
        }
    }

    @SuppressWarnings("all")
    private void distributeEvents() {
        listener.accept(getEnum());
    }

}
