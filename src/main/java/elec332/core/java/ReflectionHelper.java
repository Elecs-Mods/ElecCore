package elec332.core.java;

import java.lang.reflect.Field;

/**
 * Created by Elec332 on 29-7-2015.
 */
public class ReflectionHelper {

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
