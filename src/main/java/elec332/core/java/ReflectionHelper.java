package elec332.core.java;

import com.google.common.collect.Lists;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by Elec332 on 29-7-2015.
 */
public class ReflectionHelper {

    public static Class[] getAllTillMainClass(Class innerClass){
        if (!isInnerClass(innerClass)) {
            return new Class[]{innerClass};
        }
        String pN = getPackage(innerClass);
        List<Class> ret = Lists.newArrayList();
        List<String> sl = Lists.newArrayList();
        boolean f = true;
        for (String s : getAllClassNamesTillMainClass(innerClass)){
            try {
                if (f) {
                    pN += ".";
                    f = false;
                } else {
                    pN += "$";
                }
                pN += s;
                ret.add(Class.forName(pN));
            } catch (ClassNotFoundException e){
                System.out.println("Error finding class: "+pN);
                throw new RuntimeException();
            }
        }
        return ret.toArray(new Class[ret.size()]);
    }

    public static String[] getAllClassNamesTillMainClass(Class innerClass){
        if (!isInnerClass(innerClass))
            return new String[]{innerClass.getSimpleName()};
        String cN = innerClass.getCanonicalName();
        String pN = getPackage(innerClass);
        String dot = ".";
        pN = pN + dot;
        return cN.replace(pN, "").replace(dot, " ").split(" ");
    }

    public static String getPackage(Class clazz){
        return clazz.getPackage().getName();
    }

    public static boolean isClass(String s){
        String s1 = s.replace("$", ".");
        try {
            Class.forName(s1);
            return true;
        } catch (ClassNotFoundException e){
            try {
                Class.forName(s);
                return true;
            } catch (ClassNotFoundException e1){
                return false;
            }
        }
    }

    public static boolean isInnerClass(Class clazz){
        return clazz.getName().contains("$");
    }

    public static Field makeFinalFieldModifiable(Field field) throws NoSuchFieldException, IllegalAccessException{
        field.setAccessible(true);
        int i = field.getModifiers();
        Field modifier = field.getClass().getDeclaredField("modifiers");
        i &= -17;
        modifier.setAccessible(true);
        modifier.setInt(field, i);
        return field;
    }

    public static Field makeFieldAccessible(Field field){
        field.setAccessible(true);
        return field;
    }

    public static void temporarilyAccessField(Field field, IAccessibleField fieldAccess){
        boolean b = field.isAccessible();
        field.setAccessible(true);
        fieldAccess.onAccess(field);
        field.setAccessible(b);
    }

    public interface IAccessibleField{
        public void onAccess(Field field);
    }

}
