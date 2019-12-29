package elec332.core.util;

import elec332.core.api.util.IMemberPointer;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;

/**
 * Created by Elec332 on 2-1-2019
 */
public class MethodPointer<P, R> implements IMemberPointer<Method, P, R> {

    public MethodPointer(Class<P> parent, String name, Class... parameters) {
        this(parent, parameters, name);
    }

    public MethodPointer(Class<P> parent, Class[] parameters, String... names) {
        this(ReflectionHelper.findMethod(parent, parameters, names));
    }

    @SuppressWarnings("unchecked")
    public MethodPointer(Method method) {
        this.parent = (Class<P>) method.getDeclaringClass();
        this.type = (Class<R>) method.getReturnType();
        this.name = method.getName();
        this.method = method;

        try {
            handle = MethodHandles.lookup().unreflect(this.method);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private final Class<P> parent;
    private final Class<R> type;
    private final String name;
    private final Method method;
    private final MethodHandle handle;

    @Override
    public Class<P> getParentType() {
        return parent;
    }

    @Override
    public Class<R> getType() {
        return type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isStatic() {
        return ReflectionHelper.isStatic(method);
    }

    @SuppressWarnings("unchecked")
    public R invoke(P parent, Object... params) {
        try {
            return (R) ReflectionHelper.dismantledInvoke(handle, parent, params);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Method getReflectedMember() {
        return method;
    }

}

