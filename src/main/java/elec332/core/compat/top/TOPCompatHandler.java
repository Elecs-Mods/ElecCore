package elec332.core.compat.top;

import com.google.common.base.Function;
import elec332.core.ElecCore;
import elec332.core.api.module.ElecModule;
import elec332.core.compat.ModNames;
import mcjty.theoneprobe.api.ITheOneProbe;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import javax.annotation.Nullable;

/**
 * Created by Elec332 on 19-1-2020
 */
@ElecModule(owner = ElecCore.MODID, name = "TOPCompat", modDependencies = ModNames.THE_ONE_PROBE)
public class TOPCompatHandler implements Function<ITheOneProbe, Void> { //Function and not a Consumer... :/

    @ElecModule.EventHandler
    public void afterModLoad(FMLCommonSetupEvent event) {
        InterModComms.sendTo(ModNames.THE_ONE_PROBE, "getTheOneProbe", () -> this);
    }

    @Nullable
    @Override
    public Void apply(@Nullable ITheOneProbe input) {
        if (input == null) {
            throw new IllegalArgumentException();
        }
        input.registerProvider(new TOPHandlerBlock());
        input.registerEntityProvider(new TOPHandlerEntity());
        return null;
    }

}
