package elec332.core.client.model.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.util.vector.Vector3f;

import java.util.List;

/**
 * Created by Elec332 on 21-11-2015.
 */
@SuppressWarnings("deprecation")
public abstract class AbstractItemModel implements IItemModel {

    protected static final ItemCameraTransforms DEFAULT_ITEM_TRANSFORM;
    protected static final ImmutableList<BakedQuad> EMPTY_LIST;

    @Override
    public List<BakedQuad> getFaceQuads(EnumFacing facing) {
        return EMPTY_LIST;
    }

    @Override
    public boolean isAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public final boolean isBuiltInRenderer() {
        return isItemTESR();
    }

    public boolean isItemTESR(){
        return false;
    }

    @Override
    public TextureAtlasSprite getTexture() {
        return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(getTextureLocation().toString());
    }

    public abstract ResourceLocation getTextureLocation();

    @Override
    public IBakedModel handleItemState(ItemStack stack) {
        return this;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return DEFAULT_ITEM_TRANSFORM;
    }

    static {
        DEFAULT_ITEM_TRANSFORM = new ItemCameraTransforms(new ItemTransformVec3f(new Vector3f(-90, 0, 0), applyTranslationScale(new Vector3f(0, 1, -3)), new Vector3f(0.55f, 0.55f, 0.55f)), new ItemTransformVec3f(new Vector3f(0, -135, 25), applyTranslationScale(new Vector3f(0, 4, 2)), new Vector3f(1.7f, 1.7f, 1.7f)), ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT, ItemTransformVec3f.DEFAULT);
        EMPTY_LIST = ImmutableList.of();
    }

    private static Vector3f applyTranslationScale(Vector3f vec){
        vec.scale(0.0625F);
        return vec;
    }


}
