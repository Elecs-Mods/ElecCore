package elec332.core.client.model.template;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.util.vector.Vector3f;

/**
 * Created by Elec332 on 6-12-2015.
 */@SideOnly(Side.CLIENT)
public interface IQuadTemplate {

    public Vector3f getV1();

    public Vector3f getV2();

    public TextureAtlasSprite getTexture();

    public EnumFacing getSide();

    public ModelRotation getRotation();

    public IUVData getUVData();

    public int getTintIndex();

    public interface IUVData {

        public float getUMin();

        public float getVMin();

        public float getUMax();

        public float getVMax();

    }

    public interface IMutableUVData extends IUVData {

        public IMutableUVData setUMin(float f);

        public IMutableUVData setVMin(float f);

        public IMutableUVData setUMax(float f);

        public IMutableUVData setVMax(float f);

        @Override
        public float getUMin();

        @Override
        public float getVMin();

        @Override
        public float getUMax();

        @Override
        public float getVMax();

    }

}
