package elec332.core.util;

import com.google.common.base.Preconditions;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.RecipeSerializers;
import net.minecraft.nbt.INBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.ModDimension;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.registries.*;

import javax.annotation.Nonnull;
import java.util.Map;
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
        CapabilityManager.INSTANCE.register(clazz, new Capability.IStorage<T>() {

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

    public static <T extends IForgeRegistryEntry<T>> ForgeRegistry<T> createRegistry(ResourceLocation name, Class<T> type, Consumer<RegistryBuilder<T>> modifier) {
        RegistryBuilder<T> b = new RegistryBuilder<>();
        b.setName(Preconditions.checkNotNull(name));
        b.setType(Preconditions.checkNotNull(type));
        if (modifier != null) {
            modifier.accept(b);
        }
        return (ForgeRegistry<T>) b.create();
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

    public static <T extends TileEntity> TileEntityType<T> registerTileEntity(ResourceLocation id, Supplier<T> builder) {
        return registerTileEntity(id, new TileEntityType<T>(builder, null, null) {

            @Override
            public boolean isValidBlock(@Nonnull Block p_223045_1_) {
                return true;
            }

        });
    }

    public static <T extends TileEntity> TileEntityType<T> registerTileEntity(ResourceLocation id, TileEntityType<T> type) {
        GameData.register_impl(type.setRegistryName(id));
        return type;
    }

    public static <T extends IRecipe> void registerUnmodifiableRecipeType(@Nonnull ResourceLocation id, @Nonnull Function<ResourceLocation, T> factory) {
        registerRecipeType(new RecipeSerializers.SimpleSerializer<>(id.toString(), factory));
    }

    public static <T extends IRecipe> void registerRecipeType(IRecipeSerializer<T> serializer) {
        RecipeSerializers.register(serializer);
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
