package elec332.core.api.dimension.N_W;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import elec332.core.main.ElecCore;
import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

/**
 * Created by Elec332 on 27-1-2015.
 */
public class PortalBlurr extends GuiIngameForge{
    public PortalBlurr(Minecraft mc) {
        super(mc);
    }

    @SubscribeEvent
    public void RenderGameOverlayEvent(RenderGameOverlayEvent event){
        float f1 = mc.thePlayer.timeInPortal * event.partialTicks;
       // ElecCore.instance.info("Fired");
        if (!Minecraft.isGuiEnabled())
            if (!mc.thePlayer.isPotionActive(Potion.confusion)){
                //renderPortal(event.resolution.getScaledWidth(), event.resolution.getScaledHeight(), event.partialTicks);
                ElecCore.instance.info("Blurring @ "+String.valueOf(f1));
                func_130015_b(f1, event.resolution.getScaledWidth(), event.resolution.getScaledHeight());}
    }
}
