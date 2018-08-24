package elec332.core.api.config;

import net.minecraftforge.common.config.Configuration;

import java.lang.reflect.Field;

/**
 * Created by Elec332 on 6-10-2017.
 *
 * Serializer for serializing the values of fields annotated with {@link Configurable} to a {@link Configuration}
 */
public interface IConfigElementSerializer {

    /**
     * Synchronises the value of the {@param field} and the value in the configuration ({@param config})
     *
     * @param type The type of the value of the {@param field}
     * @param instance The object whose field will be modified
     * @param field The field
     * @param data The annotation data from the {@param field}
     * @param config The configuration from which the data must loaded
     * @param category The configuration category of the configurable value
     * @param defaultValue The default value
     * @param comment The configuration comment
     *
     * @return True if this serializer supports the {@param type} and serialized it.
     * @throws Exception If an error occurred.
     */
    public boolean setData(Class<?> type, Object instance, Field field, Configurable data, Configuration config, String category, Object defaultValue, String comment) throws Exception;

}
