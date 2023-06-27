package elec332.core.compat.top;

import elec332.core.api.info.IInformation;
import elec332.core.api.info.InfoMod;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Elec332 on 19-1-2020
 */
public class TOPInformationType implements IInformation {

    public TOPInformationType(ProbeMode probeMode, IProbeInfo info) {
        this.probeMode = probeMode;
        this.info = info;
    }

    private final ProbeMode probeMode;
    private final IProbeInfo info;

    @Nonnull
    @Override
    public InfoMod getProviderType() {
        return InfoMod.TOP;
    }

    @Override
    public void addInformation(@Nonnull ITextComponent text) {
        info.text(text);
    }

    @Nonnull
    @Override
    public Object getInformationComponent() {
        return info;
    }

    @Nullable
    @Override
    public Boolean isDebugMode() {
        return probeMode == ProbeMode.DEBUG;
    }

    @Nullable
    @Override
    public Boolean isExtendedMode() {
        return probeMode == ProbeMode.EXTENDED;
    }

}
