package elec332.core.api.config;

import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by Elec332 on 6-10-2017.
 * <p>
 * Serializer for serializing the values of fields annotated with {@link Configurable}
 */
public interface IConfigElementSerializer {

    /**
     * Synchronises the value of the {@param field} and the value in the configuration ({@param config})
     *
     * @param type         The type of the value of the {@param field}
     * @param instance     The object whose field will be modified
     * @param field        The field
     * @param data         The annotation data from the {@param field}
     * @param config       The configuration from which the data must loaded
     * @param defaultValue The default value
     * @param comment      The configuration comment
     * @return The config entry for the {@param type} and the provided parameters, null if not supported.
     */
    @Nullable
    public ForgeConfigSpec.ConfigValue makeConfigEntry(Class<?> type, Object instance, Field field, Configurable data, ForgeConfigSpec.Builder config, Object defaultValue, String comment);

    /**
     * Gets the value from the config property and makes sure its a type that the field accepts
     *
     * @param field       The config field
     * @param configValue The forge config property
     * @return The value from the config property, a value which the field can accept
     */
    default public Object getFieldValue(Field field, ForgeConfigSpec.ConfigValue configValue) {
        if (field.getType().isArray()) {
            return ((List) configValue.get()).toArray((Object[]) Array.newInstance(field.getType().getComponentType(), 0));
        }
        return configValue.get();
    }

}
