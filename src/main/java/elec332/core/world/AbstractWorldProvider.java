package elec332.core.world;

import net.minecraft.world.WorldProvider;

/**
 * Created by Elec332 on 16-12-2016.
 */
public abstract class AbstractWorldProvider extends WorldProvider {

    public void setup(){
        super.createBiomeProvider();
    }

    @Override
    protected void createBiomeProvider() {
        setup();
    }

}
