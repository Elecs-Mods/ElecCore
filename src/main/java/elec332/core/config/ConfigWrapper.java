package elec332.core.config;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import elec332.core.api.config.IConfigElementSerializer;
import elec332.core.api.config.IConfigWrapper;
import elec332.core.api.config.IConfigurableElement;
import elec332.core.java.ReflectionHelper;
import elec332.core.util.FMLUtil;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.LoaderState;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

/**
 * Created by Elec332 on 12-4-2015.
 */
public class ConfigWrapper implements IConfigWrapper {

    public ConfigWrapper(Configuration configuration){
        this.configuration = configuration;
        this.instances = Lists.newArrayList();
        this.hasInit = false;
        this.categoryDataList = Lists.newArrayList();
        this.categories = Sets.newHashSet();
        this.configurableElements = Lists.newArrayList();
        this.isReloading = false;
    }

    private Configuration configuration;
    private List<Object> instances;
    private boolean hasInit;
    private List<CategoryData> categoryDataList;
    private Set<String> categories;
    private List<IConfigurableElement> configurableElements;
    private boolean isReloading;
    private static List<IConfigElementSerializer> serializers;

    @Override
    public void registerConfig(Object o){
        if (!hasInit) {
            this.instances.add(o);
        } else {
            throw new RuntimeException("You cannot register configs after init");
        }
    }

    @Nonnull
    @Override
    public ConfigWrapper setCategoryData(String category, String description){
        for (CategoryData cat: this.categoryDataList){
            if (category.equals(cat.getCategory())) {
                throw new IllegalArgumentException();
            }
        }
        this.categoryDataList.add(new CategoryData(category, description));
        addRegisteredCategory(category);
        return this;
    }

    protected void addRegisteredCategory(String category){
        if (!categories.contains(category.toLowerCase())) {
            this.categories.add(category.toLowerCase());
        }
    }

    @Nonnull
    @Override
    public List<String> getRegisteredCategories() {
        return ImmutableList.copyOf(categories);
    }

    @Override
    public boolean hasBeenLoaded() {
        return hasInit;
    }

    @Override
    public void registerConfigWithInnerClasses(Object obj){
        registerConfig(obj);
        for (Class<?> clazz : Lists.reverse(Lists.newArrayList(obj.getClass().getDeclaredClasses()))){
            if (!clazz.isInterface()){
                try {
                    registerConfigWithInnerClasses(clazz.getConstructor().newInstance());
                } catch (Exception e){
                    throw new RuntimeException("Error registering config: "+clazz.getName());
                }
            }
        }
    }

    @Override
    public void registerConfigurableElement(IConfigurableElement configurableElement) {
        this.configurableElements.add(configurableElement);
    }

    @Nonnull
    @Override
    public Configuration getConfiguration() {
        return this.configuration;
    }

    @Override
    public void refresh(boolean load) {
        refreshInternal(load);
    }

