package elec332.core.handler;

import com.google.common.collect.Lists;
import elec332.core.ElecCore;
import elec332.core.api.APIHandlerInject;
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
import elec332.core.util.CommandHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
        moduleManager.registerFieldProcessor(ElecModule.Network.class, iModuleContainer -> networkManager.getAdditionalSimpleNetworkManager(iModuleContainer.getOwnerMod(), iModuleContainer.getName()));
        moduleManager.registerModuleDiscoverer((asmData, moduleControllerGetter) -> {
            List<IModuleInfo> ret = Lists.newArrayList();
            for (ASMDataTable.ASMData data : asmData.getAnnotationList(ElecModule.class)) {
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
        modHandler.registerModHandler((mc, mod) -> mod.registerClientCommands(CommandHelper.getClientCommandRegistry()));
        modHandler.registerModHandler((mc, mod) -> mod.registerServerCommands(CommandHelper.getServerCommandRegistry()));
        modHandler.registerModHandler((mc, mod) -> mod.registerSaveHandlers(saveHandler -> SaveHandler.INSTANCE.registerSaveHandler(mc, saveHandler)));
        modHandler.registerModHandler((mc, mod) -> {
            mc.getMetadata().autogenerated = false; //Enables mod enable/disable & config-GUI buttons
        });
        modHandler.registerModHandler((mc, mod) -> {
            List<IObjectRegister<?>> list = Lists.newArrayList();
            mod.registerRegisters(list::add);
            if (!list.isEmpty()) {
                MinecraftForge.EVENT_BUS.register(new Object() {

                    @SubscribeEvent
                    @SuppressWarnings("all")
                    public void registerStuff(RegistryEvent.Register event1) {
                        for (IObjectRegister register : list) {
                            Type ty = Arrays.stream(register.getClass().getAnnotatedInterfaces())
                                    .filter(annotatedType -> annotatedType.getType() instanceof ParameterizedType)
                                    .filter(annotatedType -> ((ParameterizedType) register.getClass().getAnnotatedInterfaces()[0].getType()).getRawType().equals(IObjectRegister.class))
                                    .findFirst()
                                    .get()
                                    .getType();

                            if (((ParameterizedType) ty).getActualTypeArguments()[0].equals(event1.getGenericType())) {
                                register.preRegister();
                                register.register(event1.getRegistry());
                            }
                        }
                    }

                });
            }
        });

    }

    private static void registerConfigSerializers() {
        ConfigWrapper.registerConfigElementSerializer((type, instance, field, data, config, category, defaultValue, comment) -> {
            if (type.isAssignableFrom(Integer.TYPE)) {
                field.set(instance, config.getInt(field.getName(), category, (Integer) defaultValue, (int) data.minValue(), (int) data.maxValue(), comment));
                return true;
            }
            return false;
        });
        ConfigWrapper.registerConfigElementSerializer((type, instance, field, data, config, category, defaultValue, comment) -> {
            if (type.isAssignableFrom(Boolean.TYPE)) {
                field.set(instance, config.getBoolean(field.getName(), category, (Boolean) defaultValue, comment));
                return true;
            }
            return false;
        });
        ConfigWrapper.registerConfigElementSerializer((type, instance, field, data, config, category, defaultValue, comment) -> {
            if (field.getType().isAssignableFrom(String.class)) {
                if (data.validStrings().length > 0) {
                    field.set(instance, config.getString(field.getName(), category, (String) defaultValue, comment, data.validStrings()));
                } else {
                    field.set(instance, config.getString(field.getName(), category, (String) defaultValue, comment));
                }
                return true;
            }
            return false;
        });
        ConfigWrapper.registerConfigElementSerializer((type, instance, field, data, config, category, defaultValue, comment) -> {
            if (field.getType().isAssignableFrom(Float.TYPE)) {
                field.set(instance, config.getFloat(field.getName(), category, (Float) defaultValue, data.minValue(), data.maxValue(), comment));
                return true;
            }
            return false;
        });
    }

}