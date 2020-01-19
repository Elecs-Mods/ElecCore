package elec332.core.config;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import elec332.core.api.config.Configurable;
import elec332.core.api.config.IConfigElementSerializer;
import elec332.core.api.config.IConfigWrapper;
import elec332.core.api.config.IConfigurableElement;
import elec332.core.util.ReflectionHelper;
import elec332.core.util.function.FuncHelper;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by Elec332 on 12-4-2015.
 */
public abstract class AbstractConfigWrapper implements IConfigWrapper {

    public AbstractConfigWrapper() {
        this.instances = Lists.newArrayList();
    }

    private final List<Object> instances;

    @Override
    public void registerConfigWithInnerClasses(Object obj) {
        registerConfig(obj);
        for (Class<?> clazz : Lists.reverse(Lists.newArrayList(((Class) (obj instanceof Class ? obj : obj.getClass())).getDeclaredClasses()))) {
            if (!clazz.isInterface()) {
                try {
                    registerConfigWithInnerClasses(clazz.getConstructor().newInstance());
                } catch (Exception e) {
                    throw new RuntimeException("Error registering config: " + clazz.getName());
                }
            }
        }
    }

    @Override
    public void registerConfig(Object o) {
        if (hasBeenLoaded()) {
            throw new RuntimeException("Cannot register configs after registering!");
        }
        if (instances.contains(o)) {
            return;
        }
        this.instances.add(o);
        Class objClass = o.getClass();
        if (o instanceof Class) {
            objClass = (Class) o;
            o = null;
        }
        final Object fInst = o;
        String classCategory;
        String classComment;
        if (objClass.isAnnotationPresent(Configurable.Class.class)) {
            Configurable.Class configClass = (Configurable.Class) objClass.getAnnotation(Configurable.Class.class);
            if (configClass.inherit()) {
                Class[] classes = ReflectionHelper.getAllTillMainClass(objClass);
                StringBuilder s = new StringBuilder();
                for (Class clazz : classes) {
                    if (clazz.isAnnotationPresent(Configurable.Class.class)) {
                        if (s.length() != 0) {
                            s.append(".");
                        }
                        String s1 = ((Configurable.Class) clazz.getAnnotation(Configurable.Class.class)).category();
                        s.append(Strings.isNullOrEmpty(s1) ? clazz.getSimpleName() : s1);
                    }
                }
                classCategory = s.toString();
            } else {
                classCategory = configClass.category();
            }
            classComment = configClass.comment();
        } else {
            classCategory = CATEGORY_GENERAL;
            classComment = "";
        }

        for (Field field : objClass.getDeclaredFields()) {
            try {
                boolean oldAccess = field.isAccessible();
                field.setAccessible(true);
                if (field.isAnnotationPresent(Configurable.class)) {
                    Configurable configurable = field.getAnnotation(Configurable.class);
                    Object oldValue = field.get(fInst);

                    registerConfigurableElement(new IConfigurableElement() {

                        private Runnable listener;

                        @Override
                        public void registerProperties(@Nonnull ForgeConfigSpec.Builder builder, ModConfig.Type type) {
                            String categoryString = configurable.category();
                            if (Strings.isNullOrEmpty(categoryString)) {
                                categoryString = classCategory;
                            }
                            boolean serialized = false;
                            if (!Strings.isNullOrEmpty(classComment)) {
                                builder.comment(classComment);
                            }
                            List<String> category = Lists.newArrayList(categoryString.split(CATEGORY_SPLITTER));
                            builder.push(category);
                            for (IConfigElementSerializer serializer : serializers) {
                                ForgeConfigSpec.ConfigValue cv = serializer.makeConfigEntry(field.getType(), fInst, field, configurable, builder, oldValue, configurable.comment());
                                if (cv != null) {
                                    listener = FuncHelper.safeRunnable(() -> field.set(fInst, serializer.getFieldValue(field, cv)));
                                    serialized = true;
                                    break;
                                }
                            }
                            builder.pop(category.size());
                            if (!serialized) {
                                throw new RuntimeException("Could not find serializer for type " + field.getType());
                            }
                        }

                        @Override
                        public void load() {
                            listener.run();
                        }

                    });
                }
                field.setAccessible(oldAccess);
            } catch (RuntimeException e) {
                throw e; //Eh
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }

    @Nonnull
    @Override
    public IConfigWrapper getSubConfig(@Nonnull String category, String comment) {
        if (!Strings.isNullOrEmpty(comment)) {
            setCategoryDescription(category, comment);
        }
        if (Strings.isNullOrEmpty(category)) {
            throw new IllegalArgumentException("Invalid category name: " + category);
        }
        return new NestedWrappedConfig(this, category);
    }

}
