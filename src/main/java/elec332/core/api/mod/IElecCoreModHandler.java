package elec332.core.api.mod;

import net.minecraftforge.fml.ModContainer;

import java.lang.annotation.Annotation;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Created by Elec332 on 12-8-2018.
 */
public interface IElecCoreModHandler {

    public void registerSimpleFieldHandler(Class<? extends Annotation> annotation, Function<ModContainer, Object> func);

    public void registerModHandler(BiConsumer<ModContainer, IElecCoreMod> handler);

    public void registerConstructionModHandler(BiConsumer<ModContainer, IElecCoreMod> handler);

}
