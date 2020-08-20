package elec332.core.api.client.model;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Created by Elec332 on 9-12-2015.
 * <p>
 * Can be used by objects that need to make models,
 * Needs to be registered by calling
 * {@link IRenderingRegistry#registerLoader(IModelLoader)}
 */
public interface IModelLoader {

    /**
     * A helper method to prevent you from having to hook into the event,
     * use this to make your quads. (This always comes AFTER the textures are loaded)
     *
     * @param quadBakery The QuadBakery.
     */
    @OnlyIn(Dist.CLIENT)
    void registerModels(IQuadBakery quadBakery, IModelBakery modelBakery, ITemplateBakery templateBakery);

}
