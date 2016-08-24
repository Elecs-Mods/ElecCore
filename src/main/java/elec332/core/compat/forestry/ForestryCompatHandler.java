package elec332.core.compat.forestry;

import elec332.core.util.AbstractCompatHandler;
import net.minecraft.creativetab.CreativeTabs;

/**
 * Created by Elec332 on 14-8-2016.
 */
public class ForestryCompatHandler extends AbstractCompatHandler.ICompatHandler {

    @Override
    public String getName() {
        return "forestry";
    }

    public static CreativeTabs tabBees;

    @Override
    public void init() {
        ForestryInit.init();
    }

    public void postInit(){
        ForestryInit.postInit();
    }

    public static CreativeTabs getForestryBeeTab(){
        return tabBees == null ? CreativeTabs.SEARCH : tabBees;
    }

}
