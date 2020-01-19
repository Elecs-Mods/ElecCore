package elec332.core.api.info;

import com.google.common.base.Preconditions;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Elec332 on 16-10-2016.
 * <p>
 * Represents the information that will be given to the player
 */
public interface IInformation {

    @Nonnull
    public InfoMod getProviderType();

    default public void addInformation(@Nonnull String line) {
        addInformation(new StringTextComponent(Preconditions.checkNotNull(line)));
    }

    public void addInformation(@Nonnull ITextComponent text);

    @Nonnull
    public Object getInformationComponent();

    @Nullable
    default public Boolean isDebugMode() {
        return null;
    }

    @Nullable
    default public Boolean isExtendedMode() {
        return null;
    }

}
