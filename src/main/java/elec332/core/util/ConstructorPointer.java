package elec332.core.util;

import elec332.core.api.util.IMemberPointer;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;

/**
 * Created by Elec332 on 29-12-2019
 */
public class ConstructorPointer<T> implements IMemberPointer<Constructor<T>, T, T> {

    public ConstructorPointer(Class<T> clazz, Class... params) {
        this(getConstructor(clazz, params));
    }

    public ConstructorPointer(Constructor<T> constructor) {
        this.constructor = constructor;
        try {
            handle = MethodHandles.lookup().unreflectConstructor(constructor);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private final Constructor<T> constructor;
    private final MethodHandle handle;

    @SuppressWarnings("unchecked")
    public T newInstance(Object... params) {
        try {
            return (T) ReflectionHelper.dismantledInvoke(handle, params);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Class<T> getType() {
        return constructor.getDeclaringClass();
    }

    @Override
    public boolean isStatic() {
        return false;
    }

    @Override
    public Constructor<T> getReflectedMember() {
        return constructor;
    }

    private static <T> Constructor<T> getConstructor(Class<T> clazz, Class... params) {
        try {
            Constructor<T> ret = clazz.getDeclaredConstructor(params);
            ret.setAccessible(true);
            return ret;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
