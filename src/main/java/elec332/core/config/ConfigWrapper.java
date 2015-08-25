package elec332.core.config;

import com.google.common.collect.Lists;
import net.minecraftforge.common.config.Configuration;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by Elec332 on 12-4-2015.
 */
public class ConfigWrapper {

    public ConfigWrapper(Configuration configuration){
        this.configuration = configuration;
        this.instances = Lists.newArrayList();
        this.hasInit = false;
        this.categoryDataList = Lists.newArrayList();
    }

    private Configuration configuration;
    private List<Object> instances;
    private boolean hasInit;
    private List<CategoryData> categoryDataList;

    public void registerConfig(Object o){
        if (!hasInit)
            this.instances.add(o);
        else throw new RuntimeException("You cannot register configs after init");
    }

    public ConfigWrapper setCategoryData(String category, String description){
        for (CategoryData cat: this.categoryDataList){
            if (category.equals(cat.getCategory()))
                throw new IllegalArgumentException();
        }
        this.categoryDataList.add(new CategoryData(category, description));
        return this;
    }

    public void registerConfigWithInnerClasses(Object obj){
        registerConfig(obj);
        for (Class<?> clazz : obj.getClass().getDeclaredClasses()){
            if (!clazz.isInterface()){
                try {
                    registerConfig(clazz.getConstructor().newInstance());
                } catch (Exception e){
                    throw new RuntimeException("Error registering config: "+clazz.getName());
                }
            }
        }
    }

    public Configuration getConfiguration() {
        return this.configuration;
    }

    public void refresh(){
        configuration.load();
        if (!this.hasInit)
            this.hasInit = true;
        for (CategoryData categoryData : categoryDataList){
            configuration.setCategoryComment(categoryData.getCategory(), categoryData.getDescription());
        }
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

    private final class CategoryData{

        private CategoryData(String category, String desc){
            this.category = category;
            this.desc = desc;
        }

        private final String category, desc;

        private String getCategory(){
            return this.category;
        }

        private String getDescription(){
            return this.desc;
        }

    }
}
