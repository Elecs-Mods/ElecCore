package elec332.core.compat.forestry.bee;

import com.google.common.collect.Maps;
import elec332.core.compat.forestry.ForestryCompatHandler;
import elec332.core.tile.AbstractBlock;
import forestry.api.apiculture.hives.HiveManager;
import forestry.api.apiculture.hives.IHiveDescription;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

/**
 * Created by Elec332 on 20-8-2016.
 */
public abstract class BlockHive<T extends Enum<T> & IHiveEnum> extends AbstractBlock {

    public BlockHive(){
        this(null);
    }

    public BlockHive(T defaultT) {
        super(new HiveMaterial());
        setLightLevel(0.4f);
        setHardness(2.5f);
        metaToObject = Maps.newHashMap();
        for (T t : getHiveTypes().getEnumConstants()){
            metaToObject.put(t.getMeta(), t);
            for (IHiveDescription desc : t.getHiveDescriptions()) {
                HiveManager.hiveRegistry.registerHive(t.getUid(), desc);
            }
        }
        if (defaultT == null){
            defaultT = getHiveTypes().getEnumConstants()[0];
        }
        this.defaultT = defaultT;
        setDefaultState(this.blockState.getBaseState().withProperty(PROPERTY, defaultT));
        setCreativeTab(ForestryCompatHandler.getForestryBeeTab());
        for (int i = 0; i < 4; i++) {
            setHarvestLevel("scoop", 0);
        }
    }

    public BlockHive<T> register(@Nonnull ResourceLocation rl){
        setRegistryName(rl);
        GameRegistry.register(new ItemBlock(this){

            private final String unlName = "tile." + rl.toString().replace(":", ".") + ".";

            @Override
            public int getMetadata(int damage) {
                T t = metaToObject.get(damage);
                if (t == null){
                    damage = defaultT.getMeta();
                }
                return damage;
            }

            @Override
            @Nonnull
            public String getUnlocalizedName(ItemStack stack) {
                return unlName + metaToObject.get(getMetadata(stack.getItemDamage())).getName();
            }

        }.setHasSubtypes(true), rl);
        return GameRegistry.register(this);
    }

    @Nonnull
    public abstract Class<T> getHiveTypes();

    @Nonnull
    public PropertyEnum<T> getProperty(){
        if (PROPERTY == null){
            PROPERTY = PropertyEnum.create("hivetype", getHiveTypes());
        }
        return PROPERTY;
    }

    private PropertyEnum<T> PROPERTY;

    @Nonnull
    private final T defaultT;
    private Map<Integer, T> metaToObject;

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, getProperty());
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(PROPERTY).getMeta();
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta) {
        T t = metaToObject.get(meta);
        if (t == null){
            t = defaultT;
        }
        return getStateFromHive(t);
    }

    @Nonnull
    public IBlockState getStateFromHive(@Nonnull T t){
        return getDefaultState().withProperty(PROPERTY, t);
    }

    @Override
    @Nonnull
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, @Nonnull IBlockState state, int fortune) {
        return state.getValue(PROPERTY).getDrops(world, pos, state, fortune);
    }

    @Override
    public void getSubBlocksC(@Nonnull Item item, List<ItemStack> subBlocks, CreativeTabs creativeTab) {
        for (T t : metaToObject.values()){
            if (t.showInTab()) {
                subBlocks.add(new ItemStack(this, 1, t.getMeta()));
            }
        }
    }

    @Override
    public int getLightValue(@Nonnull IBlockState state, IBlockAccess world, @Nonnull BlockPos pos) {
        return state.getValue(PROPERTY).getLight();
    }

    private static class HiveMaterial extends Material {

        private HiveMaterial() {
            super(MapColor.STONE);
            setRequiresTool();
            setImmovableMobility();
        }

    }

}
