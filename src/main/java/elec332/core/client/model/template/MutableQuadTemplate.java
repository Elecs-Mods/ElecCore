package elec332.core.client.model.template;

import elec332.core.api.client.model.template.IMutableQuadTemplate;
import elec332.core.api.client.model.template.IQuadTemplate;
import elec332.core.client.RenderHelper;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.util.vector.Vector3f;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 6-12-2015.
 */
@SideOnly(Side.CLIENT)
public class MutableQuadTemplate implements IMutableQuadTemplate {

    @Nonnull
    public static MutableQuadTemplate templateForTexture(EnumFacing side, TextureAtlasSprite texture){
        return newTemplate(side).setTexture(texture);
    }

    @Nonnull
    public static MutableQuadTemplate newTemplate(EnumFacing side){
        return new MutableQuadTemplate(side);
    }

    @Nonnull
    public static MutableQuadTemplate copyOf(IQuadTemplate template) {
        if (template == null)
            return newTemplate(EnumFacing.UP);
        MutableQuadTemplate ret = new MutableQuadTemplate(template.getSide());
        ret.v1 = new Vector3f(template.getV1());
        ret.v2 = new Vector3f(template.getV2());
        ret.texture = template.getTexture();
        ret.rotation = template.getRotation();
        ret.uvData = makeImmutable(template.getUVData());
        return ret;
    }

    private MutableQuadTemplate(EnumFacing side){
        this.uvData = DEFAULT_UV;
        this.rotation = ModelRotation.X0_Y0;
        this.side = side;
        this.texture = RenderHelper.getMissingTextureIcon();
        this.v1 = new Vector3f(0, 0, 0);
        this.v2 = new Vector3f(16, 16, 16);
        this.tintIndex = -1;
    }

    private static final IQuadTemplate.IUVData DEFAULT_UV;
    private Vector3f v1, v2;
    private TextureAtlasSprite texture;
    private EnumFacing side;
    private ModelRotation rotation;
    private IQuadTemplate.IUVData uvData;
    private int tintIndex;

    @Override
    public MutableQuadTemplate setV1(Vector3f v1) {
        this.v1 = v1;
        return this;
    }

    @Override
    public MutableQuadTemplate setV2(Vector3f v2) {
        this.v2 = v2;
        return this;
    }

    @Override
    public MutableQuadTemplate setTexture(TextureAtlasSprite texture) {
        this.texture = texture;
        return this;
    }

    @Override
    public MutableQuadTemplate setSide(EnumFacing side) {
        this.side = side;
        return this;
    }

    @Override
    public MutableQuadTemplate setRotation(ModelRotation rotation) {
        this.rotation = rotation;
        return this;
    }

    @Override
    public MutableQuadTemplate setUvData(IQuadTemplate.IUVData uvData) {
        this.uvData = uvData;
        return this;
    }

    @Override
    public MutableQuadTemplate setTintIndex(int tintIndex) {
        if (tintIndex < -1)
            throw new IllegalArgumentException();
        this.tintIndex = tintIndex;
        return this;
    }

    @Override
    public Vector3f getV1() {
        return v1;
    }

    @Override
    public Vector3f getV2() {
        return v2;
    }

    @Override
    public TextureAtlasSprite getTexture() {
        return RenderHelper.checkIcon(texture);
    }

    @Override
    public EnumFacing getSide() {
        return side;
    }

    @Override
    public ModelRotation getRotation() {
        return rotation;
    }

    @Override
    public IUVData getUVData() {
        return uvData;
    }

    @Override
    public int getTintIndex() {
        return tintIndex;
    }

    public static IMutableUVData forUV(float f1, float f2, float f3, float f4){
        return new MutableUVData(f1, f2, f3, f4);
    }

    public static IUVData makeImmutable(IUVData data){
        return new ImmutableUVData(data);
    }


    private static class MutableUVData implements IMutableUVData {

        private MutableUVData(float f1, float f2, float f3, float f4){
            uMin = f1;
            vMin = f2;
            uMax = f3;
            vMax = f4;
        }

        private float uMin, vMin, uMax, vMax;

        @Override
        public IMutableUVData setUMin(float f) {
            this.uMin = f;
            return this;
        }

        @Override
        public IMutableUVData setVMin(float f) {
            this.vMin = f;
            return this;
        }

        @Override
        public IMutableUVData setUMax(float f) {
            this.uMax = f;
            return this;
        }

        @Override
        public IMutableUVData setVMax(float f) {
            this.vMax = f;
            return this;
        }

        @Override
        public float getUMin() {
            return this.uMin;
        }

        @Override
        public float getVMin() {
            return this.vMin;
        }

        @Override
        public float getUMax() {
            return this.uMax;
        }

        @Override
        public float getVMax() {
            return this.vMax;
        }

    }

    private static class ImmutableUVData implements IUVData {

        private ImmutableUVData(IUVData from){
            this(from.getUMin(), from.getVMin(), from.getUMax(), from.getVMax());
        }

        private ImmutableUVData(float f1, float f2, float f3, float f4){
            uMin = f1;
            vMin = f2;
            uMax = f3;
            vMax = f4;
        }

        private final float uMin, vMin, uMax, vMax;

        @Override
        public float getUMin() {
            return this.uMin;
        }

        @Override
        public float getVMin() {
            return this.vMin;
        }

        @Override
        public float getUMax() {
            return this.uMax;
        }

        @Override
        public float getVMax() {
            return this.vMax;
        }

    }

    static {
        DEFAULT_UV = makeImmutable(forUV(0, 0, 16, 16));
    }

}
