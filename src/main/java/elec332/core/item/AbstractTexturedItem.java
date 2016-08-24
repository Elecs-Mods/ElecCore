package elec332.core.item;

import elec332.core.client.IIconRegistrar;
import elec332.core.client.model.ElecModelBakery;
import elec332.core.client.model.ElecQuadBakery;
import elec332.core.client.model.INoJsonItem;
import elec332.core.client.model.template.ElecTemplateBakery;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Elec332 on 21-8-2016.
 */
public abstract class AbstractTexturedItem extends AbstractItem implements INoJsonItem {

    public AbstractTexturedItem(ResourceLocation rl) {
        super(rl);
    }

    @SideOnly(Side.CLIENT)
    private TextureAtlasSprite[] textures;
    @SideOnly(Side.CLIENT)
    private IBakedModel model;

    @Override
    @SideOnly(Side.CLIENT)
    public IBakedModel getItemModel(ItemStack stack, World world, EntityLivingBase entity) {
        return model;
    }

    /**
     * A helper method to prevent you from having to hook into the event,
     * use this to make your quads. (This always comes AFTER the textures are loaded)
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void registerModels(ElecQuadBakery quadBakery, ElecModelBakery modelBakery, ElecTemplateBakery templateBakery) {
        model = modelBakery.itemModelForTextures(textures);
    }

    /**
     * Use this to register your textures.
     *
     * @param iconRegistrar The IIconRegistrar.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void registerTextures(IIconRegistrar iconRegistrar) {
        ResourceLocation[] tl = getTextureLocations();
        textures = new TextureAtlasSprite[tl.length];
        for (int i = 0; i < textures.length; i++) {
            textures[i] = iconRegistrar.registerSprite(tl[i]);
        }
    }

    protected ResourceLocation[] getTextureLocations(){
        return new ResourceLocation[]{
            getTextureLocation()
        };
    }

    protected ResourceLocation getTextureLocation(){
        return new ResourceLocation(getRegistryName().getResourceDomain(), "items/"+getRegistryName().getResourcePath());
    }

}
