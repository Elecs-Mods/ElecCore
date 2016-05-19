package elec332.core.util;

import com.google.common.collect.BiMap;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;
import net.minecraftforge.fml.common.registry.GameData;

/**
 * Created by Elec332 on 5-4-2016.
 */
public class RegistryHelper {

    public static FMLControlledNamespacedRegistry<Block> getBlockRegistry() {
        return (FMLControlledNamespacedRegistry<Block>)Block.REGISTRY;
    }

    public static FMLControlledNamespacedRegistry<Item> getItemRegistry() {
        return (FMLControlledNamespacedRegistry<Item>)Item.REGISTRY;
    }

    public static FMLControlledNamespacedRegistry<Potion> getPotionRegistry() {
        return (FMLControlledNamespacedRegistry<Potion>)Potion.REGISTRY;
    }

    public static FMLControlledNamespacedRegistry<Biome> getBiomeRegistry() {
        return (FMLControlledNamespacedRegistry<Biome>)Biome.REGISTRY;
    }

    public static FMLControlledNamespacedRegistry<SoundEvent> getSoundEventRegistry() {
        return (FMLControlledNamespacedRegistry<SoundEvent>)SoundEvent.REGISTRY;
    }

    public static FMLControlledNamespacedRegistry<PotionType> getPotionTypesRegistry() {
        return (FMLControlledNamespacedRegistry<PotionType>)PotionType.REGISTRY;
    }

    public static FMLControlledNamespacedRegistry<Enchantment> getEnchantmentRegistry() {
        return (FMLControlledNamespacedRegistry<Enchantment>)Enchantment.REGISTRY;
    }

    public static BiMap<Block, Item> getBlockItemMap() {
        return GameData.getBlockItemMap();
    }

    public static CraftingManager getCraftingManager() {
        return CraftingManager.getInstance();
    }

    public static FurnaceRecipes getFurnaceRecipes() {
        return FurnaceRecipes.instance();
    }

}
