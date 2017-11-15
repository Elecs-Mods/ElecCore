package elec332.core.api.config;

import elec332.core.config.Configurable;
import net.minecraftforge.common.config.Configuration;

import java.lang.reflect.Field;

/**
 * Created by Elec332 on 6-10-2017.
 */
public interface IConfigElementSerializer {

    public boolean setData(Class<?> type, Object instance, Field field, Configurable data, Configuration config, String category, Object defaultValue, String comment) throws Exception;

}
