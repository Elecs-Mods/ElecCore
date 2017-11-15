package elec332.core.api.registration;

import net.minecraft.item.Item;

/**
 * Created by Elec332 on 12-10-2017.
 */
public interface IItemRegister extends IObjectRegister<Item> {

    @Override
    default public Class<Item> getType(){
        return Item.class;
    }

}
