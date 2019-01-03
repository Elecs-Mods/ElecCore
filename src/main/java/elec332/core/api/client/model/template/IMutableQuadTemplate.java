package elec332.core.api.client.model.template;

import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

/**
 * Created by Elec332 on 29-10-2016.
 */
public interface IMutableQuadTemplate extends IQuadTemplate {

    public IMutableQuadTemplate setV1(Vector3f v1);

    public IMutableQuadTemplate setV2(Vector3f v2);

    public IMutableQuadTemplate setTexture(TextureAtlasSprite texture);

    public IMutableQuadTemplate setSide(EnumFacing side);

    public IMutableQuadTemplate setRotation(ModelRotation rotation);

    public IMutableQuadTemplate setUvData(IUVData uvData);

    public IMutableQuadTemplate setTintIndex(int tintIndex);

}
