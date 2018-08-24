package elec332.core.config;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

/**
 * Created by Elec332 on 11-4-2015.
 */
class CategoryAsConfig extends Configuration {

    CategoryAsConfig(String m, Configuration ma) {
        this.name = m;
        this.m = ma;
    }

    private String name;
    private Configuration m;

    @Override
    public ConfigCategory getCategory(String category) {
        return m.getCategory(name + "." + category);
    }

    @Override
    public void save() {
        m.save();
    }

    @Override
    public void load() {
        m.load();
    }

}
