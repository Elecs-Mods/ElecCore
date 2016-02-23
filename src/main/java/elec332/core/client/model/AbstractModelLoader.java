package elec332.core.client.model;

import elec332.core.client.model.model.IModelAndTextureLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Elec332 on 21-11-2015.
 */
@SideOnly(Side.CLIENT)
public abstract class AbstractModelLoader implements IModelAndTextureLoader {

    public AbstractModelLoader(){
        RenderingRegistry.instance().registerLoader(this);
    }

}
