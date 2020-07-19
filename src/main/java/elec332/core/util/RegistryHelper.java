package elec332.core.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import elec332.core.api.util.IClearable;
import elec332.core.handler.annotations.TileEntityAnnotationProcessor;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.nbt.INBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.Feature;
import net.minecraftforge.common.ModDimension;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import net.minecraftforge.registries.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 5-4-2016.
 */
public class RegistryHelper {

    /**
     * Registers an empty cabability, meaning that it has no serialization and no default implementation.
     *
     * @param clazz The capability type
     */
    public static <T> void registerEmptyCapability(Class<T> clazz) {
        registerCapability(clazz, new Capability.IStorage<T>() {

            @Override
            public INBT writeNBT(Capability capability, Object instance, Direction side) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void readNBT(Capability capability, Object instance, Direction side, INBT nbt) {
                throw new UnsupportedOperationException();
            }

        }, () -> {
            throw new UnsupportedOperationException();
        });
    }

    public static <T> void registerCapability(Class<T> clazz, Capability.IStorage<T> storage, Callable<? extends T> factory) {
        if (FMLHelper.hasReachedState(ModLoadingStage.COMMON_SETUP)) {
            CapabilityManager.INSTANCE.register(clazz, storage, factory);
        } else {
            if (FMLHelper.getActiveModContainer() != null && !FMLHelper.getActiveModContainer().getModId().equals("minecraft")) {
                FMLHelper.getActiveModEventBus().addListener((Consumer<FMLCommonSetupEvent>) event -> CapabilityManager.INSTANCE.register(clazz, storage, factory));
            } else {
                ModContainer c = FMLHelper.getOwner(clazz);
                if (c instanceof FMLModContainer) {
                    ((FMLModContainer) c).getEventBus().addListener((Consumer<FMLCommonSetupEvent>) event -> CapabilityManager.INSTANCE.register(clazz, storage, factory));
                } else {
                    throw new UnsupportedOperationException();
                }
            }
        }
    }

