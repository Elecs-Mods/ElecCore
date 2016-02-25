package elec332.core.asm.asmload;

import elec332.core.client.model.replace.ElecBlockRendererDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Elec332 on 26-11-2015.
 */
public final class ASMHooks {

    @SideOnly(Side.CLIENT)
    public static final class Client {

        public static BlockRendererDispatcher newBlockRendererDispatcher(){
            return new ElecBlockRendererDispatcher(Minecraft.getMinecraft().blockRenderDispatcher);
        }

    }

}
