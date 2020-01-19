package elec332.core.client.model;

import com.google.common.base.Preconditions;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Created by Elec332 on 3-2-2019
 */
public class WrappedModel implements IDynamicBakedModel {

    public WrappedModel(IBakedModel parent) {
        this.parent = Preconditions.checkNotNull(parent);
    }

    protected final IBakedModel parent;

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        return parent.getQuads(state, side, rand, extraData);
    }

    @Override
    public boolean isAmbientOcclusion() {
        return parent.isAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return parent.isGui3d();
    }

    @Override
    public boolean isBuiltInRenderer() {
        return parent.isBuiltInRenderer();
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public TextureAtlasSprite getParticleTexture() {
        return parent.getParticleTexture();
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public ItemCameraTransforms getItemCameraTransforms() {
        return parent.getItemCameraTransforms();
    }

    @Nonnull
    @Override
    public ItemOverrideList getOverrides() {
        return parent.getOverrides();
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
        return Pair.of(this, Optional.ofNullable(parent.handlePerspective(cameraTransformType)).map(Pair::getValue).orElse(null));
    }

}
