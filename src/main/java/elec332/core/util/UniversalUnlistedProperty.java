package elec332.core.util;

import com.google.common.base.Predicate;
import net.minecraftforge.common.property.IUnlistedProperty;

/**
 * Created by Elec332 on 4-3-2016.
 */
public class UniversalUnlistedProperty<T> implements IUnlistedProperty<T> {

    public UniversalUnlistedProperty(String name, Class<T> clazz){
        this(name, clazz, null);
    }

    public UniversalUnlistedProperty(String name, Class<T> clazz, Predicate<T> predicate) {
        this.name = name;
        this.clazz = clazz;
        this.predicate = predicate;
    }

    private final String name;
    private final Class<T> clazz;
    private final Predicate<T> predicate;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isValid(T value) {
        return predicate == null || predicate.apply(value);
    }

    @Override
    public Class<T> getType() {
        return clazz;
    }

    @Override
    public String valueToString(T value) {
        return value == null ? "null" : value.toString();
    }

}
