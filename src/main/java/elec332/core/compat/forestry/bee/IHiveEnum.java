package elec332.core.compat.forestry.bee;

import com.google.common.collect.Lists;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IHiveDrop;
import forestry.api.apiculture.hives.HiveManager;
import forestry.api.apiculture.hives.IHiveDescription;
import forestry.apiculture.worldgen.HiveRegistry;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;


/**
 * Created by Elec332 on 20-8-2016.
 */
public interface IHiveEnum extends IStringSerializable {

    @Nonnull
    default public List<IHiveDescription> getHiveDescriptions(){
        return Lists.newArrayList(getHiveDescription());
    }

    @Nonnull
    public IHiveDescription getHiveDescription();

    public int getMeta();

    public String getUid(IHiveDescription description);

    default public boolean showInTab(){
        return true;
    }

    default public int getLight(){
        return 0;
    }

    @Override
    @Nonnull
    default public String getName() {
        return toString().toLowerCase(Locale.ENGLISH);
    }

    default public void addDrop(IHiveDrop... drops){
        HiveManager.hiveRegistry.addDrops(getUid(getHiveDescription()), drops);
    }

    @Nonnull
    default public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune){
        List<ItemStack> ret = Lists.newArrayList();
        Random random = world instanceof World ? ((World) world).rand : new Random(pos.toLong());

        List<IHiveDrop> hiveDrops = ((HiveRegistry)HiveManager.hiveRegistry).getDrops(getUid(getHiveDescription()));
        Collections.shuffle(hiveDrops);

        int tries = 0;
        boolean hasPrincess = false;
        while (tries <= 10 && !hasPrincess) {
            tries++;
            for (IHiveDrop drop : hiveDrops) {
                if (random.nextDouble() < drop.getChance(world, pos, fortune)) {
                    IBee bee = drop.getBeeType(world, pos);
                    if (random.nextFloat() < drop.getIgnobleChance(world, pos, fortune)) {
                        bee.setIsNatural(false);
                    }

                    ItemStack princess = BeeManager.beeRoot.getMemberStack(bee, EnumBeeType.PRINCESS);
                    ret.add(princess);
                    hasPrincess = true;
                    break;
                }
            }
        }

        for (IHiveDrop drop : hiveDrops) {
            if (random.nextDouble() < drop.getChance(world, pos, fortune)) {
                IBee bee = drop.getBeeType(world, pos);
                ItemStack drone = BeeManager.beeRoot.getMemberStack(bee, EnumBeeType.DRONE);
                ret.add(drone);
                break;
            }
        }

        for (IHiveDrop drop : hiveDrops) {
            if (random.nextDouble() < drop.getChance(world, pos, fortune)) {
                ret.addAll(drop.getExtraItems(world, pos, fortune));
                break;
            }
        }

        return ret;
    }

}
