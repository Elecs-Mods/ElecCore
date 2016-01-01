package elec332.core.asm.asmload;

import elec332.core.client.model.replace.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Elec332 on 26-11-2015.
 */
public final class ASMHooks {

    @SideOnly(Side.CLIENT)
    public static final class Client {

        public static void initItemRender(){
            Minecraft mc = Minecraft.getMinecraft();
            mc.renderItem = new ElecRenderItem(mc.renderItem);
            mc.renderManager = new RenderManager(mc.renderEngine, mc.renderItem);
            mc.itemRenderer = new ElecItemRenderer(mc.itemRenderer);
            mc.mcResourceManager.registerReloadListener(mc.renderItem);
            TileEntityItemStackRenderer.instance = new ElecTileEntityItemStackRenderer();
        }

        public static ItemModelMesher newModelMesher(){
            return new ElecItemModelMesher(Minecraft.getMinecraft().renderItem.itemModelMesher);
        }

        public static BlockRendererDispatcher newBlockRendererDispatcher(){
            return new ElecBlockRendererDispatcher(Minecraft.getMinecraft().blockRenderDispatcher);
        }

    }

}
