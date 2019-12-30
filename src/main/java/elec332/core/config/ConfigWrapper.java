package elec332.core.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.google.common.base.Preconditions;
import elec332.core.util.ConstructorPointer;
import elec332.core.util.FMLHelper;
import elec332.core.util.MethodPointer;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by Elec332 on 29-12-2019
 */
public class ConfigWrapper extends AbstractConfigWrapper {

    public ConfigWrapper(@Nonnull Object mod) {
        this(Preconditions.checkNotNull(FMLHelper.getModContainer(mod)));
    }

    public ConfigWrapper(@Nonnull Object mod, @Nonnull ModConfig.Type type) {
        this(Preconditions.checkNotNull(FMLHelper.getModContainer(mod)), type);
    }

    public ConfigWrapper(@Nonnull Object mod, @Nonnull ModConfig.Type type, @Nonnull String fileName) {
        this(Preconditions.checkNotNull(FMLHelper.getModContainer(mod)), type, fileName + TOML_EXTENSION);
    }

    public ConfigWrapper(@Nonnull ModContainer mod) {
        this(mod, ModConfig.Type.COMMON, mod.getModId() + TOML_EXTENSION);
    }

    public ConfigWrapper(@Nonnull ModContainer mod, @Nonnull ModConfig.Type type) {
        this(mod, type, mod.getModId() + "-" + type.extension() + TOML_EXTENSION);
    }

    public ConfigWrapper(@Nonnull ModContainer mod, @Nonnull ModConfig.Type type, @Nonnull String fileName) {
        this.mod = Preconditions.checkNotNull(mod);
        this.type = Preconditions.checkNotNull(type);
        this.fileName = Preconditions.checkNotNull(fileName);
        this.logger = LogManager.getLogger(mod.getModInfo().getDisplayName());
    }

    private final ModContainer mod;
    private final String fileName;
    private final ModConfig.Type type;
    private final Logger logger;

    @Override
    protected void registerConfigSpec() {
        if (FMLHelper.hasReachedState(ModLoadingStage.COMPLETE)) {
            throw new IllegalStateException();
        }
        ModConfig config = new ModConfig(type, getSpec(), mod, fileName);
        mod.addConfig(config);
        logger.info("Registered config: " + fileName);
        if (FMLHelper.hasFMLModContainer(mod)) {
            FMLModContainer mc = FMLHelper.getFMLModContainer(mod);
            mc.getEventBus().addListener((Consumer<? extends ModConfig.Loading>) cfgLoad -> {
                logger.info("Loading config: " + fileName);
                runLoadTasks();
            });
            mc.getEventBus().addListener((Consumer<? extends ModConfig.ConfigReloading>) cfgLoad -> {
                logger.info("Reloading config: " + fileName);
                runLoadTasks();
            });
        }
        if (FMLHelper.hasReachedState(ModLoadingStage.COMMON_SETUP)) {
            final CommentedFileConfig configData = config.getHandler()
                    .reader(FMLPaths.CONFIGDIR.get())
                    .apply(config);
            try {
                getSpec().correct(configData);
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse config!", e);
            }
            setConfig.accept(config, configData);
            sendLoadingEvent.accept(mod, config);
            config.save();
        }
    }

    @Override
    protected void postRegister() {
    }

    private static final BiConsumer<ModConfig, CommentedConfig> setConfig;
    private static final BiConsumer<ModContainer, ModConfig> sendLoadingEvent;

    static {
        MethodPointer<ModConfig, Void> setCfg = new MethodPointer<>(ModConfig.class, "setConfigData", CommentedConfig.class);
        setConfig = setCfg::invoke;
        ConstructorPointer<ModConfig.Loading> crt = new ConstructorPointer<>(ModConfig.Loading.class, ModConfig.class);
        sendLoadingEvent = (mc, cfg) -> mc.dispatchConfigEvent(crt.newInstance(cfg));
    }

}
