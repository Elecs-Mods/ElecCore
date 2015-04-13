package elec332.core.nbt;

import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang3.Validate;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Elec332 on 12-4-2015.
 */
public class NBTHelper {

    public static void save(NBTTagCompound tagCompound, Object object){
        Class objClass = object.getClass();
        for (Field field : getFieldsListWithAnnotation(objClass, NBTData.class)){
        //for (Field field : objClass.getDeclaredFields()) {
            try {
               /* field.setAccessible(true);
                System.out.println("Running for field: " + field.getName());
                if (field.isAnnotationPresent(NBTData.class)) {*/
                    //System.out.println(field.getName() + " is annotated with @ISaveData");
                    if (field.getType().equals(Integer.TYPE)) {
                        System.out.println(field.getName() + " was a valid integer");
                        tagCompound.setInteger(field.getName(), field.getInt(object));
                        System.out.println(field.getName() + " was saved to NBT");
                    } else if (field.getType().equals(Boolean.TYPE)) {
                        System.out.println(field.getName() + " was a valid boolean");
                        tagCompound.setBoolean(field.getName(), field.getBoolean(object));
                        System.out.println(field.getName() + " was saved to NBT");
                    //}
                }
            } catch (Throwable throwable){
                spam("ERROR SAVING");
                throw new RuntimeException(throwable);
            }
        }
    }

    public static List<Field> getFieldsListWithAnnotation(Class<?> cls, Class<? extends Annotation> annotationCls) {
        Validate.isTrue(annotationCls != null, "The annotation class must not be null", new Object[0]);
        Field[] allFields = cls.getDeclaredFields();
        ArrayList<Field> annotatedFields = new ArrayList<Field>();

        for (Field field : allFields){
            if(field.getAnnotation(annotationCls) != null) {
                annotatedFields.add(field);
            }
        }

        return annotatedFields;
    }

    public static void load(NBTTagCompound tagCompound, Object object){
        Class objClass = object.getClass();
        for (Field field : objClass.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                System.out.println("Running for field: " + field.getName());
                if (field.isAnnotationPresent(NBTData.class)) {
                    if (field.getType().equals(Integer.TYPE)) {
                        System.out.println(field.getName() + " was a valid integer");
                        field.set(object, tagCompound.getInteger(field.getName()));
                        System.out.println(field.getName() + " was loaded from NBT");
                    } else if (field.getType().equals(Boolean.TYPE)) {
                        System.out.println(field.getName() + " was a valid boolean");
                        field.set(object, tagCompound.getBoolean(field.getName()));
                        System.out.println(field.getName() + " was loaded from NBT");
                    }
                }
            } catch (Throwable throwable){
                spam("ERROR LOADING");
                throw new RuntimeException(throwable);
            }
        }
    }

    private static void spam(String s){
        for (int i = 0; 100 > i; i++)
            System.out.println(s);
    }
}
