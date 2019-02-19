package elec332.core.compat.waila;

import elec332.core.ElecCore;
import elec332.core.api.module.ElecModule;
import elec332.core.compat.ModNames;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.impl.WailaRegistrar;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;

/**
 * Created by Elec332 on 15-8-2015.
 */
@ElecModule(owner = ElecCore.MODID, name = "WailaCompat", modDependencies = ModNames.WAILA)
public class WailaCompatHandler {

    @ElecModule.EventHandler
    public void afterModLoad(FMLLoadCompleteEvent event) {
        IRegistrar registrar = WailaRegistrar.INSTANCE;
        WailaHandlerTileEntity teh = new WailaHandlerTileEntity();
        registrar.registerBlockDataProvider(teh, Block.class);
        registrar.registerComponentProvider(teh, TooltipPosition.BODY, Block.class);
        WailaHandlerEntity eh = new WailaHandlerEntity();
        registrar.registerEntityDataProvider(eh, Entity.class);
        registrar.registerComponentProvider(eh, TooltipPosition.BODY, Entity.class);
    }

}