package elec332.core.util;

import com.google.common.collect.BiMap;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.*;

import java.util.Map;

/**
 * Created by Elec332 on 5-4-2016.
 */
public class RegistryHelper {

    public static <T extends IForgeRegistryEntry<T>, C extends IForgeRegistry.AddCallback<T> & IForgeRegistry.ClearCallback<T> & IForgeRegistry.CreateCallback<T> & IForgeRegistry.SubstitutionCallback<T>> FMLControlledNamespacedRegistry<T> createRegistry(ResourceLocation registryName, Class<T> registryType, C callback){
        return createRegistry(registryName, registryType, 0, Byte.MAX_VALUE, callback);
    }

    @SuppressWarnings("deprecation")
    public static <T extends IForgeRegistryEntry<T>, C extends IForgeRegistry.AddCallback<T> & IForgeRegistry.ClearCallback<T> & IForgeRegistry.CreateCallback<T> & IForgeRegistry.SubstitutionCallback<T>> FMLControlledNamespacedRegistry<T> createRegistry(ResourceLocation registryName, Class<T> registryType, int minId, int maxId, C callback){
        return PersistentRegistryManager.createRegistry(registryName, registryType, null, minId, maxId, true, callback, callback, callback, callback);
    }

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

    public static FMLControlledNamespacedRegistry<VillagerRegistry.VillagerProfession> getVillagerRegistry(){
        return (FMLControlledNamespacedRegistry<VillagerRegistry.VillagerProfession>) VillagerRegistry.instance().getRegistry();
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

    //Callback helpers

    public interface FullRegistryCallback<T extends IForgeRegistryEntry<T>> extends IForgeRegistry.AddCallback<T>, IForgeRegistry.ClearCallback<T>, IForgeRegistry.CreateCallback<T>, IForgeRegistry.SubstitutionCallback<T> {

    }

    @SuppressWarnings("unchecked")
    public static <T extends IForgeRegistryEntry<T>> FullRegistryCallback<T> getNullCallback(){
        return (FullRegistryCallback<T>) NULL_CALLBACK;
    }

    private static final FullRegistryCallback NULL_CALLBACK;

    static {
        NULL_CALLBACK = new FullRegistryCallback() {

            @Override
            public void onCreate(Map slaveset, BiMap registries) {
            }

            @Override
            public void onClear(IForgeRegistry is, Map slaveset) {
            }

            @Override
            public void onAdd(IForgeRegistryEntry obj, int id, Map slaveset) {
            }

            @Override
            public void onSubstituteActivated(Map slaveset, IForgeRegistryEntry original, IForgeRegistryEntry replacement, ResourceLocation name) {
            }

        };
    }

}
