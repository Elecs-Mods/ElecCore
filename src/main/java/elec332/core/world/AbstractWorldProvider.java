package elec332.core.world;

import elec332.abstraction.impl.MCAbstractedWorldProvider;
import elec332.abstraction.object.IAbstractedWorldProvider;
import net.minecraft.world.World;

/**
 * Created by Elec332 on 16-12-2016.
 */
public abstract class AbstractWorldProvider extends MCAbstractedWorldProvider implements IAbstractedWorldProvider {

    public World getWorld(){
        return this.world;
    }

}
