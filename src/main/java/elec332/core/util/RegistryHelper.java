package elec332.core.util;

import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.nbt.INBTBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.registries.*;

import java.util.Map;

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
        CapabilityManager.INSTANCE.register(clazz, new Capability.IStorage<T>() {

            @Override
            public INBTBase writeNBT(Capability capability, Object instance, EnumFacing side) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void readNBT(Capability capability, Object instance, EnumFacing side, INBTBase nbt) {
                throw new UnsupportedOperationException();
            }

        }, () -> {
            throw new UnsupportedOperationException();
        });
    }

    public static <T extends IForgeRegistryEntry<T>, C extends IForgeRegistry.AddCallback<T> & IForgeRegistry.ClearCallback<T> & IForgeRegistry.CreateCallback<T>> ForgeRegistry<T> createRegistry(ResourceLocation registryName, Class<T> registryType, C callback) {
        return createRegistry(registryName, registryType, 0, Byte.MAX_VALUE, callback);
    }

    @SuppressWarnings("deprecation")
    public static <T extends IForgeRegistryEntry<T>, C extends IForgeRegistry.AddCallback<T> & IForgeRegistry.ClearCallback<T> & IForgeRegistry.CreateCallback<T>> ForgeRegistry<T> createRegistry(ResourceLocation registryName, Class<T> registryType, int minId, int maxId, C callback) {
        return (ForgeRegistry<T>) new RegistryBuilder<T>().setName(registryName).addCallback(callback).setType(registryType).setIDRange(minId, maxId).create();
    }

    public static ForgeRegistry<Block> getBlockRegistry() {
        return (ForgeRegistry<Block>) ForgeRegistries.BLOCKS;
    }

    public static ForgeRegistry<Item> getItemRegistry() {
        return (ForgeRegistry<Item>) ForgeRegistries.ITEMS;
    }

    public static ForgeRegistry<Potion> getPotionRegistry() {
        return (ForgeRegistry<Potion>) ForgeRegistries.POTIONS;
    }

    public static ForgeRegistry<Biome> getBiomeRegistry() {
        return (ForgeRegistry<Biome>) ForgeRegistries.BIOMES;
    }

    public static ForgeRegistry<SoundEvent> getSoundEventRegistry() {
        return (ForgeRegistry<SoundEvent>) ForgeRegistries.SOUND_EVENTS;
    }

    public static ForgeRegistry<PotionType> getPotionTypesRegistry() {
        return (ForgeRegistry<PotionType>) ForgeRegistries.POTION_TYPES;
    }

    public static ForgeRegistry<Enchantment> getEnchantmentRegistry() {
        return (ForgeRegistry<Enchantment>) ForgeRegistries.ENCHANTMENTS;
    }

    public static ForgeRegistry<VillagerRegistry.VillagerProfession> getVillagerRegistry() {
        return (ForgeRegistry<VillagerRegistry.VillagerProfession>) ForgeRegistries.VILLAGER_PROFESSIONS;
    }
/*  //TODO: Wait until forge re-adds this
    public static ForgeRegistry<IRecipe> getRecipes() {
        return (ForgeRegistry<IRecipe>) ForgeRegistries.RECIPES;
    }*/

    public static ForgeRegistry<EntityType<?>> getEntities() {
        return (ForgeRegistry<EntityType<?>>) ForgeRegistries.ENTITIES;
    }

    public static Map<Block, Item> getBlockItemMap() {
        return GameData.getBlockItemMap();
    }

    /**
     * Callback with all callback types
     */
    public interface FullRegistryCallback<T extends IForgeRegistryEntry<T>> extends IForgeRegistry.AddCallback<T>, IForgeRegistry.ClearCallback<T>, IForgeRegistry.CreateCallback<T>, IForgeRegistry.ValidateCallback<T> {

    }

}
