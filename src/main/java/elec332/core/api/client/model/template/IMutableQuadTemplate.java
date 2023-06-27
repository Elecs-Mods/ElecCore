package elec332.core.api.client.model.template;

import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;

/**
 * Created by Elec332 on 29-10-2016.
 */
public interface IMutableQuadTemplate extends IQuadTemplate {

    IMutableQuadTemplate setV1(Vector3f v1);

    IMutableQuadTemplate setV2(Vector3f v2);

    IMutableQuadTemplate setTexture(TextureAtlasSprite texture);

    IMutableQuadTemplate setSide(Direction side);

    IMutableQuadTemplate setRotation(ModelRotation rotation);

    IMutableQuadTemplate setUvData(IUVData uvData);

    IMutableQuadTemplate setTintIndex(int tintIndex);

}
