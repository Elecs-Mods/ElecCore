package elec332.core.handler;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import elec332.core.ElecCore;
import elec332.core.api.APIHandlerInject;
import elec332.core.api.discovery.IAnnotationData;
import elec332.core.api.mod.IElecCoreMod;
import elec332.core.api.mod.IElecCoreModHandler;
import elec332.core.api.module.ElecModule;
import elec332.core.api.module.IModuleContainer;
import elec332.core.api.module.IModuleInfo;
import elec332.core.api.module.IModuleManager;
import elec332.core.api.network.INetworkManager;
import elec332.core.api.network.ModNetworkHandler;
import elec332.core.api.registration.IObjectRegister;
import elec332.core.api.world.IWorldGenManager;
import elec332.core.config.ConfigWrapper;
import elec332.core.data.SaveHandler;
import elec332.core.module.DefaultModuleInfo;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;

import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by Elec332 on 13-8-2018.
 */
public class ElecCoreSetup {

    private ElecCoreSetup() {
        throw new UnsupportedOperationException();
    }

    static {
        registerConfigSerializers();
    }

    @APIHandlerInject
    private static INetworkManager networkManager = null;
    @APIHandlerInject
    static IWorldGenManager worldGenManager = null;

    @APIHandlerInject
    private static void onModuleManagerLoaded(IModuleManager moduleManager) {
        moduleManager.registerFieldProcessor(ElecModule.Instance.class, IModuleContainer::getModule);
        moduleManager.registerFieldProcessor(ElecModule.Network.class, iModuleContainer -> networkManager.getAdditionalSimpleNetworkManager(iModuleContainer.getOwnerMod(), new ResourceLocation(iModuleContainer.getOwner(), "module." + iModuleContainer.getName())));
        moduleManager.registerModuleDiscoverer((asmData, moduleControllerGetter) -> {
            List<IModuleInfo> ret = Lists.newArrayList();
            for (IAnnotationData data : asmData.getAnnotationList(ElecModule.class)) {
                try {
                    Map<String, Object> ann = data.getAnnotationInfo();
                    Object o = ann.get("autoDisableIfRequirementsNotMet");
                    boolean autoDisableIfRequirementsNotMet = o == null || (boolean) o;
                    o = ann.get("alwaysEnabled");
                    boolean alwaysOn = o != null && (boolean) o;
                    ret.add(new DefaultModuleInfo((String) ann.get("owner"), (String) ann.get("name"), (String) ann.get("modDependencies"), (String) ann.get("moduleDependencies"), autoDisableIfRequirementsNotMet, alwaysOn, data.getClassName(), moduleControllerGetter.apply((String) data.getAnnotationInfo().get("owner"))));
                } catch (Exception e) {
                    ElecCore.logger.error("Error fetching information for module " + data.getAnnotationInfo().get("name") + " from mod " + data.getAnnotationInfo().get("owner"));
                    ElecCore.logger.error(e);
                }
            }
            return ret;
        });
    }

    @APIHandlerInject
    private static void registerModHandlers(IElecCoreModHandler modHandler) {
        modHandler.registerSimpleFieldHandler(ModNetworkHandler.class, networkManager::getNetworkHandler);
        modHandler.registerModHandler((mc, mod) -> {
            MinecraftForge.EVENT_BUS.addListener((Consumer<FMLServerStartingEvent>) e -> {
                mod.registerCommands(e.getCommandDispatcher());
                mod.registerClientCommands(e.getCommandDispatcher()); //Todo: Move when forge add client commands back
            });
        });
        modHandler.registerModHandler((mc, mod) -> mod.registerSaveHandlers(saveHandler -> SaveHandler.INSTANCE.registerSaveHandler(mc, saveHandler)));
        modHandler.registerModHandler(forFMLMod((mc, mod) -> {
            List<IObjectRegister<?>> list = Lists.newArrayList();
            mod.registerRegisters(list::add, worldGenRegister -> worldGenManager.registerWorldGenRegistry(worldGenRegister, mc));
            if (list.isEmpty()) {
                return;
            }
            mc.getEventBus().register(new Object() {

                @SubscribeEvent
                @SuppressWarnings("all")
                public void registerStuff(RegistryEvent.Register event1) {
                    for (IObjectRegister register : list) {
                        Type ty = (Type) Arrays.stream(register.getClass().getAnnotatedInterfaces())
                                .filter(annotatedType -> annotatedType.getType() instanceof ParameterizedType || (annotatedType.getType() instanceof Class && IObjectRegister.class.isAssignableFrom((Class<?>) annotatedType.getType())))
                                .map(obj -> obj instanceof AnnotatedParameterizedType ? obj : (((Class) obj.getType()).getAnnotatedInterfaces()[0]))
                                .map(apt -> ((AnnotatedParameterizedType) apt).getAnnotatedActualTypeArguments()[0].getType())
                                .findFirst()
                                .get();
                        ty = ((ParameterizedType) ty).getRawType();
                        //ty = ((ParameterizedType) ty).getActualTypeArguments()[0];
                        //if (ty instanceof ParameterizedType) { //TileEntityType also has parameters...
                        //    ty = ((ParameterizedType) ty).getRawType();
                        //}
                        if (ty.equals(event1.getGenericType())) {
                            register.preRegister();
                            register.register(event1.getRegistry());
                        }
                    }
                }

            });
        }));

    }

    private static BiConsumer<ModContainer, IElecCoreMod> forFMLMod(BiConsumer<FMLModContainer, IElecCoreMod> c) {
        return (mc, mod) -> {
            if (mc instanceof FMLModContainer) {
                c.accept((FMLModContainer) mc, mod);
            } else {
                ElecCore.logger.warn("Ignored ObjectRegisters for mod " + mc.getModId() + ", EventBus could not be found...");
            }
        };
    }

    private static void registerConfigSerializers() {
        ConfigWrapper.registerConfigElementSerializer((type, instance, field, data, config, defaultValue, comment) -> {
            if (type.isAssignableFrom(Integer.TYPE)) {
                if (!Strings.isNullOrEmpty(comment)) {
                    config.comment(comment);
                }
                return config.defineInRange(field.getName(), (Integer) defaultValue, (int) data.minValue(), (int) data.maxValue());
            }
            return null;
        });
        ConfigWrapper.registerConfigElementSerializer((type, instance, field, data, config, defaultValue, comment) -> {
            if (type.isAssignableFrom(Boolean.TYPE)) {
                if (!Strings.isNullOrEmpty(comment)) {
                    config.comment(comment);
                }
                return config.define(field.getName(), (boolean) defaultValue);
            }
            return null;
        });
        ConfigWrapper.registerConfigElementSerializer((type, instance, field, data, config, defaultValue, comment) -> {
            if (field.getType().isAssignableFrom(String.class)) {
                if (!Strings.isNullOrEmpty(comment)) {
                    config.comment(comment);
                }
                if (data.validStrings().length > 0) {
                    final List validVals = ImmutableList.copyOf(data.validStrings()); //No List<String>, because compiler warnings...
                    return config.define(field.getName(), (String) defaultValue, validVals::contains);
                } else {
                    return config.define(field.getName(), (String) defaultValue);
                }
            }
            return null;
        });
        ConfigWrapper.registerConfigElementSerializer((type, instance, field, data, config, defaultValue, comment) -> {
            if (field.getType().isAssignableFrom(Float.TYPE)) {
                if (!Strings.isNullOrEmpty(comment)) {
                    config.comment(comment);
                }
                return config.defineInRange(field.getName(), (float) defaultValue, data.minValue(), data.maxValue());
            }
            return null;
        });
    }

}
