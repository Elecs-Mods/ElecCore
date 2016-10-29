package elec332.core.api.info;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 16-10-2016.
 */
public interface IInformation {

    @Nonnull
    public InfoMod getProviderType();

    default public void add(String line){
        addInformation(line);
    }

    public void addInformation(String line);

}
