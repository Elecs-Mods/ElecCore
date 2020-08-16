package elec332.core.api.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 23-12-2019
 */
public interface ITESRItem {

    @OnlyIn(Dist.CLIENT)
    void renderItem(ItemStack stack, @Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer renderTypeBuffer, int combinedLightIn, int combinedOverlayIn);

}
