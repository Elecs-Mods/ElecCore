package elec332.core.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import elec332.core.api.registry.ICraftingManager;
import elec332.core.util.recipes.RecipeHelper;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.registries.*;

/**
 * Created by Elec332 on 5-4-2016.
 */
public class RegistryHelper {

    public static <K extends IForgeRegistryEntry<K>> K register(K object, ResourceLocation name){
        Preconditions.checkNotNull(object);
        Preconditions.checkNotNull(name);
        object.setRegistryName(name);
        return GameData.register_impl(object);
    }

    public static <K extends IForgeRegistryEntry<K>> K register(K object){
        return GameData.register_impl(object);
    }

    public static <T extends IForgeRegistryEntry<T>, C extends IForgeRegistry.AddCallback<T> & IForgeRegistry.ClearCallback<T> & IForgeRegistry.CreateCallback<T>> ForgeRegistry<T> createRegistry(ResourceLocation registryName, Class<T> registryType, C callback){
        return createRegistry(registryName, registryType, 0, Byte.MAX_VALUE, callback);
    }

    @SuppressWarnings("deprecation")
    public static <T extends IForgeRegistryEntry<T>, C extends IForgeRegistry.AddCallback<T> & IForgeRegistry.ClearCallback<T> & IForgeRegistry.CreateCallback<T>> ForgeRegistry<T> createRegistry(ResourceLocation registryName, Class<T> registryType, int minId, int maxId, C callback){
        return (ForgeRegistry<T>) new RegistryBuilder<T>().setName(registryName).addCallback(callback).setType(registryType).setIDRange(minId, maxId).create();
    }

    public static ForgeRegistry<Block> getBlockRegistry() {
        return (ForgeRegistry<Block>) ForgeRegistries.BLOCKS;
    }

    public static ForgeRegistry<Item> getItemRegistry() {
        return (ForgeRegistry<Item>)ForgeRegistries.ITEMS;
    }

    public static ForgeRegistry<Potion> getPotionRegistry() {
        return (ForgeRegistry<Potion>)ForgeRegistries.POTIONS;
    }

    public static ForgeRegistry<Biome> getBiomeRegistry() {
        return (ForgeRegistry<Biome>)ForgeRegistries.BIOMES;
    }

    public static ForgeRegistry<SoundEvent> getSoundEventRegistry() {
        return (ForgeRegistry<SoundEvent>)ForgeRegistries.SOUND_EVENTS;
    }

    public static ForgeRegistry<PotionType> getPotionTypesRegistry() {
        return (ForgeRegistry<PotionType>)ForgeRegistries.POTION_TYPES;
    }

    public static ForgeRegistry<Enchantment> getEnchantmentRegistry() {
        return (ForgeRegistry<Enchantment>)ForgeRegistries.ENCHANTMENTS;
    }

    public static ForgeRegistry<VillagerRegistry.VillagerProfession> getVillagerRegistry(){
        return (ForgeRegistry<VillagerRegistry.VillagerProfession>) ForgeRegistries.VILLAGER_PROFESSIONS;
    }

    public static BiMap<Block, Item> getBlockItemMap() {
        return GameData.getBlockItemMap();
    }

    public static ICraftingManager getCraftingManager() {
        return RecipeHelper.getCraftingManager();
    }

    //Callback helpers

    public interface FullRegistryCallback<T extends IForgeRegistryEntry<T>> extends IForgeRegistry.AddCallback<T>, IForgeRegistry.ClearCallback<T>, IForgeRegistry.CreateCallback<T> {

    }

    @SuppressWarnings("unchecked")
    public static <T extends IForgeRegistryEntry<T>> FullRegistryCallback<T> getNullCallback(){
        return (FullRegistryCallback<T>) NULL_CALLBACK;
    }

    private static final FullRegistryCallback NULL_CALLBACK;

    static {
        NULL_CALLBACK = new FullRegistryCallback() {

            @Override
            public void onCreate(IForgeRegistryInternal owner, RegistryManager stage) {
            }

            @Override
            public void onClear(IForgeRegistryInternal owner, RegistryManager stage) {
            }

            @Override
            public void onAdd(IForgeRegistryInternal owner, RegistryManager stage, int id, IForgeRegistryEntry obj) {
            }

        };
    }

}
