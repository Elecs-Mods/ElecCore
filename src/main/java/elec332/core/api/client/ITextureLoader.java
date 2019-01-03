package elec332.core.api.client;

import elec332.core.api.client.model.IElecRenderingRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Created by Elec332 on 9-12-2015.
 * <p>
 * Can be used by objects that need to register textures.
 * Needs to be registered by calling
 * {@link IElecRenderingRegistry#registerLoader(ITextureLoader)}
 */
public interface ITextureLoader {

    /**
     * Use this to register your textures.
     *
     * @param iconRegistrar The IIconRegistrar.
     */
    @OnlyIn(Dist.CLIENT)
    public void registerTextures(IIconRegistrar iconRegistrar);

}
