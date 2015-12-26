package elec332.core.client.model.replace;

import elec332.core.asm.asmload.ASMHooks;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.ModelManager;

/**
 * Created by Elec332 on 26-12-2015.
 */
public class ElecRenderItem extends RenderItem {

    public ElecRenderItem(TextureManager textureManager, ModelManager modelManager) {
        super(textureManager, modelManager);
        this.itemModelMesher = ASMHooks.Client.newModelMesher();
        this.registerItems();
    }

}
