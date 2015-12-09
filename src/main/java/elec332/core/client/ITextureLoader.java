package elec332.core.client;

import elec332.core.client.model.IIconRegistrar;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Elec332 on 9-12-2015.
 */
public interface ITextureLoader {

    /**
     * Use this to register your textures.
     *
     * @param iconRegistrar The IIconRegistrar.
     */
    @SideOnly(Side.CLIENT)
    public void registerTextures(IIconRegistrar iconRegistrar);

}
