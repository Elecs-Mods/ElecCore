package elec332.core.client.model;

import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Elec332 on 21-11-2015.
 */
@SideOnly(Side.CLIENT)
@SuppressWarnings("unused")
public class ModelEventHandler {

    public ModelEventHandler(){
        if (init)
            throw new RuntimeException();
        MinecraftForge.EVENT_BUS.register(ElecQuadBakery.instance);
        init = true;
    }

    private static boolean init;

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onTextureStitch(TextureStitchEvent event){
        RenderingRegistry.instance().invokeEvent(event);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onJsonModelLoad(ModelLoadEventImpl event){
        RenderingRegistry.instance().invokeEvent(event);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void afterAllModelsBaked(ModelBakeEvent event){
        RenderingRegistry.instance().removeJsonErrors(event.getModelLoader());
    }

}
