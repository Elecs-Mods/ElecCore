package elec332.core.util;

import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.village.MerchantRecipeList;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * Created by Elec332 on 26-11-2016.
 */
public interface IElecTradeList {

    public void modifyMerchantRecipeList(IMerchant merchant, @Nonnull MerchantRecipeList tradeList, @Nonnull Random random);

    @SuppressWarnings("all")
    @Nonnull
    public static EntityVillager.ITradeList wrap(final IElecTradeList tradeList){
        return wrap((Object) tradeList);
    }

    @SuppressWarnings("all")
    @Nonnull
    static EntityVillager.ITradeList wrap(final Object tradeList){
        return ((IElecTradeList)tradeList)::modifyMerchantRecipeList;
    }

}
