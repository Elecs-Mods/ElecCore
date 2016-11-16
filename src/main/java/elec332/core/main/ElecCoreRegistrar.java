package elec332.core.main;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import elec332.core.api.info.IInfoProvider;
import elec332.core.api.info.IInfoProviderEntity;
import elec332.core.api.registry.IDualObjectRegistry;
import elec332.core.api.registry.ISingleObjectRegistry;
import elec332.core.api.registry.ISingleRegister;
import elec332.core.api.registry.SimpleRegistries;
import elec332.core.api.world.IAdvancedChunkPopulator;
import elec332.core.api.world.IFeatureGenerator;
import elec332.core.api.world.IWorldGenHook;
import elec332.core.compat.waila.IWailaCapabilityDataProvider;
import elec332.core.grid.IStructureWorldEventHandler;
import elec332.core.util.FMLUtil;
import net.minecraftforge.common.capabilities.Capability;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;

/**
 * Created by Elec332 on 1-8-2016.
 */
@SuppressWarnings({"deprecation", "unchecked"})
public final class ElecCoreRegistrar {

    private ElecCoreRegistrar(){
        throw new IllegalAccessError();
    }

    public static final ISingleObjectRegistry<IStructureWorldEventHandler> GRIDHANDLERS;
    @Deprecated
    public static final IDualObjectRegistry<Capability, IWailaCapabilityDataProvider> WAILA_CAPABILITY_PROVIDER;
    public static final ISingleObjectRegistry<IInfoProvider> INFORMATION_PROVIDERS;
    public static final ISingleObjectRegistry<IInfoProviderEntity> INFORMATION_PROVIDERS_ENTITY;
    public static final ISingleObjectRegistry<IWorldGenHook> WORLD_GEN_HOOKS;
    public static final ISingleRegister<IAdvancedChunkPopulator> WORLD_POPULATORS;
    public static final ISingleRegister<IFeatureGenerator> WORLD_FEATURE_GENERATORS;

    static {
        IN_MOD_LOADING = input -> FMLUtil.isInModInitialisation();
        WAILA_CAPABILITY_PROVIDER = SimpleRegistries.newDualObjectRegistry(Predicates.and(new Predicate<Pair<Capability, IWailaCapabilityDataProvider>>() {

            @Override
            public boolean apply(@Nullable Pair<Capability, IWailaCapabilityDataProvider> input) {
                return input.getValue().isCompatibleCapability(input.getKey());
            }

        }, ElecCoreRegistrar.IN_MOD_LOADING));
        GRIDHANDLERS = SimpleRegistries.newSingleObjectRegistry(ElecCoreRegistrar.IN_MOD_LOADING);
        INFORMATION_PROVIDERS = SimpleRegistries.newSingleObjectRegistry(ElecCoreRegistrar.IN_MOD_LOADING);
        INFORMATION_PROVIDERS_ENTITY = SimpleRegistries.newSingleObjectRegistry(ElecCoreRegistrar.IN_MOD_LOADING);
        WORLD_GEN_HOOKS = (ISingleObjectRegistry<IWorldGenHook>) APIHandler.worldGenManager;
        WORLD_POPULATORS = new ISingleRegister<IAdvancedChunkPopulator>() {

            @Override
            public boolean register(IAdvancedChunkPopulator chunkPopulator) {
                return APIHandler.worldGenManager.register(chunkPopulator);
            }

        };
        WORLD_FEATURE_GENERATORS = new ISingleRegister<IFeatureGenerator>() {

            @Override
            public boolean register(IFeatureGenerator generator) {
                return APIHandler.worldGenManager.register(generator);
            }

        };
    }

    private static final Predicate IN_MOD_LOADING;

    static void dummyLoad(){
    }

}
