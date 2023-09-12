package elec332.core.compat.top;

import com.google.common.base.Preconditions;
import elec332.core.api.info.IInformation;
import elec332.core.api.info.InfoMod;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.stream.Collectors;

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
        StringBuilder textBuilder = new StringBuilder();
        for (ITextComponent txt : Preconditions.checkNotNull(text).stream().collect(Collectors.toList())) {
            String formatting = txt.getStyle().getFormattingCode();
            if (txt instanceof TranslationTextComponent) {
                textBuilder.append(formatting)
                        .append(IProbeInfo.STARTLOC)
                        .append(((TranslationTextComponent) txt).getKey())
                        .append(IProbeInfo.ENDLOC);
                if (((TranslationTextComponent) txt).getFormatArgs().length > 0) {
                    throw new UnsupportedOperationException();
                }
            } else if (txt instanceof StringTextComponent) {
                textBuilder.append(formatting)
                        .append(((StringTextComponent) txt).getText());
            } else {
                throw new UnsupportedOperationException();
            }
        }
        info.text(textBuilder.toString());
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
