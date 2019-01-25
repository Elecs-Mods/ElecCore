package elec332.core.util;

import elec332.core.api.util.IMemberPointer;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

/**
 * Originally created by amadornes for the Technicalities project, the original file can be found at:
 * https://github.com/amadornes/Technicalities/blob/master/src/main/java/com/technicalitiesmc/lib/FieldPointer.java
 * <p>
 * Represents a field in a class and allows the user to set it and get its value, no matter its visibility.
 */
public final class FieldPointer<P, T> implements IMemberPointer<Field, P, T> {

    public FieldPointer(Class<P> parent, String... names) {
        this(ReflectionHelper.findField(parent, names));
    }

    @SuppressWarnings("unchecked")
    public FieldPointer(Field field) {
        this.parent = (Class<P>) field.getDeclaringClass();
        this.type = (Class<T>) field.getType();
        this.name = field.getName();
        this.field = field;

        try {
            getter = MethodHandles.lookup().unreflectGetter(field);
            setter = MethodHandles.lookup().unreflectSetter(field);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private final Class<P> parent;
    private final Class<T> type;
    private final String name;
    private final Field field;
    private final MethodHandle getter, setter;

    @Override
    public Class<P> getParentType() {
        return parent;
    }

    @Override
    public Class<T> getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isStatic() {
        return ReflectionHelper.isStatic(field);
    }

    @SuppressWarnings("unchecked")
    public T get(P parent) {
        try {
            return (T) getter.invoke(parent);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public void set(P parent, T value) {
        try {
            if (isStatic() && parent == null) {
                setter.invoke(value);
                return;
            }
            setter.invoke(parent, value);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public <A extends Annotation> A get(Class<A> annotation) {
        return field.getAnnotation(annotation);
    }

    @Override
    public Field getReflectedMember() {
        return field;
    }

}
