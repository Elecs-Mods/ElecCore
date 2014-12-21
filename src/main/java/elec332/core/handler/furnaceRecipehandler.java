package elec332.core.handler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

//import cpw.mods.fml.common.Loader;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Created by Elec332 on 21-12-2014.
 */
public class furnaceRecipehandler {

    //99% copied from vanilla

    private Map smeltingList = new HashMap();
    private Map experienceList = new HashMap();
    public static furnaceRecipehandler smelting()
    {
        return smeltingBase;
    }
    private static final furnaceRecipehandler smeltingBase = new furnaceRecipehandler();

    private furnaceRecipehandler(){
        //if(Loader.isModLoaded(modInfo.MODID_FURNACE)){
        //    this.func_151393_a(Blocks.iron_ore, new ItemStack(JFurnace.ironNugget), 0F);
        //    this.func_151393_a(Blocks.gold_ore, new ItemStack(Items.gold_nugget), 0F);
        //}
    }

    public void func_151393_a(Block p_151393_1_, ItemStack p_151393_2_, float p_151393_3_)
    {
        this.func_151396_a(Item.getItemFromBlock(p_151393_1_), p_151393_2_, p_151393_3_);
    }

    public void func_151396_a(Item p_151396_1_, ItemStack p_151396_2_, float p_151396_3_)
    {
        this.func_151394_a(new ItemStack(p_151396_1_, 1, 32767), p_151396_2_, p_151396_3_);
    }

    public void func_151394_a(ItemStack p_151394_1_, ItemStack p_151394_2_, float p_151394_3_)
    {
        this.smeltingList.put(p_151394_1_, p_151394_2_);
        this.experienceList.put(p_151394_2_, Float.valueOf(p_151394_3_));
    }

    public ItemStack getSmeltingResult(ItemStack p_151395_1_)
    {
        Iterator iterator = this.smeltingList.entrySet().iterator();
        Map.Entry entry;

        do
        {
            if (!iterator.hasNext())
            {
                return null;
            }

            entry = (Map.Entry)iterator.next();
        }
        while (!this.func_151397_a(p_151395_1_, (ItemStack)entry.getKey()));

        return (ItemStack)entry.getValue();
    }

    private boolean func_151397_a(ItemStack p_151397_1_, ItemStack p_151397_2_)
    {
        return p_151397_2_.getItem() == p_151397_1_.getItem() && (p_151397_2_.getItemDamage() == 32767 || p_151397_2_.getItemDamage() == p_151397_1_.getItemDamage());
    }

}
