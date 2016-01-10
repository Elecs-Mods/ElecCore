package elec332.core.client.model.replace;

import elec332.core.asm.asmload.ASMHooks;
import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Created by Elec332 on 26-12-2015.
 */
@SideOnly(Side.CLIENT)
public class ElecRenderItem extends RenderItem {

    public ElecRenderItem(RenderItem renderItem) {
        super(renderItem.textureManager, renderItem.itemModelMesher.getModelManager());
        this.itemModelMesher = ASMHooks.Client.newModelMesher();
        renderItem.itemModelMesher = this.itemModelMesher;
        this.renderItem = renderItem;
        this.renderItem.registerItems();
    }

    private final RenderItem renderItem;

    @Override
    public void registerItems() {
        //NOPE
    }

    /* Link-through */

    @Override
    public boolean func_183005_a(ItemTransformVec3f p_183005_1_) {
        return renderItem.func_183005_a(p_183005_1_);
    }

    @Override
    public void func_175039_a(boolean p_175039_1_) {
        renderItem.func_175039_a(p_175039_1_);
    }

    @Override
    public void renderItem(ItemStack p_181564_1_, ItemCameraTransforms.TransformType p_181564_2_) {
        renderItem.renderItem(p_181564_1_, p_181564_2_);
    }

    @Override
    public void func_181565_a(WorldRenderer p_181565_1_, int p_181565_2_, int p_181565_3_, int p_181565_4_, int p_181565_5_, int p_181565_6_, int p_181565_7_, int p_181565_8_, int p_181565_9_) {
        renderItem.func_181565_a(p_181565_1_, p_181565_2_, p_181565_3_, p_181565_4_, p_181565_5_, p_181565_6_, p_181565_7_, p_181565_8_, p_181565_9_);
    }

    @Override
    public void renderItemModelForEntity(ItemStack stack, EntityLivingBase entityToRenderFor, ItemCameraTransforms.TransformType cameraTransformType) {
        renderItem.renderItemModelForEntity(stack, entityToRenderFor, cameraTransformType);
    }

    @Override
    public void preTransform(ItemStack stack) {
        renderItem.preTransform(stack);
    }

    @Override
    public void renderEffect(IBakedModel model) {
        renderItem.renderEffect(model);
    }

    @Override
    public void renderItemAndEffectIntoGUI(ItemStack stack, int xPosition, int yPosition) {
        renderItem.renderItemAndEffectIntoGUI(stack, xPosition, yPosition);
    }

    @Override
    public void renderItemModelTransform(ItemStack stack, IBakedModel model, ItemCameraTransforms.TransformType cameraTransformType) {
        renderItem.renderItemModelTransform(stack, model, cameraTransformType);
    }

    @Override
    public void setupGuiTransform(int xPosition, int yPosition, boolean isGui3d) {
        renderItem.setupGuiTransform(xPosition, yPosition, isGui3d);
    }

    @Override
    public ItemModelMesher getItemModelMesher() {
        return renderItem.getItemModelMesher();
    }

    @Override
    public void registerItem(Item itm, int subType, String identifier) {
        renderItem.registerItem(itm, subType, identifier);
    }

    @Override
    public void registerItem(Item itm, String identifier) {
        renderItem.registerItem(itm, identifier);
    }

    @Override
    public void registerBlock(Block blk, String identifier) {
        renderItem.registerBlock(blk, identifier);
    }

    @Override
    public void registerBlock(Block blk, int subType, String identifier) {
        renderItem.registerBlock(blk, subType, identifier);
    }

    @Override
    public void renderItem(ItemStack stack, IBakedModel model) {
        renderItem.renderItem(stack, model);
    }

    @Override
    public void renderItemIntoGUI(ItemStack stack, int x, int y) {
        renderItem.renderItemIntoGUI(stack, x, y);
    }

    @Override
    public void renderItemOverlayIntoGUI(FontRenderer fr, ItemStack stack, int xPosition, int yPosition, String text) {
        renderItem.renderItemOverlayIntoGUI(fr, stack, xPosition, yPosition, text);
    }

    @Override
    public void renderItemOverlays(FontRenderer fr, ItemStack stack, int xPosition, int yPosition) {
        renderItem.renderItemOverlays(fr, stack, xPosition, yPosition);
    }

    @Override
    public void renderModel(IBakedModel model, int color) {
        renderItem.renderModel(model, color);
    }

    @Override
    public void renderModel(IBakedModel model, int color, ItemStack stack) {
        renderItem.renderModel(model, color, stack);
    }

    @Override
    public void renderModel(IBakedModel model, ItemStack stack) {
        renderItem.renderModel(model, stack);
    }

    @Override
    public void renderQuad(WorldRenderer renderer, BakedQuad quad, int color) {
        renderItem.renderQuad(renderer, quad, color);
    }

    @Override
    public void renderQuads(WorldRenderer renderer, List<BakedQuad> quads, int color, ItemStack stack) {
        renderItem.renderQuads(renderer, quads, color, stack);
    }

    @Override
    public boolean shouldRenderItemIn3D(ItemStack stack) {
        return renderItem.shouldRenderItemIn3D(stack);
    }

    @Override
    public void putQuadNormal(WorldRenderer renderer, BakedQuad quad) {
        renderItem.putQuadNormal(renderer, quad);
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        renderItem.onResourceManagerReload(resourceManager);
    }

}
