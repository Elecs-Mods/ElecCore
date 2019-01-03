package elec332.core.client.model.loading.handler;

import elec332.core.api.client.model.loading.IItemModelHandler;
import elec332.core.api.client.model.loading.ModelHandler;
import elec332.core.client.RenderHelper;
import elec332.core.client.model.loading.IBlockModelItemLink;
import elec332.core.client.model.loading.INoJsonItem;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
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
        modelItemBlockLink = new LinkedItemBlockModel();
        modelLinkedItemBlock = new LinkedItemBlockModel2();
    }

    private final IBakedModel modelItemLink, modelItemBlockLink, modelLinkedItemBlock;

    @Override
    public boolean handleItem(Item item) {
        return item instanceof INoJsonItem || (item instanceof ItemBlock && (((ItemBlock) item).getBlock() instanceof INoJsonItem || (((ItemBlock) item).getBlock() instanceof IBlockModelItemLink) && ((IBlockModelItemLink) ((ItemBlock) item).getBlock()).itemInheritsModel()));
    }

    @Override
    public String getIdentifier(Item item) {
        return "inventory";
    }

    @Override
    public IBakedModel getModelFor(Item item, String identifier, ModelResourceLocation fullResourceLocation) {
        return item instanceof INoJsonItem ? modelItemLink : ((ItemBlock) item).getBlock() instanceof INoJsonItem ? modelItemBlockLink : modelLinkedItemBlock;
    }

    private class LinkedItemBlockModel2 extends NullModel {

        private LinkedItemBlockModel2() {
            super(new NoJsonItemOverrideList(null) {

                @Nonnull
                @Override
                @SuppressWarnings("all")
                public IBakedModel getModelWithOverrides(@Nonnull IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
                    IBlockModelItemLink b = (IBlockModelItemLink) ((ItemBlock) stack.getItem()).getBlock();
                    IBakedModel ret = Minecraft.getInstance().getBlockRendererDispatcher().getModelForState(b.getRenderState(stack));
                    ret = ret.getOverrides().getModelWithOverrides(ret, stack, world, entity);
                    return ret;
                }

            });
        }

    }

    private class LinkedItemModel extends NullModel {

        private LinkedItemModel() {
            super(new NoJsonItemOverrideList(null) {

                @Override
                @Nonnull
                public IBakedModel getModelWithOverrides(@Nonnull IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
                    return ((INoJsonItem) stack.getItem()).getItemModel(stack, world, entity);
                }

            });
        }

    }

    private class LinkedItemBlockModel extends NullModel {

        private LinkedItemBlockModel() {
            super(new NoJsonItemOverrideList(null) {

                @Override
                @Nonnull
                public IBakedModel getModelWithOverrides(@Nonnull IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
                    return ((INoJsonItem) (((ItemBlock) stack.getItem()).getBlock())).getItemModel(stack, world, entity);
                }

            });
        }

    }


    private class NoJsonItemOverrideList extends ItemOverrideList {

        private NoJsonItemOverrideList(INoJsonItem item) {
            this.item = item;
        }

        private final INoJsonItem item;

        @Override
        @Nonnull
        public IBakedModel getModelWithOverrides(@Nonnull IBakedModel originalModel, ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity) {
            return item.getItemModel(stack, world, entity);
        }

    }

    private class NullModel implements IBakedModel {

        private NullModel(ItemOverrideList iol) {
            this.iol = iol;
        }

        private final ItemOverrideList iol;

        @Override
        public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, Random rand) {
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
