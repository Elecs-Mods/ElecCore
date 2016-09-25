package elec332.core.client.newstuff;

import com.google.common.collect.ImmutableList;
import elec332.core.client.RenderHelper;
import elec332.core.client.model.INoJsonItem;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by Elec332 on 11-3-2016.
 */
@ModelHandler
@SideOnly(Side.CLIENT)
public class INoJsonItemHandler implements IItemModelHandler {

    public INoJsonItemHandler(){
        modelItemLink = new LinkedItemModel();
        modelItemBlockLink = new LinkedItemBlockModel();
    }

    private final IBakedModel modelItemLink, modelItemBlockLink;

    @Override
    public boolean handleItem(Item item) {
        return item instanceof INoJsonItem || (item instanceof ItemBlock && ((ItemBlock) item).getBlock() instanceof INoJsonItem);
    }

    @Override
    public String getIdentifier(Item item) {
        return "inventory";
    }

    @Override
    public IBakedModel getModelFor(Item item, String identifier, ModelResourceLocation fullResourceLocation) {
        return item instanceof INoJsonItem ? modelItemLink : modelItemBlockLink;
    }

    private class LinkedItemModel extends NullModel {

        private LinkedItemModel() {
            super(new NoJsonItemOverrideList(null){

                @Override
                @Nonnull
                public IBakedModel handleItemState(@Nonnull IBakedModel originalModel, ItemStack stack, @Nonnull World world, @Nonnull EntityLivingBase entity) {
                    return ((INoJsonItem)stack.getItem()).getItemModel(stack, world, entity);
                }

            });
        }

    }

    private class LinkedItemBlockModel extends NullModel {

        private LinkedItemBlockModel() {
            super(new NoJsonItemOverrideList(null){

                @Override
                @Nonnull
                public IBakedModel handleItemState(@Nonnull IBakedModel originalModel, ItemStack stack, @Nonnull World world, @Nonnull EntityLivingBase entity) {
                    return ((INoJsonItem)(((ItemBlock)stack.getItem()).getBlock())).getItemModel(stack, world, entity);
                }

            });
        }

    }


    private class NoJsonItemOverrideList extends ItemOverrideList {

        private NoJsonItemOverrideList(INoJsonItem item) {
            super(ImmutableList.<ItemOverride>of());
            this.item = item;
        }

        private final INoJsonItem item;

        @Override
        @Nonnull
        public IBakedModel handleItemState(@Nonnull IBakedModel originalModel, ItemStack stack, @Nonnull World world, @Nonnull EntityLivingBase entity) {
            return item.getItemModel(stack, world, entity);
        }

    }

    private class NullModel implements IBakedModel {

        private NullModel(ItemOverrideList iol){
            this.iol = iol;
        }

        private final ItemOverrideList iol;

        @Override
        @Nonnull
        public List<BakedQuad> getQuads(IBlockState p_188616_1_, EnumFacing p_188616_2_, long p_188616_3_) {
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
