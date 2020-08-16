package elec332.core.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.fluid.Fluid;
import net.minecraftforge.common.data.LanguageProvider;

import java.util.function.Supplier;

/**
 * Created by Elec332 on 15-7-2020
 */
public abstract class AbstractTranslationProvider extends LanguageProvider {

    public AbstractTranslationProvider(DataGenerator gen, String modid) {
        this(gen, modid, "en_us");
    }

    public AbstractTranslationProvider(DataGenerator gen, String modid, String locale) {
        super(gen, modid, locale);
    }

    @Override
    protected final void addTranslations() {
        registerTranslations();
    }

    protected abstract void registerTranslations();

    public void addFluid(Supplier<Fluid> fluid, String name) {
        add(fluid.get(), name);
    }

    public void add(Fluid fluid, String name) {
        add(fluid.getAttributes().getTranslationKey(), name);
    }

}
