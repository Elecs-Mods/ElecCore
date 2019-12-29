package elec332.core.util;

import com.google.common.collect.Lists;
import joptsimple.internal.Strings;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

/**
 * Created by Elec332 on 29-7-2015.
 */
public class ReflectionHelper {

    /**
     * Checks if the provided {@link Member} is static
     *
     * @param member The {@link Member} to be checked
     * @return Whether the provided {@link Member} is static
     */
    public static boolean isStatic(Member member) {
        return (member.getModifiers() & Modifier.STATIC) == Modifier.STATIC;
    }

    /**
     * If the provided class is an inner-class, returns all classes "above" it,
     * until it reaches the "main" declaring class
     *
     * @param innerClass The class to be checked
     * @return All classes until the declaring class
     */
    public static Class[] getAllTillMainClass(Class innerClass) {
        if (!isInnerClass(innerClass)) {
            return new Class[]{innerClass};
        }
        String pN = getPackage(innerClass);
        List<Class> ret = Lists.newArrayList();
        List<String> sl = Lists.newArrayList();
        boolean f = true;
        for (String s : getAllClassNamesTillMainClass(innerClass)) {
            try {
                if (f) {
                    pN += ".";
                    f = false;
                } else {
                    pN += "$";
                }
                pN += s;
                ret.add(Class.forName(pN));
            } catch (ClassNotFoundException e) {
                System.out.println("Error finding class: " + pN);
                throw new RuntimeException();
            }
        }
        return ret.toArray(new Class[ret.size()]);
    }

    /**
     * If the provided class is an inner-class, returns all class names "above" it,
     * until it reaches the "main" declaring class
     *
     * @param innerClass The class to be checked
     * @return All class names until the declaring class
     */
    public static String[] getAllClassNamesTillMainClass(Class innerClass) {
        if (!isInnerClass(innerClass)) {
            return new String[]{
                    innerClass.getSimpleName()
            };
        }
        String cN = innerClass.getCanonicalName();
        String pN = getPackage(innerClass);
        String dot = ".";
        pN = pN + dot;
        return cN.replace(pN, "").replace(dot, " ").split(" ");
    }

    /**
     * Gets the package name in which this class is located
     *
     * @param clazz The class
     * @return The package name in which this class is located
     */
    public static String getPackage(Class clazz) {
        return clazz.getPackage().getName();
    }

    /**
     * Checks the provided classname to see if it exists
     *
     * @param s The classname
     * @return Whether the class exists
     */
    public static boolean isClass(String s) {
        String s1 = s.replace("$", ".");
        try {
            Class.forName(s1);
            return true;
        } catch (ClassNotFoundException e) {
            try {
                Class.forName(s);
                return true;
            } catch (ClassNotFoundException e1) {
                return false;
            }
        }
    }

    /**
     * Checks whether the provided class is an inner class
     *
     * @param clazz The class
     * @return Whether the provided class is an inner class
     */
    public static boolean isInnerClass(Class clazz) {
        return clazz.getName().contains("$");
    }

    /**
     * Makes a final field modifiable
     *
     * @param field The field
     * @return The same field, but modifiable
     */
    public static Field makeFinalFieldModifiable(Field field) throws NoSuchFieldException, IllegalAccessException {
        field.setAccessible(true);
        int i = field.getModifiers();
        Field modifier = field.getClass().getDeclaredField("modifiers");
        i &= -17;
        modifier.setAccessible(true);
        modifier.setInt(field, i);
        return field;
    }

    /**
     * Makes a field accessible
     *
     * @param field The field
     * @return The same field, but accessible
     */
    public static Field makeFieldAccessible(Field field) {
        field.setAccessible(true);
        return field;
    }

    public static Field findField(Class<?> clazz, String... fieldNames) {
        Exception lastE = null;
        for (String fieldName : fieldNames) {
            try {
                Field f = clazz.getDeclaredField(fieldName);
                f.setAccessible(true);
                return f;
            } catch (Exception e) {
                lastE = e;
            }
        }
        throw new RuntimeException("Failed to find fields: " + Strings.join(fieldNames, ","), lastE);
    }

    public static Method findMethod(Class<?> clazz, Class[] parameters, String... methodNames) {
        Exception lastE = null;
        for (String methodName : methodNames) {
            try {
                Method m = clazz.getDeclaredMethod(methodName, parameters);
                m.setAccessible(true);
                return m;
            } catch (Exception e) {
                lastE = e;
            }
        }
        throw new RuntimeException("Failed to find methods: " + Strings.join(methodNames, ","), lastE);
    }

    public static Object dismantledInvoke(MethodHandle func, Object o, Object[] objects) throws Throwable {
        Object[] all = new Object[objects.length + 1];
        all[0] = o;
        System.arraycopy(objects, 0, all, 1, objects.length);
        return dismantledInvoke(func, all);
    }

    public static Object dismantledInvoke(MethodHandle func, Object[] objects) throws Throwable {
        switch (objects.length) {
            case 0:
                return func.invoke();
            case 1:
                return func.invoke(objects[0]);
            case 2:
                return func.invoke(objects[0], objects[1]);
            case 3:
                return func.invoke(objects[0], objects[1], objects[2]);
            case 4:
                return func.invoke(objects[0], objects[1], objects[2], objects[3]);
            case 5:
                return func.invoke(objects[0], objects[1], objects[2], objects[3], objects[4]);
            case 6:
                return func.invoke(objects[0], objects[1], objects[2], objects[3], objects[4], objects[5]);
            default:
                throw new UnsupportedOperationException();
        }
    }

}