    public static <C> void registerCapability(AttachCapabilitiesEvent<?> event, ResourceLocation key, Capability<C> capability, C instance) {
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(capability);
        Preconditions.checkNotNull(instance);
        if (instance instanceof INBTSerializable) {
            event.addCapability(key, new ICapabilitySerializable<INBT>() {

                @Override
                public INBT serializeNBT() {
                    return ((INBTSerializable<?>) instance).serializeNBT();
                }

                @Override
                @SuppressWarnings({"unchecked", "rawtypes"})
                public void deserializeNBT(INBT nbt) {
                    ((INBTSerializable) instance).deserializeNBT(nbt);
                }

                @Nonnull
                @Override
                public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
                    return capability.orEmpty(cap, LazyOptional.of(() -> instance));
                }

            });
        } else {
            event.addCapability(key, new ICapabilityProvider() {

                @Nonnull
                @Override
                public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
                    return capability.orEmpty(cap, LazyOptional.of(() -> instance));
                }

            });
        }
        if (instance instanceof IClearable) {
            event.addListener(((IClearable) instance)::clear);
        }
    }

    public static <T extends IForgeRegistryEntry<T>> ForgeRegistry<T> createRegistry(ResourceLocation name, Class<T> type, Consumer<RegistryBuilder<T>> modifier) {
        RegistryBuilder<T> b = new RegistryBuilder<>();
        b.setName(Preconditions.checkNotNull(name));
        b.setType(Preconditions.checkNotNull(type));
        if (modifier != null) {
            modifier.accept(b);
        }
        return (ForgeRegistry<T>) b.create();
    }

    public static <T extends IForgeRegistryEntry<T>, C extends IForgeRegistry.AddCallback<T> & IForgeRegistry.ClearCallback<T> & IForgeRegistry.CreateCallback<T>> ForgeRegistry<T> createRegistry(ResourceLocation registryName, Class<T> registryType, int minId, int maxId, C callback) {
        return (ForgeRegistry<T>) new RegistryBuilder<T>().setName(registryName).addCallback(callback).setType(registryType).setIDRange(minId, maxId).create();
    }

    public static ForgeRegistry<Block> getBlockRegistry() {
        return (ForgeRegistry<Block>) ForgeRegistries.BLOCKS;
    }

    public static ForgeRegistry<Item> getItemRegistry() {
        return (ForgeRegistry<Item>) ForgeRegistries.ITEMS;
    }

    public static ForgeRegistry<Effect> getPotionRegistry() {
        return (ForgeRegistry<Effect>) ForgeRegistries.POTIONS;
    }

    public static ForgeRegistry<Biome> getBiomeRegistry() {
        return (ForgeRegistry<Biome>) ForgeRegistries.BIOMES;
    }

    public static ForgeRegistry<SoundEvent> getSoundEventRegistry() {
        return (ForgeRegistry<SoundEvent>) ForgeRegistries.SOUND_EVENTS;
    }

    public static ForgeRegistry<Potion> getPotionTypesRegistry() {
        return (ForgeRegistry<Potion>) ForgeRegistries.POTION_TYPES;
    }

    public static ForgeRegistry<Enchantment> getEnchantmentRegistry() {
        return (ForgeRegistry<Enchantment>) ForgeRegistries.ENCHANTMENTS;
    }

    public static ForgeRegistry<VillagerProfession> getVillagerRegistry() {
        return (ForgeRegistry<VillagerProfession>) ForgeRegistries.PROFESSIONS;
    }

    public static ForgeRegistry<EntityType<?>> getEntities() {
        return (ForgeRegistry<EntityType<?>>) ForgeRegistries.ENTITIES;
    }

    public static ForgeRegistry<TileEntityType<?>> getTileEntities() {
        return (ForgeRegistry<TileEntityType<?>>) ForgeRegistries.TILE_ENTITIES;
    }

    public static ForgeRegistry<ModDimension> getDimensions() {
        return (ForgeRegistry<ModDimension>) ForgeRegistries.MOD_DIMENSIONS;
    }

    public static ForgeRegistry<DimensionType> getDimensionTypes() {
        return (ForgeRegistry<DimensionType>) RegistryManager.ACTIVE.getRegistry(DimensionType.class);
    }

    public static ForgeRegistry<IRecipeSerializer<?>> getRecipeSerializers() {
        return (ForgeRegistry<IRecipeSerializer<?>>) ForgeRegistries.RECIPE_SERIALIZERS;
    }

    public static ForgeRegistry<ContainerType<?>> getContainers() {
        return (ForgeRegistry<ContainerType<?>>) ForgeRegistries.CONTAINERS;
    }

    public static ForgeRegistry<Feature<?>> getFeatures() {
        return (ForgeRegistry<Feature<?>>) ForgeRegistries.FEATURES;
    }

    public static ForgeRegistry<WorldCarver<?>> getCarvers() {
        return (ForgeRegistry<WorldCarver<?>>) ForgeRegistries.WORLD_CARVERS;
    }

    @SuppressWarnings("unchecked")
    public static <T extends TileEntity> TileEntityType<T> getTileEntityType(Class<T> clazz) {
        Collection<TileEntityType<T>> types = Lists.newArrayList();
        getTileEntities().forEach(type -> {
            TileEntity tile = type.create();
            if (tile != null && tile.getClass() == clazz) {
                types.add((TileEntityType<T>) type);
            }
        });
        if (types.size() == 1) {
            return types.iterator().next();
        }
        throw new UnsupportedOperationException("Multiple registered types");
    }

    @SuppressWarnings("ConstantConditions")
    public static <T extends TileEntity> TileEntityType<T> registerTileEntity(ResourceLocation id, Supplier<T> builder) {
        return registerTileEntity(id, new TileEntityType<T>(builder, null, null) {

            @Override
            public boolean isValidBlock(@Nonnull Block p_223045_1_) {
                return true;
            }

        });
    }

    public static <T extends TileEntity> TileEntityType<T> registerTileEntity(Class<T> clazz, ResourceLocation rl) {
        return TileEntityAnnotationProcessor.registerTileEntity(clazz, rl);
    }

    public static <T extends TileEntity> TileEntityType<T> registerTileEntity(ResourceLocation id, TileEntityType<T> type) {
        GameData.register_impl(type.setRegistryName(id));
        return type;
    }

    public static <T extends IRecipe<?>> void registerUnmodifiableRecipeType(@Nonnull ResourceLocation id, @Nonnull Function<ResourceLocation, T> factory) {
        registerRecipeType(new SpecialRecipeSerializer<>(factory).setRegistryName(id));
    }

    public static <T extends IRecipe<?>> void registerRecipeType(IRecipeSerializer<T> serializer) {
        getRecipeSerializers().register(serializer);
    }

    public static Map<Block, Item> getBlockItemMap() {
        return GameData.getBlockItemMap();
    }

    @SuppressWarnings("deprecation")
    public static Optional<DimensionType> getDimensionType(ResourceLocation name) {
        return Registry.DIMENSION_TYPE.getValue(name);
    }

    /**
     * Callback with all callback types
     */
    public interface FullRegistryCallback<T extends IForgeRegistryEntry<T>> extends IForgeRegistry.AddCallback<T>, IForgeRegistry.ClearCallback<T>, IForgeRegistry.CreateCallback<T>, IForgeRegistry.ValidateCallback<T> {

    }

}
