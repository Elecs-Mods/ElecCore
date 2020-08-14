package elec332.core.client.model.loading.handler;

import com.google.common.base.Preconditions;
import elec332.core.api.client.model.loading.IItemModelHandler;
import elec332.core.api.client.model.loading.ModelHandler;
import elec332.core.client.RenderHelper;
import elec332.core.client.model.loading.IBlockModelItemLink;
import elec332.core.client.model.loading.INoJsonItem;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/**
 * Created by Elec332 on 11-3-2016.
 */
@ModelHandler
@OnlyIn(Dist.CLIENT)
public class INoJsonItemHandler implements IItemModelHandler {

    public INoJsonItemHandler() {
        modelItemLink = new LinkedItemModel();
        modelBlockItemLink = new LinkedBlockItemModel();
        modelLinkedBlockItem = new LinkedBlockItemModel2();
    }

    private final IBakedModel modelItemLink, modelBlockItemLink, modelLinkedBlockItem;

    @Override
    public boolean handleItem(Item item) {
        return item instanceof INoJsonItem || (item instanceof BlockItem && (((BlockItem) item).getBlock() instanceof INoJsonItem || (((BlockItem) item).getBlock() instanceof IBlockModelItemLink) && ((IBlockModelItemLink) ((BlockItem) item).getBlock()).itemInheritsModel()));
    }

    @Override
    public String getIdentifier(Item item) {
        return "inventory";
    }

    @Override
    public IBakedModel getModelFor(Item item, String identifier, ModelResourceLocation fullResourceLocation) {
        return item instanceof INoJsonItem ? modelItemLink : ((BlockItem) item).getBlock() instanceof INoJsonItem ? modelBlockItemLink : modelLinkedBlockItem;
    }

    private class LinkedBlockItemModel2 extends NullModel {

        private LinkedBlockItemModel2() {
            super(new NoJsonItemOverrideList() {

                @Override
                protected IBakedModel getModel(@Nonnull ItemStack stack, @Nullable World world, @Nullable LivingEntity entity) {
                    IBlockModelItemLink b = (IBlockModelItemLink) ((BlockItem) stack.getItem()).getBlock();
                    return Minecraft.getInstance().getBlockRendererDispatcher().getModelForState(b.getRenderState(stack));
                }

            });
        }

    }

    private class LinkedItemModel extends NullModel {

        private LinkedItemModel() {
            super(new NoJsonItemOverrideList() {

                @Override
                protected IBakedModel getModel(@Nonnull ItemStack stack, @Nullable World world, @Nullable LivingEntity entity) {
                    return ((INoJsonItem) stack.getItem()).getItemModel(stack, world, entity);
                }

            });
        }

    }

    private class LinkedBlockItemModel extends NullModel {

        private LinkedBlockItemModel() {
            super(new NoJsonItemOverrideList() {

                @Override
                protected IBakedModel getModel(@Nonnull ItemStack stack, @Nullable World world, @Nullable LivingEntity entity) {
                    return ((INoJsonItem) (((BlockItem) stack.getItem()).getBlock())).getItemModel(stack, world, entity);
                }

            });
        }

    }

    private static abstract class NoJsonItemOverrideList extends ItemOverrideList {

        protected abstract IBakedModel getModel(@Nonnull ItemStack stack, @Nullable World world, @Nullable LivingEntity entity);

        @Nonnull
        @Override
        @SuppressWarnings("ConstantConditions")
        public final IBakedModel getModelWithOverrides(@Nonnull IBakedModel originalModel, @Nonnull ItemStack stack, @Nullable World world, @Nullable LivingEntity entity) {
            IBakedModel ret = getModel(stack, world, entity);
            ItemOverrideList iol = ret.getOverrides();
            if (iol != null) {
                ret = iol.getModelWithOverrides(ret, stack, world, entity);
            }
            return Preconditions.checkNotNull(ret);
        }

    }

    private static class NullModel implements IBakedModel {

        private NullModel(ItemOverrideList iol) {
            this.iol = iol;
        }

        private final ItemOverrideList iol;

        @Nonnull
        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isAmbientOcclusion() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isGui3d() {
            return false;//throw new UnsupportedOperationException();
        }

        @Override //isSideLit
        public boolean func_230044_c_() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isBuiltInRenderer() {
            throw new UnsupportedOperationException();
        }

        @Override
        @Nonnull
        public TextureAtlasSprite getParticleTexture() {
            return RenderHelper.getMissingTextureIcon();
        }

        @Override
        @SuppressWarnings("deprecation")
        @Nonnull
        public ItemCameraTransforms getItemCameraTransforms() {
            throw new UnsupportedOperationException();
        }

        @Override
        @Nonnull
        public ItemOverrideList getOverrides() {
            return iol;
        }

    }

}
