package elec332.core.world;

import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;

/**
 * Created by Elec332 on 16-12-2016.
 */
public abstract class AbstractWorldProvider extends WorldProvider {

    protected void setup(){
        super.init();
    }

    @Override
    protected final void init() {
        setup();
    }

    public World getWorld(){
        return this.world;
    }

}
