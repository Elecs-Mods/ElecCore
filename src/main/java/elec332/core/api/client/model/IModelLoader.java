package elec332.core.api.client.model;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Elec332 on 9-12-2015.
 * <p>
 * Can be used by objects that need to make models,
 * Needs to be registered by calling
 * {@link IElecRenderingRegistry#registerLoader(IModelLoader)}
 */
public interface IModelLoader {

    /**
     * A helper method to prevent you from having to hook into the event,
     * use this to make your quads. (This always comes AFTER the textures are loaded)
     *
     * @param quadBakery The QuadBakery.
     */
    @SideOnly(Side.CLIENT)
    public void registerModels(IElecQuadBakery quadBakery, IElecModelBakery modelBakery, IElecTemplateBakery templateBakery);

}
