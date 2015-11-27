package elec332.core.asm.asmload;

import elec332.core.client.model.replace.ElecBlockRendererDispatcher;
import elec332.core.client.model.replace.ElecItemModelMesher;
import elec332.core.client.model.replace.ElecItemRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Elec332 on 26-11-2015.
 */
public final class ASMHooks {

    @SideOnly(Side.CLIENT)
    public static final class Client {

        private ItemModelMesher m;

        private void  v(){
            initItemRender();
            m = newModelMesher();
            initItemRender();
        }

        public static void initItemRender(){
            Minecraft mc = Minecraft.getMinecraft();
            mc.renderItem = new RenderItem(mc.renderEngine, mc.modelManager);
            mc.renderManager = new RenderManager(mc.renderEngine, mc.renderItem);
            mc.itemRenderer = new ElecItemRenderer(mc);
            System.out.println("Replaced MC ItemRenderer.");
        }

        public static ItemModelMesher newModelMesher(){
            System.out.println("Replacing modelMesher...");
            return new ElecItemModelMesher(Minecraft.getMinecraft().modelManager);
        }

        public static BlockRendererDispatcher newBlockRendererDispatcher(){
            System.out.println("Replacing BlockRendererDispatcher...");
            return new ElecBlockRendererDispatcher(Minecraft.getMinecraft().modelManager.getBlockModelShapes(), Minecraft.getMinecraft().gameSettings);
        }

    }

}
