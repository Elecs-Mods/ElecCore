package elec332.core.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

/**
 * Created by Elec332 on 31-7-2015.
 */
public class RenderHelper {

    public static final float renderUnit = 1/16f;

    public static void bindBlockTextures(){
        bindTexture(getBlocksResourceLocation());
    }

    public static void bindTexture(ResourceLocation rl){
        Minecraft.getMinecraft().renderEngine.bindTexture(rl);
    }

    public static IIcon checkIcon(IIcon icon) {
        if (icon == null)
            return getMissingTextureIcon();
        return icon;
    }

    public static IIcon getFluidTexture(Fluid fluid, boolean flowing) {
        if (fluid == null)
            return getMissingTextureIcon();
        return checkIcon(flowing ? fluid.getFlowingIcon() : fluid.getStillIcon());
    }

    public static IIcon getMissingTextureIcon(){
        return ((TextureMap) Minecraft.getMinecraft().getTextureManager().getTexture(getBlocksResourceLocation())).getAtlasSprite("missingno");
    }

    public static ResourceLocation getBlocksResourceLocation(){
        return TextureMap.locationBlocksTexture;
    }

    public void spawnParticle(EntityFX particle){
        Minecraft.getMinecraft().effectRenderer.addEffect(particle);
    }

}
