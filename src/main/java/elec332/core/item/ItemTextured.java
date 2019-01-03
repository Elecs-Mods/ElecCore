package elec332.core.item;

import com.google.common.base.Preconditions;
import elec332.core.api.client.IIconRegistrar;
import elec332.core.api.client.model.IElecModelBakery;
import elec332.core.api.client.model.IElecQuadBakery;
import elec332.core.api.client.model.IElecTemplateBakery;
import elec332.core.client.model.loading.INoJsonItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Created by Elec332 on 21-8-2016.
 */
public class ItemTextured extends AbstractItem implements INoJsonItem {

    public ItemTextured(ResourceLocation rl, Builder itemBuilder) {
        super(itemBuilder);
        setRegistryName(rl);
    }

    @OnlyIn(Dist.CLIENT)
    protected TextureAtlasSprite[] textures;
    @OnlyIn(Dist.CLIENT)
    private IBakedModel model;

    @Override
    @OnlyIn(Dist.CLIENT)
    public IBakedModel getItemModel(ItemStack stack, World world, EntityLivingBase entity) {
        return model;
    }

    /**
     * A helper method to prevent you from having to hook into the event,
     * use this to make your quads. (This always comes AFTER the textures are loaded)
     */
    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerModels(IElecQuadBakery quadBakery, IElecModelBakery modelBakery, IElecTemplateBakery templateBakery) {
        model = modelBakery.itemModelForTextures(textures);
    }

    /**
     * Use this to register your textures.
     *
     * @param iconRegistrar The IIconRegistrar.
     */
    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerTextures(IIconRegistrar iconRegistrar) {
        ResourceLocation[] tl = getTextureLocations();
        textures = new TextureAtlasSprite[tl.length];
        for (int i = 0; i < textures.length; i++) {
            textures[i] = iconRegistrar.registerSprite(tl[i]);
        }
    }

    protected ResourceLocation[] getTextureLocations() {
        return new ResourceLocation[]{
                getTextureLocation()
        };
    }

    protected ResourceLocation getTextureLocation() {
        Preconditions.checkNotNull(getRegistryName());
        return new ResourceLocation(getRegistryName().getNamespace(), "items/" + getRegistryName().getPath());
    }

}
