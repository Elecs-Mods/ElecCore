package elec332.core.util;

import net.minecraft.util.IStringSerializable;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 23-6-2017.
 */
public interface IDefaultStringSerializable extends IStringSerializable {

    @Nonnull
    @Override
    default public String getName(){
        return ((Enum) this).name().toLowerCase();
    }

}
