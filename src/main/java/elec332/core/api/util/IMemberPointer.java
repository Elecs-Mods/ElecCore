package elec332.core.api.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;

/**
 * Created by Elec332 on 2-1-2019
 */
public interface IMemberPointer<M extends AccessibleObject & Member, P, R> {

    @SuppressWarnings("unchecked")
    default public Class<P> getParentType() {
        return (Class<P>) getReflectedMember().getDeclaringClass();
    }

    public Class<R> getType();

    default public String getName() {
        return getReflectedMember().getName();
    }

    public boolean isStatic();

    default public <A extends Annotation> A getAnnotation(Class<A> annotation) {
        return getReflectedMember().getAnnotation(annotation);
    }

    public M getReflectedMember();

}
