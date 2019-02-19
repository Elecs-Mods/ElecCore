package elec332.core.api.info;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 16-10-2016.
 * <p>
 * Represents the information that will be given to the player
 */
public interface IInformation {

    @Nonnull
    public InfoMod getProviderType();

    default public void addInformation(String line) {
        addInformation(new TextComponentString(line));
    }

    public void addInformation(ITextComponent text);

}