    protected void refreshInternal(boolean load){
        this.isReloading = true;
        if (load) {
            configuration.load();
        }
        if (!this.hasInit) {
            this.hasInit = true;
        }
        for (CategoryData categoryData : categoryDataList){
            configuration.setCategoryComment(categoryData.getCategory(), categoryData.getDescription());
        }
        for (Object o : instances){
            Class objClass = o.getClass();
            String classCategory = Configuration.CATEGORY_GENERAL;
            if (objClass.isAnnotationPresent(Configurable.Class.class)){
                Configurable.Class configClass = (Configurable.Class) objClass.getAnnotation(Configurable.Class.class);
                if (configClass.inherit() == Configurable.Inherit.TRUE){
                    Class[] classes = ReflectionHelper.getAllTillMainClass(objClass);
                    String s = "";
                    for (Class clazz : classes){
                        if (clazz.isAnnotationPresent(Configurable.Class.class)){
                            if (!s.equals("")){
                                s += ".";
                            }
                            String s1 = ((Configurable.Class) clazz.getAnnotation(Configurable.Class.class)).category();
                            s += s1.equals(Configuration.CATEGORY_GENERAL)?clazz.getSimpleName():s1;
                        }
                    }
                    classCategory = s;
                } else {
                    classCategory = configClass.category();
                }
                String comment = configClass.comment();
                //classCategory = classCategory.toLowerCase();
                if (!comment.equals("")) {
                    configuration.setCategoryComment(classCategory, comment);
                }
            }
            for (Field field : objClass.getDeclaredFields()){
                try {
                    boolean oldAccess = field.isAccessible();
                    field.setAccessible(true);
                    if (field.isAnnotationPresent(Configurable.class)) {
                        Configurable configurable = field.getAnnotation(Configurable.class);
                        Object oldValue = field.get(o);
                        String category = configurable.category();
                        if (category.equals(Configuration.CATEGORY_GENERAL)){
                            category = classCategory;
                        }
                        addRegisteredCategory(category);
                        boolean serialized = false;
                        for (IConfigElementSerializer serializer : serializers){
                            if (serializer.setData(field.getType(), o, field, configurable, configuration, category, oldValue, configurable.comment())){
                                serialized = true;
                                break;
                            }
                        }
                        if (!serialized){
                            throw new RuntimeException("Could not find serializer for type "+field.getType());
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

        for (IConfigurableElement cfgElement : configurableElements){
            cfgElement.reconfigure(configuration);
        }

        if (configuration.hasChanged()) {
            configuration.save();
        }
        this.isReloading = false;
    }

    @Nonnull
    @Override
    public IConfigWrapper wrapCategoryAsConfigWrapper(String category) {
        return new ConfigWrapper(wrapCategoryAsConfig(category)){

            @Override
            public void refresh(boolean load) {
                if (load && isReloading){
                    throw new IllegalStateException("Cannot active load config file while master config is reloading.");
                }
                super.refresh(load);
            }

            @Override
            protected void addRegisteredCategory(String category) {
                super.addRegisteredCategory(category);
                ConfigWrapper.this.addRegisteredCategory(category+"."+category);
            }

        };
    }

    @Nonnull
    @Override
    public Configuration wrapCategoryAsConfig(String category) {
        addRegisteredCategory(category);
        return new CategoryAsConfig(category, configuration){

            @Override
            public void load() {
                if (!isReloading) {
                    super.load();
                } else {
                    throw new IllegalStateException("Cannot load config while master configuration is reloading.");
                }
            }


            @Override
            public void save() {
                if (!isReloading) {
                    super.save();
                } else {
                    throw new IllegalStateException("Cannot save config while master configuration is reloading.");
                }
            }

        };
    }

    public static Configuration wrapCategoryAsConfig(Configuration configuration, String category){
        return new CategoryAsConfig(category, configuration);
    }

    public static void registerConfigElementSerializer(IConfigElementSerializer serializer){
        if (FMLUtil.hasReachedState(LoaderState.INITIALIZATION)){
            throw new RuntimeException("Cannot register config element serializer after PreInit!");
        }
        serializers.add(serializer);
    }

    private final class CategoryData {

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

    static {
        serializers = Lists.newArrayList();
        registerConfigElementSerializer((type, instance, field, data, config, category, defaultValue, comment) -> {
            if (type.isAssignableFrom(Integer.TYPE)) {
                field.set(instance, config.getInt(field.getName(), category, (Integer) defaultValue, (int) data.minValue(), (int) data.maxValue(), comment));
                return true;
            }
            return false;
        });
        registerConfigElementSerializer((type, instance, field, data, config, category, defaultValue, comment) -> {
            if (type.isAssignableFrom(Boolean.TYPE)) {
                field.set(instance, config.getBoolean(field.getName(), category, (Boolean) defaultValue, comment));
                return true;
            }
            return false;
        });
        registerConfigElementSerializer((type, instance, field, data, config, category, defaultValue, comment) -> {
            if (field.getType().isAssignableFrom(String.class)){
                if (data.validStrings().length > 0) {
                    field.set(instance, config.getString(field.getName(), category, (String) defaultValue, comment, data.validStrings()));
                } else {
                    field.set(instance, config.getString(field.getName(), category, (String) defaultValue, comment));
                }
                return true;
            }
            return false;
        });
        registerConfigElementSerializer((type, instance, field, data, config, category, defaultValue, comment) -> {
            if (field.getType().isAssignableFrom(Float.TYPE)){
                field.set(instance, config.getFloat(field.getName(), category, (Float) defaultValue, data.minValue(), data.maxValue(), comment));
                return true;
            }
            return false;
        });
    }

}
