package elec332.core.config;

import net.minecraftforge.common.config.Configuration;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created by Elec332 on 12-4-2015.
 */
public class ConfigWrapper {

    public ConfigWrapper(Configuration configuration){
        this.configuration = configuration;
        this.instances = new ArrayList<Object>();
        this.hasInit = false;
    }

    private Configuration configuration;
    private ArrayList<Object> instances;
    private boolean hasInit;

    public void registerConfig(Object o){
        if (!hasInit)
            this.instances.add(o);
        else throw new RuntimeException("You cannot register configs after init");
    }

    public Configuration getConfiguration() {
        return this.configuration;
    }

    public void refresh(){
        configuration.load();
        this.hasInit = true;
        for (Object o : instances){
            Class objClass = o.getClass();
            for (Field field : objClass.getDeclaredFields()){
                try {
                    boolean oldAccess = field.isAccessible();
                    field.setAccessible(true);
                    if (field.isAnnotationPresent(Configurable.class)) {
                        Configurable configurable = field.getAnnotation(Configurable.class);
                        Object oldValue = field.get(o);
                        if (field.getType().isAssignableFrom(Integer.TYPE)) {
                            field.set(o, configuration.getInt(field.getName(), configurable.category(), (Integer) oldValue, configurable.minValue(), configurable.maxValue(), configurable.comment()));
                        } else if (field.getType().isAssignableFrom(Boolean.TYPE)) {
                            field.set(o, configuration.getBoolean(field.getName(), configurable.category(), (Boolean) oldValue, configurable.comment()));
                        } else if (field.getType().isAssignableFrom(String.class)){
                            if (configurable.validStrings().length > 0)
                                field.set(o, configuration.getString(field.getName(), configurable.category(), (String) oldValue, configurable.comment(), configurable.validStrings()));
                            else
                                field.set(o, configuration.getString(field.getName(), configurable.category(), (String) oldValue, configurable.comment()));
                        }
                    }
                    field.setAccessible(oldAccess);
                } catch (Throwable t) {
                    throw new RuntimeException(t);
                }
            }
            for (Method method : objClass.getDeclaredMethods()){
                try {
                    boolean oldAccess = method.isAccessible();
                    method.setAccessible(true);
                    if (method.isAnnotationPresent(Configurable.class) && method.getParameterTypes().length == 0){
                        Configurable configurable = method.getAnnotation(Configurable.class);
                        if (configuration.getBoolean(method.getName(), configurable.category(), configurable.enabledByDefault(), configurable.comment())){
                            method.invoke(o);
                        }
                    }
                    method.setAccessible(oldAccess);
                } catch (Throwable t) {
                    throw new RuntimeException(t);
                }
            }
        }
        configuration.save();
    }

    public static Configuration wrapCategoryAsConfig(Configuration configuration, String category){
        return new CategoryAsConfig(category, configuration);
    }
}
