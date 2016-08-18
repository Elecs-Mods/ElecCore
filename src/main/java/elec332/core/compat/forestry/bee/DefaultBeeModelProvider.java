package elec332.core.compat.forestry.bee;

import elec332.core.compat.forestry.bee.AbstractBeeModelProvider;
import forestry.api.apiculture.EnumBeeType;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Elec332 on 15-8-2016.
 */
public class DefaultBeeModelProvider extends AbstractBeeModelProvider {

    public DefaultBeeModelProvider(ResourceLocation textureFolder){
        this.rl = textureFolder;
    }

    private final ResourceLocation rl;

    @Override
    protected ResourceLocation getTextureLocation(EnumBeeType beeType) {
        return new ResourceLocation(rl.getResourceDomain(), rl.getResourcePath()+"."+beeType.toString().toLowerCase());
    }

}
