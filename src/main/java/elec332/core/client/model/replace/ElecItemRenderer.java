package elec332.core.client.model.replace;

import elec332.core.client.model.RenderingRegistry;
import elec332.core.client.render.ISpecialItemRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * Created by Elec332 on 19-11-2015.
 */
@SideOnly(Side.CLIENT)
public class ElecItemRenderer extends ItemRenderer {

    public ElecItemRenderer(ItemRenderer itemRenderer) {
        super(itemRenderer.mc);
        this.itemRenderer = itemRenderer;
    }

    private final ItemRenderer itemRenderer;

    @Override
    public void renderItem(EntityLivingBase entityIn, ItemStack heldStack, ItemCameraTransforms.TransformType p_178099_3_) {
        itemRenderer.renderItem(entityIn, heldStack, p_178099_3_);
    }

    @Override
    public void renderItemInFirstPerson(float partialTickTime) {
        ISpecialItemRenderer renderer = getRendererFor(itemRenderer.itemToRender);
        if (renderer != null){
            if (renderer.shouldUseSpecialRendererInFirstPerson(itemRenderer.itemToRender)) {
                if (!renderer.shouldRendererHelpOut(itemRenderer.itemToRender)){
                    renderer.renderInFirstPerson(itemRenderer.itemToRender, partialTickTime);
                    return;
                } else {
                    EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
                    float f1 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTickTime;
                    float f2 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * partialTickTime;
                    GlStateManager.pushMatrix();
                    GlStateManager.rotate(f1, 1.0F, 0.0F, 0.0F);
                    GlStateManager.rotate(f2, 0.0F, 1.0F, 0.0F);
                    RenderHelper.enableStandardItemLighting();
                    GlStateManager.popMatrix();
                    int i = Minecraft.getMinecraft().theWorld.getCombinedLight(new BlockPos(player.posX, player.posY + (double) player.getEyeHeight(), player.posZ), 0);
                    float fl1 = (float) (i & 65535);
                    float fl2 = (float) (i >> 16);
                    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, fl1, fl2);
                    float fa1 = player.prevRenderArmPitch + (player.renderArmPitch - player.prevRenderArmPitch) * partialTickTime;
                    float fa2 = player.prevRenderArmYaw + (player.renderArmYaw - player.prevRenderArmYaw) * partialTickTime;
                    GlStateManager.rotate((player.rotationPitch - fa1) * 0.1F, 1.0F, 0.0F, 0.0F);
                    GlStateManager.rotate((player.rotationYaw - fa2) * 0.1F, 0.0F, 1.0F, 0.0F);
                    GlStateManager.enableRescaleNormal();
                    GlStateManager.pushMatrix();

                    renderer.renderInFirstPerson(itemRenderer.itemToRender, partialTickTime);

                    //renderItem(player, this.itemToRender, ItemCameraTransforms.TransformType.FIRST_PERSON);
                    GlStateManager.popMatrix();
                    GlStateManager.disableRescaleNormal();
                    RenderHelper.disableStandardItemLighting();
                    return;
                }
            }
        }
        itemRenderer.renderItemInFirstPerson(partialTickTime);
    }

    @Nullable
    private ISpecialItemRenderer getRendererFor(ItemStack stack){
        if (stack == null || stack.getItem() == null || !RenderingRegistry.instance().hasSpecialFirstPersonRenderer(stack.getItem()))
            return null;
        return RenderingRegistry.instance().getRendererFor(stack.getItem());
    }

    /* Link-through */

    @Override
    public void renderOverlays(float partialTicks) {
        itemRenderer.renderOverlays(partialTicks);
    }

    @Override
    public void updateEquippedItem() {
        itemRenderer.updateEquippedItem();
    }

    @Override
    public void resetEquippedProgress() {
        itemRenderer.resetEquippedProgress();
    }

    @Override
    public void resetEquippedProgress2() {
        itemRenderer.resetEquippedProgress2();
    }

}
