package elec332.core.api.client.model.template;

import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Created by Elec332 on 6-12-2015.
 */
@OnlyIn(Dist.CLIENT)
public interface IQuadTemplate {

    Vector3f getV1();

    Vector3f getV2();

    TextureAtlasSprite getTexture();

    Direction getSide();

    ModelRotation getRotation();

    IUVData getUVData();

    int getTintIndex();

    interface IUVData {

        float getUMin();

        float getVMin();

        float getUMax();

        float getVMax();

    }

    interface IMutableUVData extends IUVData {

        IMutableUVData setUMin(float f);

        IMutableUVData setVMin(float f);

        IMutableUVData setUMax(float f);

        IMutableUVData setVMax(float f);

        @Override
        float getUMin();

        @Override
        float getVMin();

        @Override
        float getUMax();

        @Override
        float getVMax();

    }

}
