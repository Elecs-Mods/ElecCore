package elec332.abstraction.handlers;

import elec332.core.abstraction.abstracted.item.IAbstractedItem;
import net.minecraft.item.Item;

/**
 * Created by Elec332 on 26-1-2017.
 */
public interface IAbstractedClassProvider {

    public <I extends Item & IAbstractedItem> Class<I> getAbstractedItemAbstraction();

}
