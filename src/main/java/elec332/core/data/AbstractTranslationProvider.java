package elec332.core.data;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;

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

}
