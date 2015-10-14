package elec332.core.config;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

/**
 * Created by Elec332 on 11-4-2015.
 */
public class CategoryAsConfig extends Configuration {

    public CategoryAsConfig(String m, Configuration ma){
        this.name = m;
        this.m = ma;
    }

    private String name;
    private Configuration m;

    public ConfigCategory getCategory(String category) {
        return m.getCategory(name + "." + category);
    }

    public void save(){
        m.save();
    }

    public void load(){
        m.load();
    }

}
