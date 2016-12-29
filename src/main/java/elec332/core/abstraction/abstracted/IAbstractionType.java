package elec332.core.abstraction.abstracted;

import elec332.core.util.ASMHelper;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;

/**
 * Created by Elec332 on 24-12-2016.
 */
public interface IAbstractionType<T, R extends IForgeRegistryEntry<R>> {

    public Class<? extends T> getAbstractionType();

    public Class<? extends R> getBaseType();

    public Object[] getParams(T item);

    public ASMHelper.IClassModifier getClassModifier();

    public Class<T> getBaseAbstractionType();

    public Class<?> getInterfaceExclusion();

}
