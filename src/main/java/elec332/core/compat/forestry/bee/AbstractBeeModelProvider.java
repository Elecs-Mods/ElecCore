package elec332.core.compat.forestry.bee;

import elec332.core.api.client.IIconRegistrar;
import elec332.core.api.client.model.IElecModelBakery;
import elec332.core.api.client.model.IElecQuadBakery;
import elec332.core.api.client.model.IElecTemplateBakery;
import elec332.core.client.model.loading.IModelAndTextureLoader;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IBeeModelProvider;
import forestry.api.core.IModelManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Elec332 on 15-8-2016.
 */
public abstract class AbstractBeeModelProvider implements IBeeModelProvider, IModelAndTextureLoader {

    public AbstractBeeModelProvider(){
        this.mrl = new ModelResourceLocation[EnumBeeType.VALUES.length];
        for (int i = 0; i < mrl.length; i++) {
            this.mrl[i] = new ModelResourceLocation(new ResourceLocation("beeModelproviders", getClass().getCanonicalName().replace(".", "")), EnumBeeType.VALUES[i].toString());
        }
    }

    private final ModelResourceLocation[] mrl;
    @SideOnly(Side.CLIENT)
    private TextureAtlasSprite[] textures;

    @SideOnly(Side.CLIENT)
    protected abstract ResourceLocation getTextureLocation(EnumBeeType beeType);

    @Override
    @SideOnly(Side.CLIENT)
    public void registerModels(Item item, IModelManager manager) {
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelResourceLocation getModel(EnumBeeType type) {
        return mrl[type.ordinal()];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerTextures(IIconRegistrar iconRegistrar) {
        textures = new TextureAtlasSprite[EnumBeeType.VALUES.length];
        for (int i = 0; i < textures.length; i++) {
            textures[i] = iconRegistrar.registerSprite(getTextureLocation(EnumBeeType.VALUES[i]));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerModels(IElecQuadBakery quadBakery, IElecModelBakery modelBakery, IElecTemplateBakery templateBakery) {
        for (EnumBeeType beeType : EnumBeeType.VALUES) {
            Minecraft.getMinecraft().modelManager.modelRegistry.putObject(getModel(beeType), modelBakery.itemModelForTextures(textures[beeType.ordinal()]));
        }
    }

}
