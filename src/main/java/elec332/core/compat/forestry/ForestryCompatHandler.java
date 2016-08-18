package elec332.core.compat.forestry;

import elec332.core.util.AbstractCompatHandler;

/**
 * Created by Elec332 on 14-8-2016.
 */
public class ForestryCompatHandler extends AbstractCompatHandler.ICompatHandler {

    @Override
    public String getName() {
        return "forestry";
    }

    @Override
    public void init() {
        ForestryInit.init();
    }

    public void postInit(){
        ForestryInit.postInit();
    }

}
