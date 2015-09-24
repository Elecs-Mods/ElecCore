package elec332.core.client.render;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fluids.Fluid;

import java.util.List;
import java.util.Map;

/**
 * Created by Elec332 on 31-7-2015.
 */
public class RenderHelper {

    public static final float renderUnit = 1/16f;
    private static List<Block> itemRenders = Lists.newArrayList();
    private static List<Block> blockRenders = Lists.newArrayList();
    private static List<Class<? extends TileEntity>> tileRenders = Lists.newArrayList();
    private static Map<Block, Integer> renderData = Maps.newHashMap();

    public static <A extends AbstractBlockRenderer> A registerBlockRenderer(A renderer){
        if (renderer == null)
            throw new IllegalArgumentException("Cannot register a null renderer!");
        if (!renderer.isISBRH() && !renderer.isItemRenderer() && !renderer.isTESR())
            throw new IllegalArgumentException("Useless render handler detected: "+renderer.getClass());
        if ((renderer.isItemRenderer() || renderer.isISBRH())){
            if (renderer.block == null)
                throw new IllegalArgumentException("Cannot register IItemRenderer or ISBRH for a null Block!");
            if (renderer.isItemRenderer() && itemRenders.contains(renderer.block) || renderer.isISBRH() && blockRenders.contains(renderer.block))
                throw new IllegalArgumentException("Renderer already registered for "+renderer.block.getClass());
        }
        if (renderer.isTESR()) {
            if (renderer.tileClass == null)
                throw new IllegalArgumentException("Cannot register TESR for a null TileEntity class!");
            if (tileRenders.contains(renderer.tileClass))
                throw new IllegalArgumentException("Renderer already registered for "+renderer.tileClass);
        }
        if (renderer.isISBRH()){
            if (renderer.renderID != -1){
                throw new UnsupportedOperationException();
            }
            int r = RenderingRegistry.getNextAvailableRenderId();
            renderer.renderID = r;
            RenderingRegistry.registerBlockHandler(r, renderer);
            blockRenders.add(renderer.block);
            renderData.put(renderer.block, r);
        }
        if (renderer.isItemRenderer()){
            MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(renderer.block), renderer);
            itemRenders.add(renderer.block);
        }
        if (renderer.isTESR()){
            ClientRegistry.bindTileEntitySpecialRenderer(renderer.tileClass, renderer);
            tileRenders.add(renderer.tileClass);
        }
        return renderer;
    }

    public static int getRenderID(Block block){
        return renderData.get(block);
    }

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
