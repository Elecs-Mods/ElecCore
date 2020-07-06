package elec332.core.client.model.legacy;

import com.google.common.base.Preconditions;
import elec332.core.api.client.ITextureLocation;
import elec332.core.client.RenderHelper;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Elec332 on 6-7-2020
 */
public class LegacyTextureLocation implements ITextureLocation {

    public LegacyTextureLocation(ResourceLocation location) {
        this.tex = Preconditions.checkNotNull(location);
        this.renderType = RenderHelper.createRenderType("ctiv_render_type", RenderType.getCutout(), location);
    }

    private final ResourceLocation tex;
    private final RenderType renderType;

    @Override
    public ResourceLocation getTextureLocation() {
        return tex;
    }

    public RenderType getRenderType() {
        return renderType;
    }

}
