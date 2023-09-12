package elec332.core.util;

import com.google.common.collect.Lists;
import elec332.core.handler.annotations.TileEntityAnnotationProcessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraftforge.common.world.ForgeWorldPreset;
import net.minecraftforge.registries.*;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 5-4-2016.
 */
public class RegistryHelper {

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

    public static ForgeRegistry<MobEffect> getPotionEffectsRegistry() {
        return (ForgeRegistry<MobEffect>) ForgeRegistries.MOB_EFFECTS;
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

    public static ForgeRegistry<BlockEntityType<?>> getTileEntities() {
        return (ForgeRegistry<BlockEntityType<?>>) ForgeRegistries.BLOCK_ENTITIES;
    }

    public static ForgeRegistry<ForgeWorldPreset> getDimensionTypes() {
        return (ForgeRegistry<ForgeWorldPreset>) ForgeRegistries.WORLD_TYPES.get();
    }

    public static ForgeRegistry<RecipeSerializer<?>> getRecipeSerializers() {
        return (ForgeRegistry<RecipeSerializer<?>>) ForgeRegistries.RECIPE_SERIALIZERS;
    }

    public static ForgeRegistry<MenuType<?>> getContainers() {
        return (ForgeRegistry<MenuType<?>>) ForgeRegistries.CONTAINERS;
    }

    public static ForgeRegistry<Feature<?>> getFeatures() {
        return (ForgeRegistry<Feature<?>>) ForgeRegistries.FEATURES;
    }

    @SuppressWarnings("unchecked")
    public static <T extends BlockEntity> BlockEntityType<T> getTileEntityType(Class<T> clazz) {
        Collection<BlockEntityType<T>> types = Lists.newArrayList();
        getTileEntities().forEach(type -> {
            BlockEntity tile = type.create(BlockPos.ZERO, Blocks.AIR.defaultBlockState());
            if (tile != null && tile.getClass() == clazz) {
                types.add((BlockEntityType<T>) type);
            }
        });
        if (types.size() == 1) {
            return types.iterator().next();
        }
        throw new UnsupportedOperationException("Multiple registered types");
    }

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
