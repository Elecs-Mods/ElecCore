package elec332.core.client.render;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.client.IItemRenderer;

/**
 * Created by Elec332 on 22-9-2015.
 */
@SideOnly(Side.CLIENT)
public abstract class AbstractBlockRenderer extends TileEntitySpecialRenderer implements ISimpleBlockRenderingHandler, IItemRenderer {

    public AbstractBlockRenderer(Block block){
        this(block, null);
    }

    public AbstractBlockRenderer(Class<? extends TileEntity> tileClass){
        this(null, tileClass);
    }

    public AbstractBlockRenderer(Block block, Class<? extends TileEntity> tileClass){
        this.block = block;
        this.tileClass = tileClass;
    }

    protected final Block block;
    final Class<? extends TileEntity> tileClass;

    int renderID = -1;
    private final Tessellator tessellator = Tessellator.instance;

    @Override
    public final boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return type != ItemRenderType.FIRST_PERSON_MAP;
    }

    @Override
    public final boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public final void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        renderItem(item, type, (RenderBlocks) data[0]);
    }

    @Override
    public final void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
    }

    @Override
    public final boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        return validForRendering(modelId) && doRenderInWorld(world, x, y, z, null, renderer, 0, false);
    }

    @Override
    public final boolean shouldRender3DInInventory(int modelId) {
        return false;
    }

    protected boolean validForRendering(int i){
        return i == renderID;
    }

    @Override
    public final int getRenderId() {
        return renderID;
    }

    @Override
    public final void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTicks) {
        doRenderInWorld(tile.getWorldObj(), x, y, z, tile, RenderBlocks.getInstance(), partialTicks, true);
    }

    private boolean doRenderInWorld(IBlockAccess world, double x, double y, double z, TileEntity tile, RenderBlocks renderer, float partialTicks, boolean tesr){
        tessellator.setColorRGBA_F(1, 1, 1, 1);
        return renderBlockAt(world, x, y, z, tile, renderer, partialTicks, tesr);
    }

    protected abstract boolean renderBlockAt(IBlockAccess world, double x, double y, double z, TileEntity tile, RenderBlocks renderer, float partialTicks, boolean tesr);

    protected abstract void renderItem(ItemStack stack, ItemRenderType renderType, RenderBlocks renderer);

    protected abstract boolean isISBRH();

    protected abstract boolean isItemRenderer();

    protected abstract boolean isTESR();

}
