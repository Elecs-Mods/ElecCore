package elec332.core.compat.waila;

import com.google.common.base.Preconditions;
import elec332.core.api.info.IInformation;
import elec332.core.api.info.InfoMod;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by Elec332 on 19-2-2019
 */
public class WailaInformationType implements IInformation {

    public WailaInformationType(List<ITextComponent> tooltip) {
        this.tooltip = Preconditions.checkNotNull(tooltip);
    }

    private final List<ITextComponent> tooltip;

    @Nonnull
    @Override
    public InfoMod getProviderType() {
        return InfoMod.WAILA;
    }

    @Override
    public void addInformation(ITextComponent text) {
        tooltip.add(text);
    }

}
